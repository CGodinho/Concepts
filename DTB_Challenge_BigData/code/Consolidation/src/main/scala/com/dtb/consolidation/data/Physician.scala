package com.dtb.consolidation.data

import util.matching.Regex
import com.dtb.consolidation.{Constants, Field}


/**
 * This class represents a Physician.
 * It holds specific fields for each parameter.
 * Class Physician extends base class Entity.
 */
case class Physician (npi : Long, medicalSchool : String) extends Entity


/**
 * Companion object for class Physician.
 * It holds specific fields for each parameter.
 * Object Physician extends base object EntityCompanion.
 */
object Physician extends EntityCompanion[Physician] {

    final val PosNpi           : Int = 1
    final val PosMedicalSchool : Int = 10

    def key(record : String , fields : List[Int]) : String = Field.getToken(Physician.PosNpi, record, fields)

    def size : Int = Constants.RecordSizePhysician

    def separator : Char = ','

    def quote : Char = '"'

    def validate : Regex = Constants.RegExValidPhysician

    def factory(record : String , fields : List[Int]) : Entity =  Physician(Field.getToken(Physician.PosNpi,           record, fields).toLong,
                                                                            Field.getToken(Physician.PosMedicalSchool, record, fields))
}