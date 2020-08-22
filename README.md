The application processes the Lumen experiments data file using Spark and generates 2 files 'experiments' and 'experiment_variations'. 

The code queries a data dictionary HTTP link to get information on availability of latest data and its location.

The application is launched from command line in Qubole using the following command:

/usr/lib/spark/bin/spark-submit \
     --class com.fanatics.data.platform.lumen.experiments.LumenDataGenerator \
     --master yarn-client \
     --max-executors 25 \
     --num-executors 6 \
     --driver-memory 4g \
     --executor-memory 5g \
     --executor-cores 2 \
     s3://fanatics.dev.internal.confidential/LumenExperiments/jar/lumen-experiments-assembly-0.1.jar \
     run  \
     --configPath s3://fanatics.dev.internal.confidential/LumenExperiments/config/lumen-spark-conf.yml


