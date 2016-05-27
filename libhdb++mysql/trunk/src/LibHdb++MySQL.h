//=============================================================================
//
// file :        LibHdb++MySQL.h
//
// description : Include for the LibHdb++MySQL library.
//
// Author: Graziano Scalamera
//
// $Revision: 1.1 $
//
// $Log: LibHdb++MySQL.h,v $
// Revision 1.1  2014-03-07 13:17:22  graziano
// first commit
//
//
//
//
//=============================================================================

#ifndef _HDBPP_MYSQL_H
#define _HDBPP_MYSQL_H

#include <mysql.h>
#include "LibHdb++.h"

#include <string>
#include <iostream>
#include <sstream>
#include <vector>
#include <map>
#include <stdint.h>

//Tango:
#include <tango.h>
//#include <event.h>

#define TYPE_SCALAR					"scalar"
#define TYPE_ARRAY					"array"

#define TYPE_DEV_BOOLEAN			"devboolean"
#define TYPE_DEV_UCHAR				"devuchar"
#define TYPE_DEV_SHORT				"devshort"
#define TYPE_DEV_USHORT				"devushort"
#define TYPE_DEV_LONG				"devlong"
#define TYPE_DEV_ULONG				"devulong"
#define TYPE_DEV_LONG64				"devlong64"
#define TYPE_DEV_ULONG64			"devulong64"
#define TYPE_DEV_FLOAT				"devfloat"
#define TYPE_DEV_DOUBLE				"devdouble"
#define TYPE_DEV_STRING				"devstring"
#define TYPE_DEV_STATE				"devstate"
#define TYPE_DEV_ENCODED			"devencoded"

#define TYPE_RO						"ro"
#define TYPE_RW						"rw"

#define EVENT_ADD					"add"
#define EVENT_REMOVE				"remove"
#define EVENT_START					"start"
#define EVENT_STOP					"stop"
#define EVENT_CRASH					"crash"
#define EVENT_PAUSE					"pause"

//######## att_conf ########
#define CONF_TABLE_NAME				"att_conf"
#define CONF_COL_ID					"att_conf_id"
#define CONF_COL_NAME				"att_name"
#define CONF_COL_TYPE_ID			"att_conf_data_type_id"
#define CONF_COL_TTL				"att_ttl"
#define CONF_COL_FACILITY			"facility"
#define CONF_COL_DOMAIN				"domain"
#define CONF_COL_FAMILY				"family"
#define CONF_COL_MEMBER				"member"
#define CONF_COL_LAST_NAME			"name"

//######## att_conf_data_type ########
#define CONF_TYPE_TABLE_NAME		"att_conf_data_type"
#define CONF_TYPE_COL_TYPE_ID		"att_conf_data_type_id"
#define CONF_TYPE_COL_TYPE			"data_type"

//######## att_history ########
#define HISTORY_TABLE_NAME			"att_history"
#define HISTORY_COL_ID				"att_conf_id"
#define HISTORY_COL_EVENT_ID		"att_history_event_id"
#define HISTORY_COL_TIME			"time"

//######## att_history_event ########
#define HISTORY_EVENT_TABLE_NAME	"att_history_event"
#define HISTORY_EVENT_COL_EVENT_ID	"att_history_event_id"
#define HISTORY_EVENT_COL_EVENT		"event"

//######## att_scalar_... ########
#define SC_COL_ID					"att_conf_id"
#define SC_COL_INS_TIME				"insert_time"
#define SC_COL_RCV_TIME				"recv_time"
#define SC_COL_EV_TIME				"data_time"
#define SC_COL_VALUE_R				"value_r"
#define SC_COL_VALUE_W				"value_w"
#define SC_COL_QUALITY				"quality"
#define SC_COL_ERROR_DESC			"error_desc"



//######## att_array_... ########
#define ARR_COL_ID					"att_conf_id"
#define ARR_COL_INS_TIME			"insert_time"
#define ARR_COL_RCV_TIME			"recv_time"
#define ARR_COL_EV_TIME				"data_time"
#define ARR_COL_VALUE_R				"value_r"
#define ARR_COL_VALUE_W				"value_w"
#define ARR_COL_IDX					"idx"
#define ARR_COL_DIMX_R				"dim_x_r"
#define ARR_COL_DIMY_R				"dim_y_r"
#define ARR_COL_DIMX_W				"dim_x_w"
#define ARR_COL_DIMY_W				"dim_y_w"
#define ARR_COL_QUALITY				"quality"
#define ARR_COL_ERROR_DESC			"error_desc"


//######## att_parameter ########
#define PARAM_TABLE_NAME				"att_parameter"
#define PARAM_COL_ID					"att_conf_id"
#define PARAM_COL_INS_TIME				"insert_time"
#define PARAM_COL_EV_TIME				"recv_time"
#define PARAM_COL_LABEL					"label"
#define PARAM_COL_UNIT					"unit"
#define PARAM_COL_STANDARDUNIT			"standard_unit"
#define PARAM_COL_DISPLAYUNIT			"display_unit"
#define PARAM_COL_FORMAT				"format"
#define PARAM_COL_ARCHIVERELCHANGE		"archive_rel_change"
#define PARAM_COL_ARCHIVEABSCHANGE		"archive_abs_change"
#define PARAM_COL_ARCHIVEPERIOD			"archive_period"
#define PARAM_COL_DESCRIPTION			"description"



class HdbPPMySQL : public AbstractDB
{
private:

	MYSQL *dbp;
	string m_dbname;
	map<string,int> attr_ID_map;
	
	string get_only_attr_name(string str);
	string get_only_tango_host(string str);
#ifndef _MULTI_TANGO_HOST
	string remove_domain(string facility);
	string add_domain(string facility);
#endif
	void string_explode(string str, string separator, vector<string>* results);
	void string_vector2map(vector<string> str, string separator, map<string,string>* results);

	string get_data_type(int type/*DEV_DOUBLE, DEV_STRING, ..*/, int format/*SCALAR, SPECTRUM, ..*/, int write_type/*READ, READ_WRITE, ..*/);
	string get_table_name(int type/*DEV_DOUBLE, DEV_STRING, ..*/, int format/*SCALAR, SPECTRUM, ..*/, int write_type/*READ, READ_WRITE, ..*/);

public:

	~HdbPPMySQL();

	HdbPPMySQL(vector<string> configuration);

	//void connect_db(string host, string user, string password, string dbname);
	int find_attr_id(string facility, string attr_name, int &ID);
	int find_attr_id_type(string facility, string attr_name, int &ID, string attr_type);
	int find_last_event(int ID, string &event);
	virtual int insert_Attr(Tango::EventData *data, HdbEventDataType ev_data_type);
	virtual int insert_param_Attr(Tango::AttrConfEventData *data, HdbEventDataType ev_data_type);
	virtual int configure_Attr(string name, int type/*DEV_DOUBLE, DEV_STRING, ..*/, int format/*SCALAR, SPECTRUM, ..*/, int write_type/*READ, READ_WRITE, ..*/, unsigned int ttl/*hours, 0=infinity*/);
	virtual int event_Attr(string name, unsigned char event);

private:
	template <typename Type> int extract_and_store(string attr_name, Tango::EventData *data, int quality/*ATTR_VALID, ATTR_INVALID, ..*/, string error_desc, Tango::AttrDataFormat data_format/*SCALAR, SPECTRUM, ..*/, int write_type/*READ, READ_WRITE, ..*/, Tango::AttributeDimension attr_r_dim, Tango::AttributeDimension attr_w_dim, double ev_time, double rcv_time, string table_name, enum_field_types mysql_value_type, bool _is_unsigned, bool isNull);
	template <typename Type> int store_scalar(string attr, vector<Type> value_r, vector<Type> value_w, int quality/*ATTR_VALID, ATTR_INVALID, ..*/, string error_desc, int write_type/*READ, READ_WRITE, ..*/, double ev_time, double rcv_time, string table_name, enum_field_types mysql_value_type, bool is_unsigned, bool isNull=false);
	template <typename Type> int store_array(string attr, vector<Type> value_r, vector<Type> value_w, int quality/*ATTR_VALID, ATTR_INVALID, ..*/, string error_desc, int write_type/*READ, READ_WRITE, ..*/, Tango::AttributeDimension attr_r_dim, Tango::AttributeDimension attr_w_dim, double ev_time, double rcv_time, string table_name, enum_field_types mysql_value_type, bool _is_unsigned, bool isNull=false);
	int store_scalar_string(string attr, vector<string> value_r, vector<string> value_w, int quality/*ATTR_VALID, ATTR_INVALID, ..*/, string error_desc, int write_type/*READ, READ_WRITE, ..*/, double ev_time, double rcv_time, string table_name, bool isNull=false);
	int store_array_string(string attr, vector<string> value_r, vector<string> value_w, int quality/*ATTR_VALID, ATTR_INVALID, ..*/, string error_desc, int write_type/*READ, READ_WRITE, ..*/, Tango::AttributeDimension attr_r_dim, Tango::AttributeDimension attr_w_dim, double ev_time, double rcv_time, string table_name, bool isNull=false);
	template <typename Type> bool is_nan_or_inf(Type val);
};

class HdbPPMySQLFactory : public DBFactory
{

public:
	virtual AbstractDB* create_db(vector<string> configuration);

};

#endif

