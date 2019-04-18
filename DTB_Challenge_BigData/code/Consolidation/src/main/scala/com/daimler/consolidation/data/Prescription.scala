package com.daimler.consolidation.data

import scala.util.matching.Regex
import com.daimler.consolidation.Field
import com.daimler.consolidation.Constants


/**
 * @author CGodinho
 *
 * This class represents a Prescription.
 * It holds specific fields for each parameter.
 * Class Prescription extends base class Entity.
 */
case class Prescription (npi : Long, simpleName : String, genericName: String, npi1stName : String, beneCount : String, totalClaimCount : String)  extends Entity


/**
 * @author CGodinho
 *
 * Companion object for class Drug.
 * It holds specific fields for each parameter.
 * Object Drug extends base object EntityCompanion.
 */
object Prescription extends EntityCompanion[Prescription] {

  final val PosNpi             : Int = 1
  final val PosSimpleName      : Int = 8
  final val PosGenericName     : Int = 9
  final val PosNpi1stName      : Int = 3
  final val PosBeneCount       : Int = 10
  final val PosTotalClaimCount : Int = 11

  def key(record : String , fields : List[Int]) : String = Field.getToken(Prescription.PosNpi,         record, fields) + "," +
                                                           Field.getToken(Prescription.PosSimpleName,  record, fields) + "," +
                                                           Field.getToken(Prescription.PosGenericName, record, fields)

  def size : Int = Constants.RecordSizePrescription

  def separator : Char = '\t'

  def quote : Char = '"'

  def validate : Regex = Constants.RegExValidPrescription

  def factory(record : String , fields : List[Int]) : Entity =
    Prescription(Field.getToken(Prescription.PosNpi,             record, fields).toLong,
                 Field.getToken(Prescription.PosSimpleName,      record, fields),
                 Field.getToken(Prescription.PosGenericName,     record, fields),
                 Field.getToken(Prescription.PosNpi1stName,      record, fields),
                 Field.getToken(Prescription.PosBeneCount,       record, fields),
                 Field.getToken(Prescription.PosTotalClaimCount, record, fields))
}