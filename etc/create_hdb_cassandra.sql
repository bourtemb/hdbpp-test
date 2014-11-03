CREATE KEYSPACE IF NOT EXISTS hdb WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'DC1' : 3 };

USE hdb;

CREATE TYPE IF NOT EXISTS DevEncoded (
 encoded_format text,
 encoded_data blob
);

CREATE TABLE IF NOT EXISTS att_conf (
cs_name text,
att_name text,
att_conf_id timeuuid,
data_type text,   -- data_types set<text> in the future?
PRIMARY KEY (cs_name, att_name)
) 
WITH comment='Attribute Configuration Table';

CREATE INDEX on att_conf(data_type);

CREATE TABLE IF NOT EXISTS att_history
(
att_conf_id timeuuid,
time timestamp,
time_us int,
event text, -- 'add','remove','start' or 'stop'
PRIMARY KEY(att_conf_id, time, time_us)
) 
WITH comment='Attribute Configuration Events History Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };


CREATE TABLE IF NOT EXISTS att_scalar_DevBoolean_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r boolean,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
)
WITH comment='Scalar DevBoolean ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevBoolean_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r boolean,
value_w boolean,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevBoolean ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevUChar_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r int,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) WITH comment='Scalar DevUChar ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_DevUChar_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r int,
value_w int,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevUChar ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevShort_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r int,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) WITH comment='Scalar DevShort ReadOnly Values Table';

CREATE TABLE IF NOT EXISTS att_scalar_DevShort_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r int,
value_w int,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevShort ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevUShort_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r int,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevUShort ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevUShort_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r int,
value_w int,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevUShort ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevLong_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r int,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevLong ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevLong_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r int,
value_w int,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevLong ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevULong_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r bigint,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevULong ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevULong_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r bigint,
value_w bigint,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevULong ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevLong64_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r bigint,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevLong64 ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevLong64_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r bigint,
value_w bigint,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevLong64 ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevULong64_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r bigint,              // issue here with very big numbers
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevULong64 ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevULong64_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r bigint, // issue here with very big numbers
value_w bigint, // issue here with very big numbers
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevLong64 ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevFloat_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r float,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevFloat ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevFloat_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r float,
value_w float,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevFloat ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevDouble_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r double,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevDouble ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevDouble_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r double,
value_w double,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevDouble ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevString_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r text,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevString ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevString_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r text,
value_w text,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevString ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevState_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r int,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevState ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevState_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r int,
value_w int,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevState ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevEncoded_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r frozen<DevEncoded>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevEncoded ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_scalar_DevEncoded_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
value_r frozen<DevEncoded>,
value_w frozen<DevEncoded>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Scalar DevEncoded ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevBoolean_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<boolean>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevBoolean ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevBoolean_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<boolean>,
value_w list<boolean>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevBoolean ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevUChar_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<int>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
)
WITH comment='Array DevUChar ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevUChar_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<int>,
value_w list<int>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevUChar ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevShort_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<int>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevShort ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevShort_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<int>,
value_w list<int>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevShort ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevUShort_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<int>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevUShort ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevUShort_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<int>,
value_w list<int>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevUShort ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevLong_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<int>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevLong ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevLong_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<int>,
value_w list<int>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevLong ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevULong_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<bigint>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevULong ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevULong_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<bigint>,
value_w list<bigint>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevULong ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevLong64_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<bigint>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevLong64 ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevLong64_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<bigint>,
value_w list<bigint>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevLong64 ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevULong64_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<bigint>,  // issue with very big numbers
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevULong64 ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevULong64_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<bigint>, // issue with very big numbers
value_w list<bigint>, // issue with very big numbers
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevULong64 ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevFloat_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<float>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevFloat ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevFloat_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<float>,
value_w list<float>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevFloat ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevDouble_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<double>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevDouble ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevDouble_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<double>,
value_w list<double>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevDouble ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevString_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<text>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevString ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevString_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<text>,
value_w list<text>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevString ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevState_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<int>, // Store a special type here 
                   // where we could store an int and a string?
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevState ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevState_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<int>,// Store a special type here 
value_w list<int>,// where we could store an int and a string?
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevState ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevEncoded_ro (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<frozen<DevEncoded>>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevEncoded ReadOnly Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

CREATE TABLE IF NOT EXISTS att_array_DevEncoded_rw (
att_conf_id timeuuid,
period text,
event_time timestamp,
event_time_us int,
recv_time timestamp,
recv_time_us int,
insert_time timestamp,
insert_time_us int,
dim_x int,
dim_y int,
value_r list<frozen<DevEncoded>>,
value_w list<frozen<DevEncoded>>,
PRIMARY KEY ((att_conf_id ,period),event_time,event_time_us)
) 
WITH comment='Array DevEncoded ReadWrite Values Table'
AND compaction = {  'class' :  'LeveledCompactionStrategy' };

