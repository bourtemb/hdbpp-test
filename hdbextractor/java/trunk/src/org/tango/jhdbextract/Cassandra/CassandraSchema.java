//+======================================================================
// $Source: $
//
// Project:   Tango
//
// Description:  java source code for HDB extraction library.
//
// $Author: pons $
//
// Copyright (C) :      2015
//						European Synchrotron Radiation Facility
//                      BP 220, Grenoble 38043
//                      FRANCE
//
// This file is part of Tango.
//
// Tango is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Tango is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Tango.  If not, see <http://www.gnu.org/licenses/>.
//
// $Revision $
//
//-======================================================================
package org.tango.jhdbextract.Cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import org.tango.jhdbextract.DbSchema;
import org.tango.jhdbextract.HdbFailed;
import org.tango.jhdbextract.HdbSigInfo;
import org.tango.jhdbextract.HdbSigParam;
import org.tango.jhdbextract.data.HdbData;
import org.tango.jhdbextract.data.HdbDataSet;

import java.util.*;

/**
 * Cassandra database access
 */
public class CassandraSchema extends DbSchema {

  public static final String   DEFAULT_DB_NAME = "hdb";
  public static final String[] DEFAULT_CONTACT_POINTS = {"cassandra2"};

  private Cluster cluster;
  private Session session;

  private final static String[] tableNames = {

      "NONE",
      "att_scalar_devdouble_ro",
      "att_scalar_devdouble_rw",
      "att_array_devdouble_ro",
      "att_array_devdouble_rw",
      "att_scalar_devlong64_ro",
      "att_scalar_devlong64_rw",
      "att_array_devlong64_ro",
      "att_array_devlong64_rw",
      "att_scalar_devchar_ro",
      "att_scalar_devchar_rw",
      "att_array_devchar_ro",
      "att_array_devchar_rw",
      "att_scalar_devstring_ro",
      "att_scalar_devstring_rw",
      "att_array_devstring_ro",
      "att_array_devstring_rw",
      "att_scalar_devfloat_ro",
      "att_scalar_devfloat_rw",
      "att_array_devfloat_ro",
      "att_array_devfloat_rw",
      "att_scalar_devuchar_ro",
      "att_scalar_devuchar_rw",
      "att_array_devuchar_ro",
      "att_array_devuchar_rw",
      "att_scalar_devshort_ro",
      "att_scalar_devshort_rw",
      "att_array_devshort_ro",
      "att_array_devshort_rw",
      "att_scalar_devushort_ro",
      "att_scalar_devushort_rw",
      "att_array_devushort_ro",
      "att_array_devushort_rw",
      "att_scalar_devlong_ro",
      "att_scalar_devlong_rw",
      "att_array_devlong_ro",
      "att_array_devlong_rw",
      "att_scalar_devulong_ro",
      "att_scalar_devulong_rw",
      "att_array_devulong_ro",
      "att_array_devulong_rw",
      "att_scalar_devstate_ro",
      "att_scalar_devstate_rw",
      "att_array_devstate_ro",
      "att_array_devstate_rw",
      "att_scalar_devboolean_ro",
      "att_scalar_devboolean_rw",
      "att_array_devboolean_ro",
      "att_array_devboolean_rw",
      "att_scalar_devencoded_ro",
      "att_scalar_devencoded_rw",
      "att_array_devencoded_ro",
      "att_array_devencoded_rw",
      "att_scalar_devulong64_ro",
      "att_scalar_devulong64_rw",
      "att_array_devulong64_ro",
      "att_array_devulong64_rw"

  };

  public CassandraSchema(String[] contacts,String db,String user,String passwd) throws HdbFailed {

    // Contact points
    if(contacts==null || contacts.length==0) {

      // Try to get contact points from environment variable
      String str = System.getenv("CONTACT_POINTS");

      if (str!=null && !str.isEmpty()) {
        StringTokenizer stk = new StringTokenizer(str, ",");
        contacts = new String[stk.countTokens()];
        int i=0;
        while (stk.hasMoreTokens())
          contacts[i++] = stk.nextToken();
      } else {
        contacts = DEFAULT_CONTACT_POINTS;
      }

    }

    // Databse name
    if(db==null || db.isEmpty()) {
      db = System.getenv("HDB_NAME");
      if (db==null || db.isEmpty())
        db = DEFAULT_DB_NAME;
    }

    //  Build cluster from contact points
    try {

      Cluster.Builder builder = Cluster.builder();
      for (String contactPoint : contacts)
        builder.addContactPoint(contactPoint);
      cluster = builder.build();

      //  Set protocol
      cluster.getConfiguration()
          .getProtocolOptions()
          .setCompression(ProtocolOptions.Compression.LZ4);

      //  Build session on database
      session = cluster.connect(db);

    } catch (Exception e) {
      throw new HdbFailed(e.getMessage());
    }

  }
  public String[] getAttributeList() throws HdbFailed {

    ArrayList<String> list = new ArrayList<String>();

    String query = "SELECT cs_name,att_name FROM att_conf";

    ResultSet resultSet;
    try {
      resultSet = session.execute(query);
      for (Row row : resultSet) {
        String csName = row.getString("cs_name");
        String attName = row.getString("att_name");
        list.add("tango://"+csName+"/"+attName);
      }
    } catch (DriverException e) {
      throw new HdbFailed(e.getMessage());
    }

    String[] retStr = new String[list.size()];
    for(int i=0;i<retStr.length;i++)
      retStr[i]=list.get(i);

    return retStr;

  }

  public HdbSigInfo getSigInfo(String attName) throws HdbFailed {

    HdbSigInfo ret = new HdbSigInfo();

    if(!attName.startsWith("tango://"))
      throw new HdbFailed("Fully qualified attribute name expected (eg:tango://hostname:port/domain/family/member)");

    attName = attName.substring(8);
    String[] fields = attName.split("/");
    if(fields.length!=5)
      throw new HdbFailed("Invalid attribute name syntax (eg:tango://hostname:port/domain/family/member)");

    String csName = fields[0];
    String shortAttName = fields[1] + "/" + fields[2] + "/" + fields[3] + "/" + fields[4];

    String query = "SELECT att_conf_id,data_type FROM att_conf WHERE cs_name='"+csName+
                   "' AND att_name='" + shortAttName + "'";

    ResultSet resultSet;
    try {

      resultSet = session.execute(query);
      Row row = resultSet.one();

      if(row==null)
       throw new HdbFailed("Signal not found");

      ret.sigId = row.getUUID("att_conf_id").toString();
      ret.type = HdbSigInfo.typeFromName(row.getString("data_type"));

    } catch (DriverException e) {
      throw new HdbFailed(e.getMessage());
    }

    return ret;

  }

  public HdbDataSet findErrors(String attName,
                               String start_date,
                               String stop_date) throws HdbFailed {
    throw new HdbFailed("Not implemented");
  }

  public ArrayList<HdbSigParam> getParams(String attName,
                                          String start_date,
                                          String stop_date) throws HdbFailed {

    HdbSigInfo sigInfo = getSigInfo(attName);

    String query = "SELECT recv_time,recv_time_us,insert_time,insert_time_us,label,unit,standard_unit,display_unit,format,"+
        "archive_rel_change,archive_abs_change,archive_period,description" +
        " FROM att_parameter " +
        " WHERE att_conf_id=" + UUID.fromString(sigInfo.sigId) +
        " AND recv_time>='" + toDBDate(start_date) + "'" +
        " AND recv_time<='" + toDBDate(stop_date) + "'";

    ArrayList<HdbSigParam> ret = new ArrayList<HdbSigParam>();

    ResultSet resultSet;
    try {

      resultSet = session.execute(query);

      for(Row rw:resultSet) {

        HdbSigParam hd = new HdbSigParam();
        hd.recvTime = timeValue(rw.getDate(0), rw.getInt(1));
        hd.insertTime = timeValue(rw.getDate(2), rw.getInt(3));
        hd.label = rw.getString(4);
        hd.unit = rw.getString(5);
        hd.standard_unit = rw.getString(6);
        hd.display_unit = rw.getString(7);
        hd.format = rw.getString(8);
        hd.archive_rel_change = rw.getString(9);
        hd.archive_abs_change = rw.getString(10);
        hd.archive_period = rw.getString(11);
        hd.description = rw.getString(12);

        ret.add(hd);

      }

    } catch (DriverException e) {
      throw new HdbFailed("Failed to get parameter history: "+e.getMessage());
    }

    return ret;

  }

  public HdbDataSet getData(String attName,
                            String start_date,
                            String stop_date,
                            boolean notify) throws HdbFailed {

    HdbSigInfo sigInfo = getSigInfo(attName);

    boolean isRW = HdbSigInfo.isRWType(sigInfo.type);
    String rwField = isRW?",value_w":"";
    String query = "SELECT data_time,data_time_us,recv_time,recv_time_us,insert_time,insert_time_us,error_desc,quality,value_r"+rwField+
        " FROM " + tableNames[sigInfo.type] +
        " WHERE att_conf_id = ?" +
        " AND period = ?" +
        " AND data_time >= ?" +
        " AND data_time <= ?";

    PreparedStatement preparedStatement = session.prepare(query);
    ArrayList<Period> periods = Period.getPeriods(start_date,stop_date);
    ArrayList<ResultSetFuture> resultSetFutures = new ArrayList<ResultSetFuture>();

    // Launch asynchronous calls
    for(Period p : periods) {
      for(String part: p.partitions) {
        BoundStatement boundStatement = preparedStatement.bind(UUID.fromString(sigInfo.sigId),
                                                               part,
                                                               p.start,
                                                               p.end);
        boundStatement.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        //boundStatement.setFetchSize(3600*24*10);
        resultSetFutures.add(session.executeAsync(boundStatement));
      }
    }

    // Wait end of result
    ArrayList<ResultSet> resultSets = new ArrayList<ResultSet>();

    try {
      for(ResultSetFuture sf: resultSetFutures)
        resultSets.add(sf.getUninterruptibly());
    } catch (NoHostAvailableException e1) {
      throw new HdbFailed("Error (NoHostAvailable): " + e1.getMessage());
    } catch (QueryExecutionException e2) {
      throw new HdbFailed("Error (QueryExecution): " + e2.getMessage());
    } catch (QueryValidationException e3) {
      throw new HdbFailed("Error (QueryValidation): " + e3.getMessage());
    }

    ArrayList<HdbData> ret = new ArrayList<HdbData>();
    ArrayList<Object> value = new ArrayList<Object>();
    ArrayList<Object> wvalue = null;
    if(isRW) wvalue = new ArrayList<Object>();

    try {

      for (ResultSet rs : resultSets) {
        for (Row rw : rs) {

          HdbData hd = HdbData.createData(sigInfo.type);

          switch(sigInfo.type) {

            case HdbSigInfo.TYPE_SCALAR_BOOLEAN_RO:
            case HdbSigInfo.TYPE_SCALAR_BOOLEAN_RW:
              setValue(value,rw.getBool(8));
              if(isRW) setValue(wvalue,rw.getBool(9));
              break;

            case HdbSigInfo.TYPE_ARRAY_BOOLEAN_RO:
            case HdbSigInfo.TYPE_ARRAY_BOOLEAN_RW:
              setValueBoolean(value, rw.getList(8, Boolean.class));
              if(isRW) setValueBoolean(wvalue, rw.getList(9, Boolean.class));
              break;

            case HdbSigInfo.TYPE_SCALAR_SHORT_RO:
            case HdbSigInfo.TYPE_SCALAR_SHORT_RW:
            case HdbSigInfo.TYPE_SCALAR_UCHAR_RO:
            case HdbSigInfo.TYPE_SCALAR_UCHAR_RW:
              setValue(value,(short)rw.getInt(8));
              if(isRW) setValue(wvalue,(short)rw.getInt(9));
              break;

            case HdbSigInfo.TYPE_ARRAY_SHORT_RO:
            case HdbSigInfo.TYPE_ARRAY_SHORT_RW:
            case HdbSigInfo.TYPE_ARRAY_UCHAR_RO:
            case HdbSigInfo.TYPE_ARRAY_UCHAR_RW:
              setValueShort(value, rw.getList(8, Integer.class));
              if(isRW) setValueShort(wvalue, rw.getList(9, Integer.class));
              break;

            case HdbSigInfo.TYPE_SCALAR_LONG_RO:
            case HdbSigInfo.TYPE_SCALAR_LONG_RW:
            case HdbSigInfo.TYPE_SCALAR_USHORT_RO:
            case HdbSigInfo.TYPE_SCALAR_USHORT_RW:
            case HdbSigInfo.TYPE_SCALAR_STATE_RO:
            case HdbSigInfo.TYPE_SCALAR_STATE_RW:
              setValue(value, rw.getInt(8));
              if(isRW) setValue(wvalue, rw.getInt(9));
              break;

            case HdbSigInfo.TYPE_ARRAY_LONG_RO:
            case HdbSigInfo.TYPE_ARRAY_LONG_RW:
            case HdbSigInfo.TYPE_ARRAY_USHORT_RO:
            case HdbSigInfo.TYPE_ARRAY_USHORT_RW:
            case HdbSigInfo.TYPE_ARRAY_STATE_RO:
            case HdbSigInfo.TYPE_ARRAY_STATE_RW:
              setValueInteger(value, rw.getList(8, Integer.class));
              if(isRW) setValueInteger(wvalue, rw.getList(9, Integer.class));
              break;

            case HdbSigInfo.TYPE_SCALAR_LONG64_RO:
            case HdbSigInfo.TYPE_SCALAR_LONG64_RW:
            case HdbSigInfo.TYPE_SCALAR_ULONG_RO:
            case HdbSigInfo.TYPE_SCALAR_ULONG_RW:
              setValue(value, rw.getLong(8));
              if(isRW) setValue(wvalue, rw.getLong(9));
              break;

            case HdbSigInfo.TYPE_ARRAY_LONG64_RO:
            case HdbSigInfo.TYPE_ARRAY_LONG64_RW:
            case HdbSigInfo.TYPE_ARRAY_ULONG_RO:
            case HdbSigInfo.TYPE_ARRAY_ULONG_RW:
              setValueLong(value, rw.getList(8, Long.class));
              if(isRW) setValueLong(wvalue, rw.getList(9, Long.class));
              break;

            case HdbSigInfo.TYPE_SCALAR_DOUBLE_RO:
            case HdbSigInfo.TYPE_SCALAR_DOUBLE_RW:
              setValue(value,rw.getDouble(8));
              if(isRW) setValue(wvalue,rw.getDouble(9));
              break;

            case HdbSigInfo.TYPE_ARRAY_DOUBLE_RO:
            case HdbSigInfo.TYPE_ARRAY_DOUBLE_RW:
              setValueDouble(value, rw.getList(8, Double.class));
              if(isRW) setValueDouble(wvalue, rw.getList(9, Double.class));
              break;

            case HdbSigInfo.TYPE_SCALAR_FLOAT_RO:
            case HdbSigInfo.TYPE_SCALAR_FLOAT_RW:
              setValue(value,rw.getFloat(8));
              if(isRW) setValue(wvalue,rw.getFloat(9));
              break;

            case HdbSigInfo.TYPE_ARRAY_FLOAT_RO:
            case HdbSigInfo.TYPE_ARRAY_FLOAT_RW:
              setValueFloat(value, rw.getList(8, Float.class));
              if(isRW) setValueFloat(wvalue, rw.getList(9, Float.class));
              break;

            case HdbSigInfo.TYPE_SCALAR_STRING_RO:
            case HdbSigInfo.TYPE_SCALAR_STRING_RW:
              setValue(value,rw.getString(8));
              if(isRW) setValue(wvalue,rw.getString(9));
              break;

            case HdbSigInfo.TYPE_ARRAY_STRING_RO:
            case HdbSigInfo.TYPE_ARRAY_STRING_RW:
              setValueString(value, rw.getList(8, String.class));
              if(isRW) setValueString(wvalue, rw.getList(9, String.class));
              break;

          }

          hd.parse(
              timeValue(rw.getDate(0), rw.getInt(1)), //Tango timestamp
              timeValue(rw.getDate(2), rw.getInt(3)), //Event receive timestamp
              timeValue(rw.getDate(4), rw.getInt(5)), //Recording timestamp
              rw.getString(6),                   // Error string
              rw.getInt(7),                      // Quality value
              value,                             // Read value
              wvalue                             // Write value
          );
          ret.add(hd);

        }
      }

    } catch (DriverException e) {
      throw new HdbFailed("Failed to get data: " + e.getMessage());
    }

    return new HdbDataSet(ret);

  }

  private void setValue(ArrayList<Object> value,double d) {
    value.clear();
    value.add(d);
  }

  private void setValueDouble(ArrayList<Object> value,List<Double> d) {
    value.clear();
    value.addAll(d);
  }

  private void setValue(ArrayList<Object> value,float d) {
    value.clear();
    value.add(d);
  }

  private void setValueFloat(ArrayList<Object> value,List<Float> d) {
    value.clear();
    value.addAll(d);
  }

  private void setValue(ArrayList<Object> value,boolean b) {
    value.clear();
    value.add(b);
  }

  private void setValueBoolean(ArrayList<Object> value,List<Boolean> b) {
    value.clear();
    value.addAll(b);
  }

  private void setValue(ArrayList<Object> value,int i) {
    value.clear();
    value.add(i);
  }

  private void setValueInteger(ArrayList<Object> value,List<Integer> d) {
    value.clear();
    value.addAll(d);
  }

  private void setValue(ArrayList<Object> value,long l) {
    value.clear();
    value.add(l);
  }

  private void setValueLong(ArrayList<Object> value,List<Long> l) {
    value.clear();
    value.addAll(l);
  }

  private void setValue(ArrayList<Object> value,short s) {
    value.clear();
    value.add(s);
  }

  private void setValueShort(ArrayList<Object> value,List<Integer> d) {
    value.clear();
    for(int i=0;i<d.size();i++)
      value.add(d.get(i).shortValue());
  }

  private void setValue(ArrayList<Object> value,String s) {
    value.clear();
    value.add(s);
  }

  private void setValueString(ArrayList<Object> value,List<String> d) {
    value.clear();
    value.addAll(d);
  }

  private long timeValue(Date t,int us) {

    long ret = t.getTime();
    ret = (ret / 1000) * 1000000;
    ret += us;
    return ret;

  }

  private String toDBDate(String date) {

    // In:   09/07/2015 12:00:00
    // Out:  2015-07-09 12:00:00
    return date.substring(6,10) + "-" + date.substring(3,5) + "-" +
        date.substring(0,2) + " " + date.substring(11,19);

  }

}