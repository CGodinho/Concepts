SPARK_HOME="/opt/spark-2.4.0"
CONSOLIDATION_HOME="/mnt/dtb/consolidation/"
MASTERNODE="master"
SPARKPORT="7077"
CONFIGS="spark.executor.extraJavaOptions=-Dlog4j.configuration=file:/opt/spark-2.4.0/conf/log4j.properties"
NAMENODE=${MASTERNODE}:${SPARKPORT}
HADOOP_CONSOLIDATION_HOME="/dtb/consolidation/"
HADOOP_CONFIG="/opt/hadoop-2.9.2/etc/hadoop/core-site.xml,/opt/hadoop-2.9.2/etc/hadoop/hdfs-site.xml"

$SPARK_HOME/bin/spark-submit \
    --conf $CONFIGS \
    --master spark://$NAMENODE \
    --executor-memory 4G \
    --driver-memory 8G \
    $CONSOLIDATION_HOME/consolidation_2.11-*.jar \
    $HADOOP_CONFIG \
    $HADOOP_CONSOLIDATION_HOME \
    PartD_Prescriber_PUF_Drug_Ntl_15.csv \
    Physician_Compare_National_Downloadable_File_2015.csv \
    PartD_Prescriber_PUF_NPI_Drug_15.txt \
    Consolidation.csv
