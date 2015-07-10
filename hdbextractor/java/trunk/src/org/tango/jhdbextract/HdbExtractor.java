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

package org.tango.jhdbextract;

import org.tango.jhdbextract.MySQL.MySQLSchema;
import org.tango.jhdbextract.data.HdbData;

import java.util.ArrayList;

/**
 * The HdbExtractor framework allows fetching data from Tango HDB++.
 *
 * @author JL Pons
 */

public class HdbExtractor {

  /** Cassandra HDB */
  public  static final int HDB_CASSANDRA = 1;
  /** MySQL HDB */
  public  static final int HDB_MYSQL     = 2;

  private static int hdbType;
  private static final String[] hdbNames = { "No connection" , "Cassandra", "Mysql"};
  private DbSchema schema;

  /**
   * Constructs a HdbExtractor.
   */
  public HdbExtractor() {
    hdbType = 0;
  }

  /**
   * Returns a handle to the used schema
   */
  public DbSchema getDB() {
    return schema;
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
   *  Connects to a Cassandra HDB.
   * @param contacts List of contact points (at least one of the hostname of the cassandra cluster)
   * @param db Database name (default is "hdb")
   * @param user Cassandra user name
   * @param passwd Cassandra user password
   * @param port Cassandra databse port (pass 0 for default Cassandra port)
   * @throws HdbFailed
   */
  public void connectCassandra(String[] contacts,String db,String user,String passwd,short port) throws HdbFailed {
    hdbType = HDB_CASSANDRA;
  }

  /**
   * Connect to HDB either using MySQL or Cassandra according to the following environment variables.
   *
   * HDB_TYPE  Connection type (MYSQL or CASSANDRA, default is CASSANDRA)
   * HDB_NAME  Database name (default is "hdb")
   * HDB_USER
   * HDB_PASSWORD
   * HDB_PORT
   *
   * MySQL specific
   * HDB_HOST
   *
   * Cassandra specific
   * HDB_CONTACTS
   *
   * @throws HdbFailed
   */
  public void connect() throws HdbFailed {

    String hdb = System.getenv("HDB_TYPE");
    if(hdb==null) {
      hdbType = HDB_CASSANDRA;
    } else if(hdb.equalsIgnoreCase("MYSQL")) {
      hdbType = HDB_MYSQL;
    } else if(hdb.equalsIgnoreCase("CASSANDRA")) {
      hdbType = HDB_CASSANDRA;
    } else {
      throw new HdbFailed("Wrong HDB_TYPE , MYSQL or CASSANDRA expected");
    }

  }

  public static void test(HdbExtractor hdb,String start,String stop,String attName) throws HdbFailed {

    ArrayList<HdbData> data = hdb.getDB().getData(attName,start,stop,false);
    for(int i=0;i<data.size();i++)
      System.out.println("Rec #"+i+" :"+data.get(i));

  }


  public static void main(String[] args) {

    HdbExtractor hdb = new HdbExtractor();

    try {

      hdb.connectMySQL("cassandra1","","","",(short)0);

      //String[] attList = hdb.getDB().getSourcesList();
      //System.out.println("Got "+attList.length+" attributes");

      // Double RO
      test(hdb,"09/07/2015 12:00:00","09/07/2015 13:00:00",
          "tango://orion.esrf.fr:10000/sr/d-ct/1/current");

      // Double RW
      test(hdb,"09/07/2015 12:00:00","09/07/2015 13:00:00",
          "tango://orion.esrf.fr:10000/elin/gun/hv/highvoltage");

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

      // Byte RO
      test(hdb,"09/07/2015 12:00:00","10/07/2015 12:00:00",
          "tango://orion.esrf.fr:10000/sr/d-fofbcorrection/globalx/state");

      // String RO
      test(hdb,"09/07/2015 12:00:00","09/07/2015 19:00:00",
          "tango://orion.esrf.fr:10000/sr/rf-circ/tra1/status");

      // String RW
      test(hdb,"09/07/2015 12:00:00","09/07/2015 19:00:00",
          "tango://orion.esrf.fr:10000/sys/talker/2/text_to_talk");


    } catch (HdbFailed e) {
      System.out.println(e.getMessage());
    }

  }

}
