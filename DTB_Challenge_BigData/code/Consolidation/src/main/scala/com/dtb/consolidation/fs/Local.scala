package com.dtb.consolidation.fs

import collection.JavaConverters._
import java.nio.file.{Files, Paths}


class Local extends Storage {

    def removeUri(location : String)  : String = location.substring(getUri.length + 1, location.size)

    def connect(configFiles: String): Unit = {}

    def disconnect: Unit = {}

    def getUri: String = "file://"

    def makeLocation(location: String): Unit = if (!isLocation(location)) Files.createDirectory(Paths.get(removeUri(location)))

    def dropLocation(location: String): Unit = {

        if (isLocation(location)) {
            if (!Files.isDirectory(Paths.get(removeUri(location)))) {
                Files.delete(Paths.get(removeUri(location)))
            }
            else {
                Files.walk(Paths.get(removeUri(location))).iterator.asScala.filter(!Files.isDirectory(_)).foreach(Files.deleteIfExists(_))
                Files.walk(Paths.get(removeUri(location))).iterator.asScala.filter(Files.isDirectory(_)).foreach(Files.deleteIfExists(_))
            }
        }
    }

    def renameLocation(oldLocation : String, newLocation : String) : Unit = Files.move(Paths.get(removeUri(s"${oldLocation}/part-00000")),
                                                                                       Paths.get(removeUri(newLocation)))

    def isLocation(location: String): Boolean = Files.exists(Paths.get(removeUri(location)))
}

