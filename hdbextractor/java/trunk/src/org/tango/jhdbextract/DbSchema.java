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
public interface DbSchema {

  /**
   * Fetch data from the database.
   *
   * @param attName         the fully qualified tango attribute
   * @param start_date      the start date (begin of the requested data interval) as string, such as "2014-07-10 10:00:00"
   * @param stop_date       the stop date (end of the requested data interval) as string, such as "2014-07-10 12:00:00"
   * @param notifyEveryRows the number of rows that make up a block of data. Every time a block of data is complete
   *                        notifications are sent to the listener of type ResultListener (HdbExtractor)
   * @throws HdbFailed In case of failure
   */
  public ArrayList<HdbData> getData(String attName,
                                    String start_date,
                                    String stop_date,
                                    boolean notifyEveryRows) throws HdbFailed;

  public ArrayList<HdbData>[] getData(String[] attNames,
                                      String start_date,
                                      String stop_date,
                                      boolean notifyEveryRows) throws HdbFailed;

  /**
   * Retrieves the list of archived sources, returning true if the query is successful.
   * @throws HdbFailed In case of failure
   */
  public String[] getSourcesList() throws HdbFailed;

  /**
   * Returns signal info
   * @param attName the fully qualified tango attribute name
   * @return The signal identifier
   * @throws HdbFailed In case of failure
   */
  public HdbSigInfo getSigInfo(String attName) throws HdbFailed;

  /**
   * This method finds the errors occurred inside a time_interval window for the specified source
   *
   * @param source     The name of the source to look for in the database
   * @param start_date the start date (begin of the requested data interval) as string, such as "2014-07-10 10:00:00"
   * @param stop_date  the stop date (end of the requested data interval) as string, such as "2014-07-10 12:00:00"
   *                   <p/>
   *                   The results can be obtained with the method get. The ResultListenerInterface::onProgressUpdate
   *                   and ResultListenerInterface::onFinished can be used in order to receive notifications.
   * @throws HdbFailed In case of failure
   */
  public ArrayList<HdbData> findErrors(String source,
                                       String start_date,
                                       String stop_date) throws HdbFailed;

}

