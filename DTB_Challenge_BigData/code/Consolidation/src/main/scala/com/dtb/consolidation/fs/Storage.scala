package com.dtb.consolidation.fs

import scala.util.Random
import java.util.Calendar
import org.apache.spark.rdd.RDD
import java.text.SimpleDateFormat


trait Storage {

    /**
     * Creates a connection to hadoop file system.
     *
     * @param configFiles - configuration files separated by a ","
     */
    def connect(configFiles: String)

    /**
     * Releases the hadoop file system connection.
     */
    def disconnect

    /**
     * Returns the available URL for HDFS
     *
     * @return - HDFS url
     */
    def getUri : String

    /**
     * Creates a new location (directory) if not already available.
     * Locations follow hadoop directory rules and allow to receive several layers in a unique call.
     *
     * @param location - location to be created
     */
    def makeLocation(location : String) : Unit

    /**
     * Removes a location (directory) if available.
     *
     * @param location - location to be deleted
     */
    def dropLocation(location : String) : Unit

    def renameLocation(oldLocation : String, newLocation : String) : Unit

    /**
     * Checks if a location (file or directory) is available.
     *
     * @param location - location to be checked
     *
     * @return - boolean: true exists; false does not exist.
     */
    def isLocation(location : String) : Boolean

    def createUniqueIdentifier() : String = {

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
        data.filter(line => !line.isEmpty()).coalesce(1).saveAsTextFile(subFilePath)

        // Drop the file if already available, so this is an overwrite)
        dropLocation(filePath)

        // Move the new created file which is named as part-00000 by HADOOP into the final file with given name
        renameLocation(subFilePath, filePath)

        // Clean the tmp file
        dropLocation(subFilePath)
    }

}
