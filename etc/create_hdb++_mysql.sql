-----------------------------------------------------------------
----------------CONCRETE TABLE INHERITANCE-----------------------
-----------------------------------------------------------------

CREATE TABLE IF NOT EXISTS att_conf
(
att_conf_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
att_name VARCHAR(255) UNIQUE NOT NULL,
data_type ENUM('scalar_double_ro','scalar_double_rw','array_double_ro','array_double_rw',
	'scalar_int64_ro','scalar_int64_rw','array_int64_ro','array_int64_rw',
	'scalar_int8_ro','scalar_int8_rw','array_int8_ro','array_int8_rw',
	'scalar_string_ro','scalar_string_rw','array_string_ro','array_string_rw') NOT NULL
) ENGINE=MyISAM COMMENT='Attribute Configuration Table';

CREATE TABLE IF NOT EXISTS att_history
(
att_conf_id INT UNSIGNED NOT NULL,
time DATETIME(6) NOT NULL,
event ENUM('add','remove','start','stop') NOT NULL,
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Attribute Configuration Events History Table';

CREATE TABLE IF NOT EXISTS att_scalar_double_ro
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r DOUBLE DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Double ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_double_rw
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r DOUBLE DEFAULT NULL,
value_w DOUBLE DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Double ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_array_double_ro
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r DOUBLE DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Double ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_array_double_rw
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r DOUBLE DEFAULT NULL,
value_w DOUBLE DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Double ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_int64_ro
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r BIGINT DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Int up to 64 bit ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_int64_rw
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r BIGINT DEFAULT NULL,
value_w BIGINT DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Int up to 64 bit ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_array_int64_ro
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r BIGINT DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Int up to 64 bit ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_array_int64_rw
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r BIGINT DEFAULT NULL,
value_w BIGINT DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Int up to 64 bit ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_int8_ro
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r TINYINT(1) DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Int up to 8 bit ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_int8_rw
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r TINYINT(1) DEFAULT NULL,
value_w TINYINT(1) DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar Int up to 8 bit ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_array_int8_ro
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r TINYINT(1) DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Int up to 8 bit ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_array_int8_rw
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r TINYINT(1) DEFAULT NULL,
value_w TINYINT(1) DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array Int up to 8 bit ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_string_ro
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r VARCHAR(16384) DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar String ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_string_rw
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
value_r VARCHAR(16384) DEFAULT NULL,
value_w VARCHAR(16384) DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Scalar String ReadWrite Values Table';

CREATE TABLE IF NOT EXISTS att_array_string_ro
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r VARCHAR(16384) DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array String ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_array_string_rw
(
att_conf_id INT UNSIGNED NOT NULL,
event_time DATETIME(6) NOT NULL,
recv_time DATETIME(6) NOT NULL,
insert_time DATETIME(6) NOT NULL,
idx INT UNSIGNED NOT NULL,
dim_x INT UNSIGNED NOT NULL,
dim_y INT UNSIGNED NOT NULL DEFAULT 0,
value_r VARCHAR(16384) DEFAULT NULL,
value_w VARCHAR(16384) DEFAULT NULL,
INDEX(event_time),
INDEX(att_conf_id)
) ENGINE=MyISAM COMMENT='Array String ReadWrite Values Table';


