package com.dtb.consolidation.fs

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem,Path}


/**
 * @author CGodinho
 *
 * This class is responsible for hiding the details of accessing the hadoop file system.
 * It extends basic trait Sotrage.
 * An handler to the file system is opened during the execution of the application.
 * Methods are offered in order to manage paths (called locations) and write RDD to files.
 */
class Hdfs extends Storage with Serializable {

    // Hadoop file system handler
    private var fileSystem : FileSystem = null

    def connect(configFiles: String) {

        val conf  = new Configuration()
        val files = configFiles.split(",")
        files.foreach(file => conf.addResource(new Path(file)))
       this.fileSystem = FileSystem.get(conf)
    }

    def disconnect =  this.fileSystem.close

    def getUri : String = this.fileSystem.getUri.toString

    def makeLocation(location : String) : Unit = if (!isLocation(location)) this.fileSystem.mkdirs(new Path(location))

    def dropLocation(location : String) : Unit = if (isLocation(location)) this.fileSystem.delete(new Path(location), true)

    /**
      * Renames a location related to a processing file into a new location.
      *
      * @param oldLocation - location to be deleted
      * @param newLocation - location to be created
      */
    def renameLocation(oldLocation : String, newLocation : String) : Unit = this.fileSystem.rename(new Path(s"${oldLocation}/part-00000"), new Path(newLocation))

    def isLocation(location : String) : Boolean = this.fileSystem.exists(new Path(location))
}