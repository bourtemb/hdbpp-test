-- ---------------------------------------------------------------
-- --------------CONCRETE TABLE INHERITANCE-----------------------
-- ---------------------------------------------------------------

CREATE TABLE IF NOT EXISTS att_conf
(
att_conf_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
att_name VARCHAR(255) UNIQUE NOT NULL,
att_conf_data_type_id INT UNSIGNED NOT NULL,
INDEX(att_conf_data_type_id)
) ENGINE=MyISAM COMMENT='Attribute Configuration Table';

DROP TABLE att_conf_data_type;
CREATE TABLE IF NOT EXISTS att_conf_data_type
(
att_conf_data_type_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
data_type VARCHAR(255) NOT NULL
) ENGINE=MyISAM COMMENT='Attribute types description';

INSERT INTO att_conf_data_type (data_type) VALUES
('scalar_double_ro'),('scalar_double_rw'),('array_double_ro'),('array_double_rw'),
('scalar_int64_ro'),('scalar_int64_rw'),('array_int64_ro'),('array_int64_rw'),
('scalar_int8_ro'),('scalar_int8_rw'),('array_int8_ro'),('array_int8_rw'),
('scalar_string_ro'),('scalar_string_rw'),('array_string_ro'),('array_string_rw');

CREATE TABLE IF NOT EXISTS att_history
(
att_conf_id INT UNSIGNED NOT NULL,
time DATETIME(6) NOT NULL,
att_history_event_id INT UNSIGNED NOT NULL,
INDEX(att_conf_id),
INDEX(att_history_event_id)
) ENGINE=MyISAM COMMENT='Attribute Configuration Events History Table';

DROP TABLE att_history_event;
CREATE TABLE IF NOT EXISTS att_history_event
(	
att_history_event_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
event VARCHAR(255) NOT NULL
) ENGINE=MyISAM COMMENT='Attribute history events description';

INSERT INTO att_history_event (event) VALUES
('add'),('remove'),('start'),('stop'),('crash');

CREATE TABLE IF NOT EXISTS att_parameter
(
att_conf_id INT UNSIGNED NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
label VARCHAR(255) NOT NULL DEFAULT '',
unit VARCHAR(64) NOT NULL DEFAULT '',
standard_unit VARCHAR(64) NOT NULL DEFAULT '1',
display_unit VARCHAR(64) NOT NULL DEFAULT '',
format VARCHAR(64) NOT NULL DEFAULT '',
archive_rel_change VARCHAR(64) NOT NULL DEFAULT '',
archive_abs_change VARCHAR(64) NOT NULL DEFAULT '',
archive_period VARCHAR(64) NOT NULL DEFAULT '',
description VARCHAR(255) NOT NULL DEFAULT '',
INDEX(recv_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Attribute configuration parameters';

CREATE TABLE IF NOT EXISTS att_scalar_double_ro
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r DOUBLE DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Double ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_double_rw
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r DOUBLE DEFAULT NULL,
value_w DOUBLE DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Double ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_array_double_ro
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r DOUBLE DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Double ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_array_double_rw
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r DOUBLE DEFAULT NULL,
value_w DOUBLE DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Double ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_int64_ro
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r BIGINT DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Int up to 64 bit ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_int64_rw
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r BIGINT DEFAULT NULL,
value_w BIGINT DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Int up to 64 bit ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_array_int64_ro
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r BIGINT DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Int up to 64 bit ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_array_int64_rw
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r BIGINT DEFAULT NULL,
value_w BIGINT DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Int up to 64 bit ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_int8_ro
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r TINYINT(1) DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Int up to 8 bit ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_int8_rw
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r TINYINT(1) DEFAULT NULL,
value_w TINYINT(1) DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Int up to 8 bit ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_array_int8_ro
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r TINYINT(1) DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Int up to 8 bit ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_array_int8_rw
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r TINYINT(1) DEFAULT NULL,
value_w TINYINT(1) DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Int up to 8 bit ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_string_ro
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r VARCHAR(16384) DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar String ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_string_rw
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r VARCHAR(16384) DEFAULT NULL,
value_w VARCHAR(16384) DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar String ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_array_string_ro
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r VARCHAR(16384) DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array String ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_array_string_rw
(
att_conf_id INT UNSIGNED NOT NULL,
data_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r VARCHAR(16384) DEFAULT NULL,
value_w VARCHAR(16384) DEFAULT NULL,
quality TINYINT(1) DEFAULT NULL,
error_desc VARCHAR(255) DEFAULT NULL,
INDEX(data_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array String ReadWrite Values Table';


