#/usr/bin/sh


#++++++++++++++++++++++++++++++++++++++++++++++++++
# Deployment call script
#++++++++++++++++++++++++++++++++++++++++++++++++++

# On YARN

  $SPARK_HOME/bin/spark-submit \
  --class LogRegApp \
  --master yarn-cluster \
  --executor-memory 1g \
  --num-executors 2 \
  --driver-memory 1g \
  --executor-memory 2g \
  --executor-cores 1 \
  batch-prediction-project_2.10-1.0.jar HIGGS.csv

  $SPARK_HOME/bin/spark-submit \
  --class LogRegApp \
  --master yarn-cluster \
  --executor-memory 4g \
  batch-prediction-project_2.10-1.0.jar HIGGS.csv


$SPARK_HOME/bin/spark-submit \
  --class LogRegApp \
  --master yarn-client \
  --executor-memory 4g \
  --num-executors 5 \
  batch-prediction-project_2.10-1.0.jar HIGGS.csv


  $SPARK_HOME/bin/spark-submit \
  --class LogRegApp \
  --master yarn-client \
  batch-prediction-project_2.10-1.0.jar HIGGS.csv

  $SPARK_HOME/bin/spark-submit \
  --class LogRegApp \
  --master local[*] \
  --executor-memory 1g \
  batch-prediction-project_2.10-1.0.jar HIGGS.csv

  /usr/local/spark/bin/spark-submit \
  --class LogRegApp \
  --master local[*] \
  --executor-memory 2g \
  batch-prediction-project_2.10-1.0.jar ./Data/HIGGS.csv




  $SPARK_HOME/bin/spark-submit \
  --class RecommandationApp \
  --master yarn-cluster \
   --executor-memory 4g \
   recommandation-project_2.10-1.0.jar u.data

/usr/local/spark/bin/spark-submit \
  --class RecommandationApp \
  --master local[*] \
  --executor-memory 2g \
  recommandation-project_2.10-1.0.jar data/u.data




  ### Pi example test

   $SPARK_HOME/bin/spark-submit \
    --class org.apache.spark.examples.SparkPi \
    --master yarn-cluster \
    $SPARK_HOME/lib/spark-examples*.jar \
    10


    --num-executors 3 \
    --driver-memory 4g \
    --executor-memory 2g \
    --executor-cores 1 \



   /usr/local/spark/bin/spark-submit \
  --class LogRegApp \
  --master local[*] \
  --executor-memory 2g \
  batch-prediction-project_2.10-1.0.jar  ./Data/HIGGS.csv


  # H2O on Hadoop

  hadoop jar h2odriver_hdp2.1.jar water.hadoop.h2odriver \
  -libjars ../h2o.jar \
  -mapperXmx 1g \
  -nodes 4 \
  -output h2o_output_2




