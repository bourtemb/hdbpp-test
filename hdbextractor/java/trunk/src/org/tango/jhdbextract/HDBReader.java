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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class provides the main methods to retrieve data from HDB.
 * Any implementation of abstract method of this class is specific to a database (e.g. MySql, Cassandra)
 *
 * @author JL Pons
 */
public abstract class HDBReader {

  /**
   * Date format used in getDataFromDB calls
   */
  public final static SimpleDateFormat hdbDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  private long extraPointLookupPeriod = 3600;
  private boolean extraPointEnabled = false;

  /**
   * Fetch data from the database.
   *
   * @param attName        The fully qualified tango attribute name (eg: tango://hostname:port/domain/family/member/attname)
   * @param startDate      Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stopDate       End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   *
   * @throws HdbFailed In case of failure
   */
  public HdbDataSet getData(String attName,
                            String startDate,
                            String stopDate) throws HdbFailed {

    if(attName==null)
      throw new HdbFailed("attName input parameters is null");

    HdbSigInfo sigInfo = getSigInfo(attName);
    return getData(sigInfo, startDate, stopDate);

  }

  /**
   * Fetch data from the database.
   *
   * @param sigInfo        Attribute info structure
   * @param startDate      Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stopDate       End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   *
   * @throws HdbFailed In case of failure
   */
  public HdbDataSet getData(HdbSigInfo sigInfo,
                            String startDate,
                            String stopDate) throws HdbFailed {

    HdbDataSet result = getDataFromDB(sigInfo,startDate,stopDate);

    if(result.size()==0 && extraPointEnabled) {

      // Try to find an extra point
      Date d;
      try {
        d = HDBReader.hdbDateFormat.parse(startDate);
      } catch( ParseException e ) {
        throw new HdbFailed("Wrong startDate format : " + e.getMessage());
      }
      d.setTime(d.getTime()-extraPointLookupPeriod*1000);

      String newStartDate = HDBReader.hdbDateFormat.format(d);
      stopDate = startDate;

      result = getDataFromDB(sigInfo,newStartDate,stopDate);

      if(result.size()>0) {
        // Return the last point
        ArrayList<HdbData> lastPoint = new  ArrayList<HdbData>();
        lastPoint.add(result.getLast());
        return new HdbDataSet(lastPoint);
      }

    }

    return result;

  }


  /**
   * Fetch data from the database (low level function, expert usage).
   *
   * @param sigInfo        Attribute info structure
   * @param startDate      Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stopDate       End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   *
   * @throws HdbFailed In case of failure
   */
  public abstract HdbDataSet getDataFromDB(HdbSigInfo sigInfo,
                                           String startDate,
                                           String stopDate) throws HdbFailed;

  /**
   * Fetch data from the database from several attributes.
   *
   * @param attNames       List of fully qualified tango attributes (eg: tango://hostname:port/domain/family/member/attname)
   * @param startDate      Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stopDate       End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   *
   * @throws HdbFailed In case of failure
   */
  public HdbDataSet[] getData(String[] attNames,
                              String startDate,
                              String stopDate) throws HdbFailed {

    if(attNames==null)
      throw new HdbFailed("getData(): attNames input parameters is null");

    HdbDataSet[] ret = new HdbDataSet[attNames.length];
    for(int i=0;i<ret.length;i++)
      ret[i] = getData(attNames[i], startDate, stopDate);
    return ret;

  }

  /**
   * Fetch data from the database from several attributes.
   *
   * @param sigInfos       List of attribute info structure
   * @param startDate      Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stopDate       End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   *
   * @throws HdbFailed In case of failure
   */
  public HdbDataSet[] getData(HdbSigInfo[] sigInfos,
                              String startDate,
                              String stopDate) throws HdbFailed {

    if(sigInfos==null)
      throw new HdbFailed("getData(): sigInfos input parameters is null");

    HdbDataSet[] ret = new HdbDataSet[sigInfos.length];
    for(int i=0;i<ret.length;i++)
      ret[i] = getData(sigInfos[i], startDate, stopDate);
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
   * @param startDate Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stopDate End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   * @return
   */
  public abstract ArrayList<HdbSigParam> getParams(String attName,
                                                   String startDate,
                                                   String stopDate) throws HdbFailed;

  /**
   * This method finds the errors occurred inside a time interval for the specified attribute
   *
   * @param attName   The fully qualified tango attribute name (eg: tango://hostname:port/domain/family/member/attname)
   * @param startDate Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stopDate  End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   * @throws HdbFailed In case of failure
   */
  public abstract HdbDataSet findErrors(String attName,
                                        String startDate,
                                        String stopDate) throws HdbFailed;

  /**
   * Sets the extra point lookup period.
   * @param time Lookup period in seconds
   */
  public void setExtraPointLookupPeriod(long time) {
    extraPointLookupPeriod = time;
  }

  /**
   * Returns the current extra point lookup period.
   */
  public long getExtraPointLookupPeriod() {
    return extraPointLookupPeriod;
  }

  /**
   * Enable extra point lookup
   */
  public void enableExtraPoint() {
    extraPointEnabled=true;
  }

  /**
   * Disable extra point lookup
   */
  public void disableExtraPoint() {
    extraPointEnabled=true;
  }

  /**
   * Check input dates
   * @param startDate Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stopDate  End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   */
  public void checkDates(String startDate,String stopDate) throws HdbFailed {

    if(startDate==null)
      throw new HdbFailed("startDate input parameters is null");
    if(stopDate==null)
      throw new HdbFailed("stopDate input parameters is null");

    Date d0,d1;

    try {
      d0 = HDBReader.hdbDateFormat.parse(startDate);
    } catch( ParseException e ) {
      throw new HdbFailed("Wrong start date format : " + e.getMessage());
    }

    try {
      d1 = HDBReader.hdbDateFormat.parse(stopDate);
    } catch( ParseException e ) {
      throw new HdbFailed("Wrong stop date format : " + e.getMessage());
    }

    if(d1.compareTo(d0)<=0) {
      throw new HdbFailed("startDate must be before stopDate");
    }

  }

}

