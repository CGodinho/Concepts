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
    Drug_t1.csv \
    Physician_t1.csv \
    Prescription_t1.txt \
    Consolidation.csv
