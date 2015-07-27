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

import org.tango.jhdbextract.data.HdbData;
import org.tango.jhdbextract.data.HdbDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * The interface representing a database schema.
 * <p/>
 * This interface provides the main method to retrieve data from the database.
 * Any implementation of this interface is specific to a database (e.g. MySql, Cassandra)
 *
 * @author JL Pons
 */
public abstract class DbSchema {

  /**
   * Fetch data from the database.
   *
   * @param attName         The fully qualified tango attribute name (eg: tango://hostname:port/domain/family/member/attname)
   * @param start_date      Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stop_date       End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   * @param notify          Every time a block of data is complete, notifications are sent to the listener of type ProgressListener
   *
   * @throws HdbFailed In case of failure
   */
  public abstract HdbDataSet getData(String attName,
                                     String start_date,
                                     String stop_date,
                                     boolean notify) throws HdbFailed;

  /**
   * Fetch data from the database from several attributes.
   *
   * @param attNames        List of fully qualified tango attributes (eg: tango://hostname:port/domain/family/member/attname)
   * @param start_date      Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stop_date       End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   * @param notify          Every time a block of data is complete, notifications are sent to the listener of type ProgressListener
   *
   * @throws HdbFailed In case of failure
   */
  public HdbDataSet[] getData(String[] attNames,
                              String start_date,
                              String stop_date,
                              boolean notify) throws HdbFailed {

    HdbDataSet[] ret = new HdbDataSet[attNames.length];
    for(int i=0;i<ret.length;i++)
      ret[i] = getData(attNames[i], start_date, stop_date, notify);
    return ret;

  }

  /**
   * Retrieves the list of archived attributes (fully qualified name eg: tango://hostname:port/domain/family/member/attname).
   * @throws HdbFailed In case of failure
   */
  public abstract String[] getAttributeList() throws HdbFailed;

  /**
   * Returns signal info
   * @param attName The fully qualified tango attribute name (eg: tango://hostname:port/domain/family/member/attname)
   * @return The signal identifier
   * @throws HdbFailed In case of failure
   */
  public abstract HdbSigInfo getSigInfo(String attName) throws HdbFailed;

  /**
   * Return history of configurations of the specified attribute
   *
   * @param attName The fully qualified tango attribute name (eg: tango://hostname:port/domain/family/member/attname)
   * @param start_date Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stop_date End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   * @return
   */
  public abstract ArrayList<HdbSigParam> getParams(String attName,
                                                   String start_date,
                                                   String stop_date) throws HdbFailed;

  /**
   * This method finds the errors occurred inside a time interval for the specified attribute
   *
   * @param attName    The fully qualified tango attribute name (eg: tango://hostname:port/domain/family/member/attname)
   * @param start_date Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stop_date  End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   * @throws HdbFailed In case of failure
   */
  public abstract HdbDataSet findErrors(String attName,
                                        String start_date,
                                        String stop_date) throws HdbFailed;

}

