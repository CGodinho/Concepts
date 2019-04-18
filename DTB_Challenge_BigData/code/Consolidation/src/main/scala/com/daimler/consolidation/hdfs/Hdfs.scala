package com.daimler.consolidation.hdfs

import scala.util.Random
import java.util.Calendar
import org.apache.spark.rdd.RDD
import java.text.SimpleDateFormat
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem,Path}

/**
 * @author CGodinho
 *
 * This class is responsible for hiding the details of accessing the hadoop file system.
 * An handler to the file system is opened during the execution of the application.
 * Methods are offered in order to manage paths (called locations) and write RDD to files.
 */
class Hdfs extends Serializable {

    // Hadoop file system handler
    private var fileSystem : FileSystem = null

    /**
     * Creates a connection to hadoop file system.
     *
     * @param configFiles - configuration files separated by a ","
     */
    def connect(configFiles: String) {

        val conf  = new Configuration()
        val files = configFiles.split(",")
        files.foreach(file => conf.addResource(new Path(file)))
       this.fileSystem = FileSystem.get(conf)
    }

    /**
     * Releases the hadoop file system connection.
     */
    def disconnect =  this.fileSystem.close

    /**
     * Returns the available URL for HDFS
     *
     * @return - HDFS url
     */
    def getUri : String = this.fileSystem.getUri.toString

    /**
     * Creates a new location (directory) if not already available.
     * Locations follow hadoop directory rules and allow to receive several layers in a unique call.
     *
     * @param location - location to be created
     */
    def makeLocation(location : String) : Unit = {

        val path = new Path(location)
        if (!fileSystem.exists(new Path(location))) this.fileSystem.mkdirs(path)
    }

    /**
     * Removes a location (directory) if available.
     *
     * @param location - location to be deleted
     */
    def dropLocation(location : String) : Unit = {

        val path = new Path(location)
        if (fileSystem.exists(new Path(location))) this.fileSystem.delete(path, true)
    }

    /**
     * Checks if a location (file or directory) is available.
     *
     * @param location - location to be checked
     *
     * @return - boolean: true exists; false does not exist.
     */
    def isLocation(location : String) : Boolean = this.fileSystem.exists(new Path(location))

    /**
     * Creates a unique identifier which is a combination of a series of
     * random, local configuration and time stamps in order to be able to
     * have a unique identified in a string.
     *
     * @return Unique identifier
     */
    private def createUniqueIdentifier() : String = {

        val nowTime = Calendar.getInstance().getTime()
        val formater = new SimpleDateFormat("yyyyMMddHHmmssSSS")
        val now = formater.format(nowTime)
        val calendar = Calendar.getInstance()
        calendar.setTime(nowTime)
        val offset = calendar.get(Calendar.DST_OFFSET)
        (s"${now}_${offset}_${System.currentTimeMillis()}_${new Random().nextInt(100)}_${java.net.InetAddress.getLocalHost.getHostName}")
    }

    /**
     * Write an RDD[String] structure to a file.
     * Each RDD[String] is written into a line.
     *
     * @param filePath - location to write the data
     * @param tmpPath - temp path used for intermediate processing
     * @param data - RDDs to be written
     */
    def writeRDD2File(filePath : String, tmpPath : String, data : RDD[String]) {

        // Create a temp location to store collected data
        val subFilePath = s"${tmpPath}/${createUniqueIdentifier}"

        // Clean empties and coalesce all content into the tmp file path
        data.filter(line => !line.isEmpty()).coalesce(1).saveAsTextFile(s"${this.getUri}${subFilePath}")

        // Drop the file if already available, so this is an overwrite)
        dropLocation(filePath)

        // Move the new created file which is named as part-00000 by hadOop into the final file with given name
        this.fileSystem.rename(new Path(s"${subFilePath}/part-00000"), new Path(filePath))

        // Clean the tmp file
        dropLocation(subFilePath)
  }
}