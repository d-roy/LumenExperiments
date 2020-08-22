package com.fanatics.data.platform.lumen.experiments.config

import java.io.File

import com.fanatics.dataplatform.sparkle.config.parser.SparkleConfigYAMLParser
import com.fanatics.dataplatform.sparkle.config.{SparkContextConfig, SparkJobConf, SparkleConfiguration}
import com.fanatics.dataplatform.sparkle.spray.orc.ORCOutputConfig
import com.fanatics.dataplatform.sparkle.spray.parquet.ParquetOutputConfig
import com.fanatics.dataplatform.sparkle.util.AWSClientBuilder

/**
  * @author debroy
  */
case class LumenDataConfiguration(sparkJobConf: SparkJobConf,
                                  sparkContextConfig: SparkContextConfig,
                                  hadoopConfiguration: Map[String, String],
                                  yarnConfiguration: Map[String, String],
                                  dictionaryUrl: String,
                                  apiKey: String,
                                  subscriptionId: String,
                                  meta_file: String,
                                  meta_file2: String,
                                  experiments_start_save_path: String,
                                  experiment_variations_save_path: String,
                                  experimentsTable: String,
                                  experimentVariationsTable: String
                              ) extends SparkleConfiguration


object LumenDataConfigParser {

  def buildConfiguration(path: String): LumenDataConfiguration   = {
    val sparkleConfigYAMLParser = new SparkleConfigYAMLParser[LumenDataConfiguration]

    if (path.startsWith("s3")) {
      val s3Client = new AWSClientBuilder().getAWSS3Client()
      sparkleConfigYAMLParser.parseConfiguartionFromS3(path, new AWSClientBuilder().getAWSS3Client(), classOf[LumenDataConfiguration] )
    }
    else {
      sparkleConfigYAMLParser.parseConfiguartionFromFile(new File(path), classOf[LumenDataConfiguration])
    }

  }
}
