CREATE EXTERNAL TABLE `omniture_reporting.experiment_variations`(
  `variation_id` string,
  `experiment_id` int,
  `experiment_variation` string,
  `default_indicator` string)
COMMENT 'Experiment Variations'
ROW FORMAT DELIMITED
  FIELDS TERMINATED BY '\t'
  LINES TERMINATED BY '\n'
STORED AS INPUTFORMAT
  'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
LOCATION
  's3://fanatics.prod.internal.confidential/omniture_report_testing/data/experiment_variations';