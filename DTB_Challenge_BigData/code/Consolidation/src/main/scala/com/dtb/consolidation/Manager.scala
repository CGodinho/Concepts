package com.dtb.consolidation

import java.net.URL
import scala.io.Source
import java.util.Calendar
import org.apache.spark.rdd.RDD
import java.text.SimpleDateFormat
import java.net.HttpURLConnection
import com.dtb.consolidation.data._
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.{SparkConf, SparkContext}
import com.dtb.consolidation.fs.{Hdfs, Local, Storage}


/**
  * @author CGodinho
  *
  * Management object where main class, spark / HDFS resources and a few utility functions are provided.
  * Manager implements the main flow of execution.
  */
object Manager {

  /**
    * Setups a SparkConf object to be used in Spark initialization.
    *
    * @return - SparkConf object initialized
    */
    private def getSparkConf : SparkConf = {

        val config = new SparkConf
        config.setAppName(Constants.ApplicationName)
            .set("spark.driver.maxResultSize", "3g")
            .set("spark.kryoserializer.buffer", "128")
            .set("spark.kryo.registrationRequired", "false")
                .registerKryoClasses(Array(classOf[Drug],
                                           classOf[Array[Drug]],
                                           classOf[Physician],
                                           classOf[Array[Physician]],
                                           classOf[Prescription],
                                           classOf[Array[Prescription]],
                                           classOf[Entity],
                                           classOf[Array[Entity]],
                                           classOf[Array[String]],
                                           classOf[org.apache.spark.internal.io.FileCommitProtocol.TaskCommitMessage]))
        config
    }

  /**
    * Returns a date representation in a string. The format is yyyy-MM-dd_HH_mm_ss_SSS
    *
    * @return - Date as string
    */
    private def getTimeString() : String = {

        val nowTime = Calendar.getInstance().getTime()
        val formater = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS")
        formater.format(nowTime)
    }

    /**
     * Logs messages into the message list and prints to the output console.
     * The message list is represented by RDD[String].
     *
     * @param sc - spark context reference
     * @param messages - list of message to be printed
     * @param message - a new message item
     * @return - message item appended to the list of messages
     */
    private def logRDD(sc : SparkContext, messages: RDD[String], message : String) : RDD[String] = {

        val nowTime = Calendar.getInstance().getTime()
        val formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
        val nowString = formater.format(nowTime)
        val timeMessage = nowString + " - " + message
        println(timeMessage)
        messages.union(sc.parallelize(List(timeMessage)))
  }

    /**
     * Checks if the path starts by HTTP(S):, and therfore is an URL.
     *
     * @param path - File path or URL
     *
     * @return - Boolean: true is URL, false otherwise
     */
    private def isWebURL(urlPath : String) : Boolean = if (urlPath.trim.toLowerCase.matches("^http(|s):.*")) true else false

    /**
     * In case of an HTTP(S) location, returns the last element in the path, which is the file name.
     *
     * @param urlPath - File path or URL
     *
     * @return - File name
     */
    private def reuseFileName(urlPath : String) : String = if (isWebURL(urlPath)) urlPath.substring(urlPath.lastIndexOf("/")) else urlPath

    /**
     * Checks if an URL ends in a valid file.
     *
     * @param path - File path or URL
     *
     * @return - Boolean: true is valid, false otherwise
     */
    private def isWebFile(urlPath : String) = {

        val url = new URL(urlPath)
        val code = url.openConnection.asInstanceOf[HttpURLConnection]
        val response = code.getResponseCode
        if (response == Constants.HTTP_OK) true else false
    }

    /**
     * Loads a file available in an URL into and RDD[String].
     *
     * @param path - File path or URL
     * @return - Boolean: true is valid, false otherwise
     */
    private def webFile(sc : SparkContext, url : String) : RDD[(String, Long)] = {

        var counter: Long = 0
        val buffer = ArrayBuffer.empty[(String, Long)]

        for (line <- Source.fromURL(url).getLines) {
          buffer.append((line, counter))
          counter = counter + 1
        }
        sc.parallelize(buffer, Constants.RDDPartitions)
    }

    /**
     * Validates the received arguments and produces an IllegalArgument Exception in case of error.
     * The # of arguments is validated before call.
     * In case of an URL in input file, it checks for file availabilty in the HTTP URL.
     *
     * @param storage - reference to a file system
     * @param args - application arguments
     */
    private def validateArgs(storage: Storage, args: Array[String]) {

        val basePath = s"${storage.getUri}/${args(1)}"
        if (!storage.isLocation(basePath)) throw new IllegalArgumentException(s"Invalid base path [${basePath}]!")

        if (isWebURL(args(2))) {
            if (!isWebFile(args(2))) throw new IllegalArgumentException(s"Invalid Drug URL [${args(2)}]!")
        }
        else {
            val drugFile = s"${storage.getUri}/${args(1)}/${Constants.InputBaseLocation}/${args(2)}"
            if (!storage.isLocation(drugFile)) throw new IllegalArgumentException(s"Invalid Drug file [${drugFile}]!")
        }

        if (isWebURL(args(3))) {
            if (!isWebFile(args(3))) throw new IllegalArgumentException(s"Invalid Physician URL [${args(3)}]!")
        }
        else {
            val physicianFile = s"${storage.getUri}/${args(1)}/${Constants.InputBaseLocation}/${args(3)}"
            if (!storage.isLocation(physicianFile)) throw new IllegalArgumentException(s"Invalid Physician file [${physicianFile}]!")
        }

        if (isWebURL(args(4))) {
            if (!isWebFile(args(4))) throw new IllegalArgumentException(s"Invalid Prescription URL [${args(4)}]!")
        }
        else {
            val prescriptionFile = s"${storage.getUri}/${args(1)}/${Constants.InputBaseLocation}/${args(4)}"
            if (!storage.isLocation(prescriptionFile)) throw new IllegalArgumentException(s"Invalid Prescription file [${prescriptionFile}]!")
        }

        val outputPath = s"${storage.getUri}/${args(1)}/${Constants.OutputBaseLocation}"
        if (!storage.isLocation(outputPath)) throw new IllegalArgumentException(s"Invalid output path [${outputPath}]!")
    }


    /**
     * Generic function to parse a file for a specified entity passed as a generic parameter.
     * Elements not validated by the entity validation reg ex are removed and logged.
     * Duplicated elements are removed and logged.
     *
     * @param sc - reference to Spark Context;
     * @param hdfs - reference to HDFS;
     * @param hadoopBasePath - HADOOP base path for application;
     * @param file - input file path;
     * @param args - application arguments;
     * @return - returned data as RDD of theentity registered.
     */
    private def parseWithValidationNoRepetition[A <: Entity](sc : SparkContext,  storage : Storage,  hadoopBasePath : String, file : String) (implicit entity: EntityCompanion[A]) : RDD[Entity] =  {

        // Read all lines and add an id for line identification
        val raw = if (isWebURL(file))
            webFile(sc, file)
        else
            sc.textFile(s"${storage.getUri}/${hadoopBasePath}/${Constants.InputBaseLocation}/${file}", Constants.RDDPartitions).zipWithIndex

        // Validate which lines are correct and tag them
        val validated = raw.map{x =>
            if ((Field.count(entity.separator, entity.quote, x._1) == entity.size) && (entity.validate.pattern.matcher(x._1).matches))
                (Constants.ResultOk, x._1, x._2, "")
            else
                (Constants.ResultError, x._1, x._2, s"Invalid record [${x._1}] at line ${x._2 + 1}")
        }

        val ok    = validated.filter(_._1 == Constants.ResultOk).map(x => (x._2, x._3))
        val error = validated.filter(_._1 == Constants.ResultError).map(_._4)
        if (error.collect.length > 0)
          storage.writeRDD2File(s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}/${reuseFileName(file)}.error",
                                s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}", error)

        // Find duplicated keys and clean up
        val keysWithLines = ok.map{x => val fields = Field.tokens(entity.separator, entity.quote, x._1)
                                        (entity.key(x._1, fields), x._2)}

        val keysWithLinesGrouped = keysWithLines.map(x => (x._1, (1, (x._2 + 1).toString))).reduceByKey((a, b) => (a._1 + b._1, a._2 + "-" + b._2))
        val keysOk    = keysWithLinesGrouped.filter(_._2._1 == 1)
        val keysError = keysWithLinesGrouped.filter(_._2._1 > 1)
        val keysErrorFlatten = keysError.map(error => s"Repeated key [${error._1}] times [${error._2._1}] in lines [${error._2._2}]")
        if (keysErrorFlatten.collect.length > 0)
          storage.writeRDD2File(s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}/${reuseFileName(file)}.dup",
                                s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}", keysErrorFlatten)

        val keyValue = ok.map{x => val fields = Field.tokens(entity.separator, entity.quote, x._1)
                                   (entity.key(x._1, fields), (x._1, fields))}
        keyValue.join(keysOk).map(entry => entry._2._1).map(x =>  entity.factory(x._1, x._2))
  }

    /**
     * Generic function to parse a file for a specified entity passed as a generic parameter, with unique duplicates.
     * Elements not validated by the entity validation reg ex are removed and logged.
     * Duplicated elements are processed and a unique instance returned.
     *
     * @param sc - reference to Spark Context;
     * @param  - reference to a file system;
     * @param hadoopBasePath - HADOOP base path for application;
     * @param file - input file path;
     * @param args - application arguments;
     * @return - returned data as RDD of the entity registered.
     */
    private def parseWithValidationUniqueRepetition[A <: Entity](sc : SparkContext, storage: Storage, hadoopBasePath : String, file : String) (implicit entity: EntityCompanion[A]) : RDD[Entity] = {

        // Read all lines and add an id for line identification
        val raw = if (isWebURL(file))
            webFile(sc, file)
        else
            sc.textFile(s"${storage.getUri}/${hadoopBasePath}/${Constants.InputBaseLocation}/${file}", Constants.RDDPartitions).zipWithIndex

        // Validate which lines are correct and tag them
        val validated = raw.map { x =>
            if ((Field.count(entity.separator, entity.quote, x._1) == entity.size) && (entity.validate.pattern.matcher(x._1).matches))
                (Constants.ResultOk, x._1, x._2, "")
            else
                (Constants.ResultError, x._1, x._2, s"Invalid record [${x._1}] at line ${x._2 + 1}")
        }
        val ok = validated.filter(_._1 == Constants.ResultOk).map(_._2)
        val error = validated.filter(_._1 == Constants.ResultError).map(_._4)
        if (error.collect.length > 0)
            storage.writeRDD2File(s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}/${reuseFileName(file)}.error",
                                  s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}", error)

        // Find duplicated keys and keep 1
        val keysWithLines = ok.map (x => (entity.key(x,  Field.tokens(entity.separator, entity.quote, x)), x))
        val linesUnique = keysWithLines.reduceByKey((a, b) => a).map(_._2)
        linesUnique.map (x =>  entity.factory(x, Field.tokens(entity.separator, entity.quote, x)))
    }

    /**
     * Generates the final consolidated data set from the 3 original datasets.
     *
     * @param sc - reference to Spark Context;
     * @param storage - reference to a file system;
     * @param drugs - Parsed drugs dataset in RDD[Drugs];
     * @param physicians - Parsed Physician dataset with RDD[Physician];
     * @param prescriptions- Parsed Prescription dataset with RDD[Prescription];
     * @return - returned data as RDD[String]
     */
    private def generateConsolidation(sc : SparkContext, storage : Storage, broadcastMapDrugs: Broadcast[Map[(String, String), String]], physicians : RDD[Physician], prescriptions : RDD[Prescription]) : RDD[String] = {

        val physiciansKeyValue = physicians.map(x => (x.npi, x.medicalSchool))
        val prescriptionsNpiKeyValue = prescriptions.map(x => (x.npi, x))
        val prescriptionsWithPhysicians = prescriptionsNpiKeyValue.join(physiciansKeyValue)

        val mapDrugs = broadcastMapDrugs.value
        val consolidatedRaw = prescriptionsWithPhysicians.map{x =>
        val drugData = mapDrugs.getOrElse((x._2._1.simpleName, x._2._1.genericName), "_")
        s"${x._2._1.npi},${x._2._1.simpleName},${x._2._1.genericName},${x._2._1.beneCount},${x._2._1.totalClaimCount},${x._2._2},${drugData}"}
        // If record ends with "_", no Drug was match in Map
        sc.parallelize(Seq(Constants.HeaderConsolidation)).union(consolidatedRaw.filter(! _.endsWith("_")))
    }

    /**
     * Application entry point and flow management.
     *
     * @param args
     */
    def main(args: Array[String]): Unit = {

        // Check args
        if (args.length != Constants.ApplicationArgs) throw new IllegalArgumentException(s"Incorrect number of args. Received [${args.length}], expected [${Constants.ApplicationArgs}]!")

        val config = getSparkConf
        if (args(0).equalsIgnoreCase(Constants.LocalFS)) config.setMaster("local[*]")
        val sc = new SparkContext(config)
        val storage : Storage = if (args(0).equalsIgnoreCase(Constants.LocalFS)) new Local() else new Hdfs()

        var mainLog = sc.parallelize(Seq(""))
        mainLog = logRDD(sc, mainLog, "Starting Consolidation ...")
        mainLog = logRDD(sc, mainLog, "... Detected OS: " + System.getProperty("os.name"))
        mainLog = logRDD(sc, mainLog, "... Received arguments:")
        mainLog = logRDD(sc, mainLog, s"...... 0 - HDFS config: ${args(0)}")
        mainLog = logRDD(sc, mainLog, s"...... 1 - HADOOP base path: ${args(1)}")
        mainLog = logRDD(sc, mainLog, s"...... 2 - Drug input file name: ${args(2)}")
        mainLog = logRDD(sc, mainLog, s"...... 3 - Physician input file name: ${args(3)}")
        mainLog = logRDD(sc, mainLog, s"...... 4 - Prescription input file name: ${args(4)}")
        mainLog = logRDD(sc, mainLog, s"...... 5 - Consolidation output file name: ${args(5)}")
        validateArgs(storage, args)

        storage.connect(args(0))
        val hadoopBasePath    = args(1)
        val drugFile          = args(2)
        val physicianFile     = args(3)
        val prescriptionFile  = args(4)
        val consolidationFile = args(5)

        mainLog = logRDD(sc, mainLog, "... Processing Drugs dataset ...")
        val drugs = parseWithValidationNoRepetition[Drug](sc, storage, hadoopBasePath, drugFile).map(_.asInstanceOf[Drug]).cache
        val mapDrugs = drugs.map(x => ((x.simpleName, x.genericName), s"${x.opioid},${x.longActingOpioid},${x.antibiotic},${x.antipsychotic}")).collect.toMap
        val broadcastMapDrugs = sc.broadcast(mapDrugs)
        mainLog = logRDD(sc, mainLog, "... Drugs dataset processed!")

        mainLog = logRDD(sc, mainLog, "... Processing Physicians dataset ...")
        val physicians = parseWithValidationUniqueRepetition[Physician](sc, storage, hadoopBasePath, physicianFile).map(_.asInstanceOf[Physician]).cache
        mainLog = logRDD(sc, mainLog, "... Physicians dataset processed!")

        mainLog = logRDD(sc, mainLog, "... Processing Prescriptions dataset ...")
        val prescriptions = parseWithValidationNoRepetition[Prescription](sc, storage, hadoopBasePath, prescriptionFile).map(_.asInstanceOf[Prescription]).filter(!_.npi1stName.isEmpty).cache
        mainLog = logRDD(sc, mainLog, "... Prescriptions dataset processed!")

        mainLog = logRDD(sc, mainLog, "... Generating Consolidation dataset ...")
        val consolidation = generateConsolidation(sc, storage, broadcastMapDrugs, physicians, prescriptions)
        storage.writeRDD2File(s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}/${consolidationFile}",
                              s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}", consolidation)
        mainLog = logRDD(sc, mainLog, s"... Consolidation generated and written to ${s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}/${consolidationFile}"}")

        mainLog = logRDD(sc, mainLog, "Finished Consolidation!")
        storage.writeRDD2File(s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}/consolidation.${getTimeString}.log",
                              s"${storage.getUri}/${hadoopBasePath}/${Constants.OutputBaseLocation}", mainLog)

        sc.stop
        storage.disconnect
  }
}