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

package org.tango.jhdb;

import org.tango.jhdb.data.HdbDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * The Hdb framework allows accessing data from Tango HDB++.
 *
 * <pre>
 * {@code
 * Hdb hdb = new Hdb();
 * try {
 *
 *   hdb.connect();
 *   String[] attList = hdb.getReader().getAttributeList();
 *   ArrayList<HdbData> data = hdb.getReader().getData(attName[0],"09/07/2015 12:00:00","10/07/2015 12:00:00");
 *   for(int i=0;i<data.size();i++)
 *     System.out.println("  Rec #"+i+" :"+data.get(i));
 *
 * } catch (HdbFailed e) {
 *   System.out.println(e.getMessage());
 * }
 *}
 * </pre>
 *
 * @author JL Pons
 */

public class Hdb {

  /**
   * Date format used in getDataFromDB calls
   */
  public final static SimpleDateFormat hdbDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  /** Verion Number */
  public final static double LIB_RELEASE = 1.2 ; // Let the space before the ';'

  /** Cassandra HDB++ */
  public  static final int HDB_CASSANDRA = 1;
  /** MySQL HDB++ */
  public  static final int HDB_MYSQL     = 2;
  /** Oracle HDB */
  public  static final int HDB_ORACLE    = 3;

  private int hdbType;
  private static final String[] hdbNames = { "No DB" , "Cassandra", "MySQL", "Oracle"};
  private HdbReader schema;

  /**
   * Constructs a Hdb Object
   */
  public Hdb() {
    hdbType = 0;
  }

  /**
   * Returns a handle to the HDB reader
   */
  public HdbReader getReader() {
    return schema;
  }

  /**
   * Returns type of connection
   */
  public int getDBType() {
    return hdbType;
  }

  /**
   * Returns type of connection
   */
  public String getDBTypeName() {
    return hdbNames[hdbType];
  }

  /**
   * Connects to a MySQL HDB.
   * @param host MySQL hostname
   * @param db Database name (default is "hdb")
   * @param user MySQL user name
   * @param passwd MySQL user password
   * @param port MySQL databse port (pass 0 for default Mysql port)
   * @throws HdbFailed in case of failure
   */
  public void connectMySQL(String host,String db,String user,String passwd,short port) throws HdbFailed {
    hdbType = HDB_MYSQL;
    schema = new MySQLSchema(host,db,user,passwd,port);
  }

  /**
   * Connects to a MySQL HDB.
   */
  public void connectMySQL() throws HdbFailed {
    hdbType = HDB_MYSQL;
    schema = new MySQLSchema(null,null,null,null,(short)0);
  }

  /**
   * Connects to a Cassandra HDB.
   * @param contacts List of contact points (at least one of the hostname of the cassandra cluster)
   * @param db Database name (default is "hdb")
   * @param user Cassandra user name
   * @param passwd Cassandra user password
   * @throws HdbFailed
   */
  public void connectCassandra(String[] contacts,String db,String user,String passwd) throws HdbFailed {
    hdbType = HDB_CASSANDRA;
    schema = new CassandraSchema(contacts,db,user,passwd);
  }

  /**
   * Connects to a Cassandra HDB.
   */
  public void connectCassandra() throws HdbFailed {
    hdbType = HDB_CASSANDRA;
    schema = new CassandraSchema(null,null,null,null);
  }

  /**
   * Connects to a Oracle HDB.
   */
  public void connectOracle() throws HdbFailed {
    hdbType = HDB_ORACLE;
    schema = new OracleSchema();
  }

  /**
   * Connect to HDB either using MySQL or Cassandra according to the following environment variables.
   *
   * HDB_TYPE  Connection type (MYSQL or CASSANDRA)
   * HDB_NAME  Database name (default is "hdb")
   * HDB_USER
   * HDB_PASSWORD
   *
   * MySQL specific
   * HDB_MYSQL_PORT
   * HDB_MYSQL_HOST
   *
   * Cassandra specific
   * HDB_CONTACT_POINTS
   *
   * @throws HdbFailed
   */
  public void connect() throws HdbFailed {

    String hdb = System.getenv("HDB_TYPE");
    if(hdb==null || hdb.isEmpty())
      throw new HdbFailed("HDB_TYPE variable not defined");

    if(hdb.equalsIgnoreCase("MYSQL")) {
      connectMySQL(null,null,null,null,(short)0);
    } else if(hdb.equalsIgnoreCase("CASSANDRA")) {
      connectCassandra(null, null, null, null);
    } else if(hdb.equalsIgnoreCase("ORACLE")) {
      connectOracle();
    } else {
      throw new HdbFailed("Wrong HDB_TYPE , MYSQL or CASSANDRA expected");
    }

  }

  public static void test(Hdb hdb,String start,String stop,String attName) throws HdbFailed {

    System.out.print("\n--------> " + attName + " ");
    HdbDataSet data = hdb.getReader().getData(attName,start,stop);
    String typeStr = "";
    if(data.size()>0) typeStr = HdbSigInfo.typeStr[data.get(0).getType()];
    System.out.println("(" + data.size() + " records) "  + typeStr);
    for(int i=0;i<data.size() && i<10;i++)
      System.out.println("  Rec #"+i+" :"+data.get(i));

  }


  public static void main(String[] args) {

    Hdb hdb = new Hdb();

    try {

      //hdb.connectMySQL("cassandra1","","","",(short)0);
      //hdb.connectCassandra();
      hdb.connectOracle();
      //hdb.connect();

      long t0 = System.currentTimeMillis();
      String[] attList = hdb.getReader().getAttributeList();
      long t1 = System.currentTimeMillis();
      System.out.println("Got "+attList.length+" attributes in " + (t1-t0) + "ms");

      //HdbSigInfo info = hdb.getDB().getSigInfo("tango://orion.esrf.fr:10000/sr/d-ct/1/current");
      //System.out.println("Info="+info);

      // Test correlated mode
      /*
      System.out.print("\n--------> Correlated ");
      HdbDataSet[] data = hdb.getReader().getData(new String[]{
          "tango://orion.esrf.fr:10000/sr/d-ct/1/current",
          "tango://orion.esrf.fr:10000/sr/d-ct/1/lifetime",
      },"01/08/2015 23:00:00", "01/08/2015 23:10:00",HdbReader.MODE_CORRELATED);
      System.out.println(" (" + data[0].size() + "/" + data[1].size());
      for(int i=0;i<data[0].size() && i<10;i++)
        System.out.println("  Rec #"+i+" :"+data[0].get(i) + " <=> " + data[1].get(i));
      */

      /*
      // Double RO
      test(hdb, "09/07/2015 23:00:00", "10/07/2015 01:00:00",
          "tango://orion.esrf.fr:10000/sr/d-ct/1/current");

      // Double RW
      test(hdb,"09/07/2015 12:00:00","09/07/2015 13:00:00",
          "tango://orion.esrf.fr:10000/elin/gun/hv/highvoltage");  

      // Float RO
      test(hdb,"09/07/2015 01:00:00","10/07/2015 01:00:00",
          "tango://orion.esrf.fr:10000/sr/d-temp/c25bpm/chamber1");

      // DoulbeArr RO
      test(hdb,"09/07/2015 12:00:00","09/07/2015 13:00:00",
          "tango://orion.esrf.fr:10000/sys/d-drops/ss1/harmonics");

      // DoulbeArr RW
      test(hdb,"09/07/2015 12:00:00","09/07/2015 13:00:00",
          "tango://orion.esrf.fr:10000/sr/st-v/all/current");

      // Long RO
      test(hdb,"09/07/2015 12:00:00","09/07/2015 19:00:00",
          "tango://orion.esrf.fr:10000/id/id/12/mode");

      // Long RW
      test(hdb,"09/07/2015 12:00:00","09/07/2015 19:00:00",
          "tango://orion.esrf.fr:10000/sys/machstat/tango/sr_mode");

      // String RO
      test(hdb,"09/07/2015 12:00:00","09/07/2015 19:00:00",
          "tango://orion.esrf.fr:10000/sr/rf-circ/tra1/status");

      // String RW
      test(hdb,"09/07/2015 12:00:00","09/07/2015 19:00:00",
          "tango://orion.esrf.fr:10000/sys/talker/2/text_to_talk");

      // INT8 or DevState RO
      test(hdb,"09/07/2015 12:00:00","10/07/2015 12:00:00",
          "tango://orion.esrf.fr:10000/sr/d-fofbcorrection/globalx/state");

      // INT8 or Boolean RO
      test(hdb,"09/07/2015 12:00:00","10/07/2015 12:00:00",
          "tango://orion.esrf.fr:10000/sr/d-emit/survey/isalarm");

      // INT64 or ULONG
      test(hdb,"09/07/2015 12:00:00","10/07/2015 12:00:00",
          "tango://orion.esrf.fr:10000/id-corr/12/correction/nberrors");

      System.out.println("\n--------> History config test: ");

      ArrayList<HdbSigParam> l = hdb.getReader().getParams("tango://orion.esrf.fr:10000/sr/d-ct/1/current",
          "05/06/2015 12:00:00",
          "10/07/2015 12:00:00");
      for(int i=0;i<l.size();i++)
        System.out.println(l.get(i));
      */

    } catch (HdbFailed e) {
      System.out.println(e.getMessage());
    }

    System.exit(0);

  }

}
