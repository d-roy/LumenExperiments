package com.fanatics.data.platform.lumen.experiments.config

import com.beust.jcommander.{JCommander, Parameter, Parameters}

/**
  * Created by ajitkoti on 9/21/16.
  */

object Cli {
  @Parameters(commandDescription = "Runs Lumen Experiments Data Processor")
  class Params {
    @Parameter(names = Array("--configPath", "-c"), description = "configuration file path on s3", required = true)
    val configPath: String = null

    @Parameter(names = Array("--start-date", "-s"), description = "start date")
    var startDate:String = ""

    @Parameter(names = Array("--end-date", "-e"), description = "end date")
    var endDate:String = ""
  }



  val params = new Params()

  val parser = new JCommander()
  parser.addCommand("run", params)
}