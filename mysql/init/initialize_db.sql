USE qbio;

CREATE TABLE metrics (
  metricId INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  formula VARCHAR(100) NOT NULL,
  PRIMARY KEY (metricId),
  UNIQUE KEY metrics_index1 (name)
) 
ENGINE=INNODB DEFAULT CHARSET=UTF8
COMMENT 'Metric metadata';

INSERT INTO metrics VALUES
(1, "cpu-usage-percent", "[CPU]Totl%"),
-- (2, "memory-usage-percent", "[MEM]Used / [MEM]Tot"),
(3, "network-kb-in", "[NET]RxKBTot"),
(4, "network-kb-out", "[NET]TxKBTot");
 

CREATE TABLE measurements (
  measurementId BIGINT(20) NOT NULL AUTO_INCREMENT,
  metricId INT(11) NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  value DOUBLE,

  PRIMARY KEY (measurementId),
  KEY measurements_index1 (metricId, timestamp)
) 
ENGINE=INNODB DEFAULT CHARSET=UTF8
COMMENT 'Raw measurements';

CREATE TABLE tiered_measurements (
  tieredMeasurementId BIGINT(20) NOT NULL AUTO_INCREMENT,
  tier SMALLINT NOT NULL COMMENT '1: minute level, 2: hour level',
  metricId INT(11) NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  sum DOUBLE NULL COMMENT 'Sum of the raw values',
  min DOUBLE NULL COMMENT 'Min of the raw values',
  max DOUBLE NULL COMMENT 'Max of the raw values',
  sampleSize BIGINT(20) NULL COMMENT 'Number of raw samples',

  PRIMARY KEY (tieredMeasurementId),
  UNIQUE KEY tiered_measurements_index1 (tier, metricId, timestamp)
) 
ENGINE=INNODB DEFAULT CHARSET=UTF8
COMMENT 'Measurements aggregated at different tiers';