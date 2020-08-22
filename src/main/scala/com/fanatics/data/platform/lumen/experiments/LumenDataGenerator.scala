package com.fanatics.data.platform.lumen.experiments

import com.fanatics.data.platform.lumen.experiments.config.Cli
import com.fanatics.data.platform.lumen.experiments.config.Cli.Params
import com.fanatics.data.platform.lumen.experiments.config.LumenDataConfigParser
import com.fanatics.data.platform.lumen.experiments.util.LumenParser
import com.fanatics.dataplatform.sparkle.SparkleContextHolder
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.sql.functions.rank
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql._

/**
  * @author debroy
  */


object LumenDataGenerator {
  val pattern = "yyyyMMdd-HHmmss"
  val usage =
    """
    Usage:
    Command:  [--configPath s3://configPath]
    Optional: [--start-date] [--end-date]
    """

  def main(args: Array[String]) {
    Cli.parser.parse(args: _*)
    process(Cli.params)
  }

  def process(params: Params): Unit = {
    val configuration = LumenDataConfigParser.buildConfiguration(params.configPath)
    val data_dict_url = configuration.dictionaryUrl
    val meta_file = configuration.meta_file
    val meta_file2 = configuration.meta_file2
    val experiments_start_save_path = configuration.experiments_start_save_path
    val experiment_variations_save_path = configuration.experiment_variations_save_path

    val conf = new SparkConf().setAll(configuration.sparkJobConf.applicationProperties)
    val sparkContext = new SparkContext(conf)
    val lumenparser = new LumenParser(meta_file, meta_file2, data_dict_url)
    val (file_path, available_dt) = lumenparser.getLumenInfo

    if (file_path != "None" && available_dt != "None") {
      getExperimentStarts(sparkContext, file_path, experiments_start_save_path)
      getExperimentVariations(sparkContext, file_path, experiment_variations_save_path)
      lumenparser.UpdateMetaLatestDt(available_dt)
      lumenparser.UpdateProcStatus("0")
    } else {
      lumenparser.UpdateProcStatus("1")
    }
  }

  def getExperimentStarts(sc: SparkContext, file_path: String, experiments_start_save_path: String): Unit = {
    val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)
    import sqlContext.implicits._

    val exp_data = sqlContext.read.format("com.databricks.spark.csv")
        .option("header", "true")
        .load(file_path)

    exp_data.createOrReplaceTempView("experiments")

    val exp_df = sqlContext.sql("select distinct experiment_id, experiment_name, status, date_format(FROM_UTC_TIMESTAMP(start_date, 'EST5EDT'), 'yyyy-MM-dd HH:mm:ss') as start_date, date_format(FROM_UTC_TIMESTAMP(end_date, 'EST5EDT'), 'yyyy-MM-dd HH:mm:ss') as end_date from experiments order by experiment_id, start_date")

    exp_df.coalesce(1)
      .write.format("com.databricks.spark.csv")
      .mode("overwrite")
      .option("delimiter","\t")
      .save(experiments_start_save_path)
  }

  def getExperimentVariations(sc: SparkContext, file_path: String, experiment_variations_save_path: String): Unit = {
    val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)
    import sqlContext.implicits._

    val exp_data = sqlContext.read.format("com.databricks.spark.csv")
        .option("header", "true")
        .load(file_path)

    exp_data.createOrReplaceTempView("experiments")

    val exp_df = sqlContext.sql("select distinct concat(experiment_id,',',variation_name,',','1') as variation_id, experiment_id, variation_name from experiments")

    val win1 = Window.partitionBy($"experiment_id").orderBy($"variation_name")
    val exp_df2 = exp_df.select($"variation_id", $"experiment_id", $"variation_name", rank.over(win1).alias("rank"))
    exp_df2.createOrReplaceTempView("variations")

    val exp_df3 = sqlContext.sql("select variation_id, experiment_id, variation_name as experiment_variation, case when rank = 1 then 'control' else '' end as default_indicator from variations")

    exp_df3.coalesce(1)
      .write
      .mode("overwrite")
      .format("com.databricks.spark.csv")
      .option("delimiter", "\t")
      .save(experiment_variations_save_path)
  }

}

