package com.fanatics.data.platform.lumen.experiments.util

import com.fanatics.dataplatform.sparkle.util.AWSS3Util
import com.fanatics.dataplatform.sparkle.util.AWSClientBuilder
import org.apache.spark.sql.SQLContext
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import net.liftweb.json.DefaultFormats
import net.liftweb.json._
import scala.io.Source
import java.io._

/**
  * @author debroy
  */

case class LumenInfo(
                      name: String,
                      dataset_version: String,
                      path: String,
                      status: String,
                      reported_at: String,
                      partition_type: String
                    )

case class Root(root: LumenInfo)

class LumenParser (meta_file: String, meta_file2: String, data_dict_url: String) {
  val s3Client = new AWSClientBuilder().getAWSS3Client()

  def getLumenInfo(): (String,String) = {
    val url = data_dict_url

    var jsonString: Option[String] = None
    try {
      jsonString = Some(Source.fromURL(url).mkString)
    } catch {
      case e: Exception => throw e
    }

    implicit val formats = DefaultFormats

    var ret_file_path = "None"
    var ret_reported_at = "None"

    if (jsonString != None && jsonString.get.length > 0) {
      println(jsonString)
      val json = parse(jsonString.get)
      val jsonRoot = json.extract[Root]
      val lumenInfo = jsonRoot.root

      val reported_at = lumenInfo.reported_at
      var file_path = lumenInfo.path
      var status = lumenInfo.status
      var last_proc_dt = ""

      if (meta_file.startsWith("s3")) {
        val s3InputStream = new AWSS3Util(s3Client).getInputStreamForS3Object(meta_file)
        val bufferedSource = Source.fromInputStream(s3InputStream)
        for (line <- bufferedSource.getLines) {
          last_proc_dt = line
        }
        bufferedSource.close
      }

      if ((reported_at.slice(0, 19) > last_proc_dt.slice(0, 19)) && status == "available") {
        ret_file_path = file_path
        ret_reported_at = reported_at
      }
    }
    (ret_file_path, ret_reported_at)
  }

  def UpdateMetaLatestDt(latest_dt: String) = {
    if (meta_file.startsWith("s3")) {
      val aws3Util = new AWSS3Util(s3Client)
      aws3Util.putS3Object(meta_file, latest_dt)
    }
  }

  def UpdateProcStatus(status: String) = {
    if (meta_file2.startsWith("s3")) {
      val aws3Util = new AWSS3Util(s3Client)
      aws3Util.putS3Object(meta_file2, status)
    }
  }
}
