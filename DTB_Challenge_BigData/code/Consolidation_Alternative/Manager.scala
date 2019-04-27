import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._



object Manager {


  def main(args: Array[String]) {

/*
    val drugsPath = "/Users/carlosgodinho/consolidation/input/PartD_Prescriber_PUF_Drug_Ntl_15.csv"
    val physiciansPath = "/Users/carlosgodinho/consolidation/input/Physician_Compare_National_Downloadable_File_2015.csv"
    val prescriptionsPath = "/Users/carlosgodinho/consolidation/input/PartD_Prescriber_PUF_NPI_Drug_15.txt"

    val drugsPath = "/Users/carlosgodinho/consolidation/input/Drug_t1.csv"
    val physiciansPath = "/Users/carlosgodinho/consolidation/input/Physician_t1.csv"
    val prescriptionsPath = "/Users/carlosgodinho/consolidation/input/Prescription_t1.txt"
*/

    val drugsPath = args(0)
    val physiciansPath = args(1)
    val prescriptionsPath = args(2)


    val spark = SparkSession
        .builder()
        .appName("Consolidation2")
        .config("spark.master", "local[*]")
        .getOrCreate()

    import spark.implicits._

    // Load data from disk
    val dfDrugs: DataFrame = spark.read.format("csv").
      option("header", "true").
      load(drugsPath)

    val dfPhysicians : DataFrame = spark.read.format("csv").
      option("header", "true").
      load(physiciansPath)

    val dfPrescriptions : DataFrame = spark.read.
      option("sep", "\t").
      option("header", "true").
      csv(prescriptionsPath)

    // Prepare drugs
    val drugsColsToSelect: Array[String] = Array("Drug Name", "Generic Name", " Opioid Drug Flag ", " Long-Acting Opioid Drug Flag ", " Antibiotic Drug Flag ", " Antipsychotic Drug Flag ")
    val dfCleanDrugs = dfDrugs.select(drugsColsToSelect.head, drugsColsToSelect.tail: _*)
    val dfDrugsTrim = dfCleanDrugs.withColumn("Drug Name",trim($"Drug Name"))
      .withColumn("Generic Name", trim($"Generic Name"))
    val dfFinalDrugs = dfDrugsTrim.withColumnRenamed("Drug Name", "drug_name").
      withColumnRenamed("Generic Name", "generic_name")

    // Prepare Physicians
    val physiciansColsToSelect: Array[String] = Array("NPI", "First Name", "Medical school name")
    val dfCleanPhysicians = dfPhysicians.select(physiciansColsToSelect.head, physiciansColsToSelect.tail: _*)
    val dfTempPhysicians = dfCleanPhysicians.withColumnRenamed("NPI", "npi").
      withColumnRenamed("First Name", "first_name").
      withColumnRenamed("Medical school name", "medical_school")
    val dfFinalPhysicians = dfTempPhysicians.groupBy("npi").agg($"npi", first("medical_school"))

    // Prepare Prescriptions
    val prescriptionsColsToSelect: Array[String] = Array("npi", "nppes_provider_first_name", "drug_name", "generic_name", "bene_count", "total_claim_count")
    val dfTempPrescriptions = dfPrescriptions.select(prescriptionsColsToSelect.head, prescriptionsColsToSelect.tail: _*)
    val dfFinalPrescriptions = dfTempPrescriptions.filter(trim($"nppes_provider_first_name").isNotNull)

    // Printing sizes of final datasets
    println("++++++++++++++++++   Final size Drug is " + dfFinalDrugs.count())
    println("++++++++++++++++++   Final size Physician is " + dfFinalPhysicians.count())
    println("++++++++++++++++++   Final size Prescription is " + dfFinalPrescriptions.count())

    // Performing the joins
    val firstJoin = dfFinalPrescriptions.join(dfFinalPhysicians, Seq("npi"), "inner")
    val secondJoin = firstJoin.join(dfFinalDrugs, Seq("drug_name", "generic_name"), "inner")

    // Printing size of joins
    println("++++++++++++++++++   Final size 1st join is " + firstJoin.count())
    println("++++++++++++++++++   Final size 2nd join is " + secondJoin.count())

    secondJoin.coalesce(1).write.option("header", "true").csv(args(3))
  }
}
