package com.daimler.consolidation.data

import scala.util.matching.Regex
import com.daimler.consolidation.Field
import com.daimler.consolidation.Constants


/**
 * @author CGodinho
 *
 * This class represents a Drug.
 * It holds specific fields for each parameter.
 * Class Drug extends base class Entity.
*/
case class Drug (simpleName : String, genericName: String, opioid : Char, longActingOpioid : Char, antibiotic : Char, antipsychotic : Char)  extends Entity


/**
 * @author CGodinho
 *
 * Companion object for class Drug.
 * It holds specific fields for each parameter.
 * Object Drug extends base object EntityCompanion.
 */
object Drug extends EntityCompanion[Drug] {

    final val PosSimpleName       : Int = 1
    final val PosGenericName      : Int = 2
    final val PosOpioid           : Int = 16
    final val PosLongActingOpioid : Int = 17
    final val PosAntibiotic       : Int = 18
    final val PosAntipsychotic    : Int = 19


    def key(record : String, fields : List[Int]) : String = Field.getToken(Drug.PosSimpleName,  record, fields) + "," +
                                                            Field.getToken(Drug.PosGenericName, record, fields)

    def size : Int = Constants.RecordSizeDrug

    def separator : Char = ','

    def quote : Char = '"'

    def validate : Regex = Constants.RegExValidDrug

    def factory(record : String , fields : List[Int]) : Entity = {

        def fieldToChar(field: String): Char = if (field.equalsIgnoreCase("Y")) 'Y' else 'N'

        Drug(Field.getToken(Drug.PosSimpleName,  record, fields),
             Field.getToken(Drug.PosGenericName, record, fields),
             fieldToChar(Field.getToken(Drug.PosOpioid,           record, fields)),
             fieldToChar(Field.getToken(Drug.PosLongActingOpioid, record, fields)),
             fieldToChar(Field.getToken(Drug.PosAntibiotic,       record, fields)),
             fieldToChar(Field.getToken(Drug.PosAntipsychotic,    record, fields)))
    }
}