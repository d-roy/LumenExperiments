CREATE EXTERNAL TABLE `omniture_reporting.experiments`(
  `experiment_id` int,
  `experiment_name` string,
  `status` string,
  `start_date` string,
  `end_date` string)
COMMENT 'Experiments'
ROW FORMAT DELIMITED
  FIELDS TERMINATED BY '\t'
  LINES TERMINATED BY '\n'
STORED AS INPUTFORMAT
  'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
LOCATION
  's3://fanatics.dev.internal.confidential/omniture_report_testing/data/experiments'