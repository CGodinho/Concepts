package com.daimler.consolidation

import util.matching.Regex


/**
  * @author CGodinho
  *
  * This class holds the general purpose constants.
  */
object Constants {

    // Results returned by validation
    final val ResultOk     : Byte =  1
    final val ResultError  : Byte = -1

    // RegExs for valiDating input records
    final val RegExValidDrug         : Regex = ".+[A-Za-z].+,.+[A-Za-z].+,.*(,[ ]*(Y|N)[ ]*){4}".r
    final val RegExValidPhysician    : Regex = "[0-9]{10},.+".r
    final val RegExValidPrescription : Regex = "[0-9]{10}\\t.+".r
    final val RegExQuoted            : Regex = "^\".*\"$".r

    // Hadoop input initial path
    final val InputBaseLocation      : String = "input"

    // Hadoop detection/output initial path
    final val OutputBaseLocation     : String = "output"

    // Size of entity records
    final val RecordSizeDrug         : Int = 19
    final val RecordSizePhysician    : Int = 43
    final val RecordSizePrescription : Int = 21

    // Arguments received by application
    final val ApplicationArgs : Int = 6

    // Spark application name
    final val ApplicationName : String = "Consolidation"

    // HTTP error code
    final val HTTP_OK : Int = 200

    // Partitions used when loading data into RDDs
    final val  RDDPartitions: Int = 8

    // Header descriptor for fina lConsolidation Dataset
    final val HeaderConsolidation : String = "npi,drug_simple_name,drug_generic_name,bene_count,total_claim_count,medical_school,drug_opioid_flag,drug_long_acting_opioid_flag,drug_antibiotic_flag,drug_antipsychotic_flag"
}
