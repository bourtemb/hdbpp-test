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

import org.tango.jhdb.data.HdbData;
import org.tango.jhdb.data.HdbDataSet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class provides the main methods to retrieve data from HDB.
 * Any implementation of abstract method of this class is specific to a database (e.g. MySql, Cassandra)
 *
 * @author JL Pons
 */
public abstract class HdbReader {

  /** Normal extraction mode */
  public final static int MODE_NORMAL = 0;
  /** Extract data and ignore errors (all HdbData which has failed are removed) */
  public final static int MODE_IGNORE_ERROR = 1;
  /** Filling gaps by correlating to the last known value of the HdbDataSet */
  public final static int MODE_FILLED = 2;
  /** Correlate all HdbDataSet to the HdbDataSet which have the lowest number of data */
  public final static int MODE_CORRELATED = 3;

  private long extraPointLookupPeriod = 3600;
  private boolean extraPointEnabled = false;
  private ArrayList<HdbProgressListener> prgListeners=null;

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
        d = Hdb.hdbDateFormat.parse(startDate);
      } catch( ParseException e ) {
        throw new HdbFailed("Wrong startDate format : " + e.getMessage());
      }
      d.setTime(d.getTime()-extraPointLookupPeriod*1000);

      String newStartDate = Hdb.hdbDateFormat.format(d);
      stopDate = startDate;

      result = getDataFromDB(sigInfo,newStartDate,stopDate);

      if(result.size()>0) {
        // Return the last point
        ArrayList<HdbData> lastPoint = new  ArrayList<HdbData>();
        lastPoint.add(result.getLast());
        result = new HdbDataSet(lastPoint);
      }

    }

    result.setName(sigInfo.name);
    result.setType(sigInfo.type);
    return result;

  }


  abstract HdbDataSet getDataFromDB(HdbSigInfo sigInfo,
                                    String startDate,
                                    String stopDate) throws HdbFailed;
  /**
   * Fetch data from the database from several attributes.
   *
   * @param attNames       List of fully qualified tango attributes (eg: tango://hostname:port/domain/family/member/attname)
   * @param startDate      Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stopDate       End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   * @param extractMode    Extraction mode MODE_NORMAL,MODE_IGNORE_ERROR or MODE_CORRELATED
   *
   * @throws HdbFailed In case of failure
   */
  public HdbDataSet[] getData(String[] attNames,
                              String startDate,
                              String stopDate,
                              int extractMode) throws HdbFailed {

    if(attNames==null)
      throw new HdbFailed("getData(): attNames input parameters is null");

    HdbSigInfo[] sigInfos = new HdbSigInfo[attNames.length];
    for(int i=0;i<sigInfos.length;i++)
      sigInfos[i] = getSigInfo(attNames[i]);

    return getData(sigInfos,startDate,stopDate,extractMode);

  }

  /**
   * Fetch data from the database from several attributes.
   *
   * @param sigInfos       List of attribute info structure
   * @param startDate      Beginning of the requested time interval (as string eg: "10/07/2014 10:00:00")
   * @param stopDate       End of the requested time interval (as string eg: "10/07/2014 12:00:00")
   * @param extractMode    Extraction mode MODE_NORMAL,MODE_IGNORE_ERROR or MODE_CORRELATED
   *
   * @throws HdbFailed In case of failure
   */
  public HdbDataSet[] getData(HdbSigInfo[] sigInfos,
                              String startDate,
                              String stopDate,
                              int extractMode) throws HdbFailed {

    if(sigInfos==null)
      throw new HdbFailed("getData(): sigInfos input parameters is null");

    // Fetch data
    HdbDataSet[] ret = new HdbDataSet[sigInfos.length];
    for(int i=0;i<ret.length;i++)
      ret[i] = getData(sigInfos[i], startDate, stopDate);

    // Remove hasFailed
    if(extractMode==MODE_IGNORE_ERROR ||
       extractMode==MODE_CORRELATED ||
       extractMode==MODE_FILLED) {
      for(int i=0;i<ret.length;i++)
        ret[i].removeHasFailed();
    }

    // Correlated mode
    if(extractMode==MODE_CORRELATED)
      correlate(ret);

    if(extractMode==MODE_FILLED)
      fill(ret);

    return ret;

  }

  /**
   * Retrieves the list of all archived attributes (fully qualified name eg: tango://hostname:port/domain/family/member/attname).
   * @throws HdbFailed In case of failure
   */
  public abstract String[] getAttributeList() throws HdbFailed;

  /**
   * Returns the list hostname.
   */
  public abstract String[] getHosts() throws HdbFailed;

  /**
   * Returns the domain list for the specified host.
   */
  public abstract String[] getDomains(String host) throws HdbFailed;

  /**
   * Returns the family list for the specified host/domain.
   */
  public abstract String[] getFamilies(String host,String domain) throws HdbFailed;

  /**
   * Returns the member list for the specified host/domain/family.
   */
  public abstract String[] getMembers(String host,String domain,String family) throws HdbFailed;

  /**
   * Returns the name list for the specified host/domain/family/member.
   */
  public abstract String[] getNames(String host,String domain,String family,String member) throws HdbFailed;

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
   * Return true whether extra point lookup is enabled
   */
  public boolean isExtraPointEnabled() {
    return extraPointEnabled;
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
      d0 = Hdb.hdbDateFormat.parse(startDate);
    } catch( ParseException e ) {
      throw new HdbFailed("Wrong start date format : " + e.getMessage());
    }

    try {
      d1 = Hdb.hdbDateFormat.parse(stopDate);
    } catch( ParseException e ) {
      throw new HdbFailed("Wrong stop date format : " + e.getMessage());
    }

    if(d1.compareTo(d0)<=0) {
      throw new HdbFailed("startDate must be before stopDate");
    }

  }

  /**
   * Returns information on this connection.
   */
  public abstract String getInfo() throws HdbFailed;


  /**
   * Return true if this reader has a progress listener
   * @return
   */
  public boolean hasProgressListener() {
    return (prgListeners!=null) && (prgListeners.size()>0);
  }

  /**
   * Add a progress listener on this HdbReader
   * @param l HdbProgressListener to be added
   */
  public void addProgressListener(HdbProgressListener l) {
    if(prgListeners==null)
      prgListeners = new ArrayList<HdbProgressListener>();
    if(!prgListeners.contains(l))
      prgListeners.add(l);
  }

  /**
   * Remove a progress listener from this HdbReader
   * @param l HdbProgressListener to be removed
   */
  public void removeProgressListener(HdbProgressListener l) {
    if(prgListeners!=null)
      prgListeners.remove(l);
  }

  void fireProgressListener(double p) {
    if(prgListeners==null)
      return;
    for(HdbProgressListener l:prgListeners)
      l.progress(this,p);
  }

  private boolean isBefore(HdbDataSet[] ret,int minIdx,long t0) {

    boolean ok=true;

    for(int i=0;i<ret.length && ok;i++) {
      if(i!=minIdx)
        ok = ok && (t0 < ret[i].get(0).getDataTime());
    }

    return ok;

  }

  private void correlate(HdbDataSet[] ret) throws HdbFailed {

    // Select the base HdbDataSet
    int min = Integer.MAX_VALUE;
    int minIdx = 0;
    for(int i=0;i<ret.length;i++) {
      if(ret[i].size()<min) {
        minIdx = i;
        min = ret[i].size();
      }
    }

    // We have to prevent that HdbDataSet.getBefore() will never return null or
    // an 'non significant' value
    boolean ok = false;
    while(!ok && ret[minIdx].size()>0) {
      long t0 = ret[minIdx].get(0).getDataTime();
      if(isBefore(ret,minIdx,t0))
        ret[minIdx].removeFirst();
      else
        ok = true;
    }

    int newLength = ret[minIdx].size();

    // Now truncate all other HdbDataSet
    for(int i=0;i<ret.length;i++) {
      if(i!=minIdx) {
        ArrayList<HdbData> newSet = new ArrayList<HdbData>();
        for(int j=0;j<newLength;j++) {
          long t = ret[minIdx].get(j).getDataTime();
          HdbData b = ret[i].getBefore(t).copy();
          newSet.add(b);
        }

        HdbDataSet newDataSet = new HdbDataSet(newSet);
        newDataSet.setType(ret[i].getType());
        newDataSet.setName(ret[i].getName());
        ret[i] = newDataSet;

      }
    }

    // Update all timetamps
    for(int i=0;i<ret.length;i++) {
      if(i!=minIdx) {
        for(int j=0;j<newLength;j++) {
          long t = ret[minIdx].get(j).getDataTime();
          ret[i].get(j).setDataTime(t);
        }
      }
    }

  }

  private void insertTime(ArrayList<Long> list,long value) {

    int low = 0;
    int high = list.size()-1;

    while (low <= high) {
      int mid = (low + high) >>> 1;
      long midVal = list.get(mid);

      if (midVal < value)
        low = mid + 1;
      else if (midVal > value)
        high = mid - 1;
      else
        return; // item found (do nothing)
    }

    // r is the insertion position
    int r = -(low + 1);
    if(r<0) r=  -(r+1);

    list.add(r,value);

  }

  private void fill(HdbDataSet[] ret) throws HdbFailed {

    // Ensure that no DataSet is empty
    for(int i=0;i<ret.length;i++) {
      if(ret[i].isEmpty())
        throw new HdbFailed("FILLED mode cannot be done on empty HdbDataSet");
    }

    // Start by creating an array of all timestamps
    ArrayList<Long> allTime = new ArrayList<Long>();
    for(int i=0;i<ret.length;i++) {
      for(int j=0;j<ret[i].size();j++) {
        HdbData d = ret[i].get(j);
        insertTime(allTime,d.getDataTime());
      }
    }

    // Now extend all HdbDataSet
    for(int i=0;i<ret.length;i++) {
      ArrayList<HdbData> newSet = new ArrayList<HdbData>();
      for(int j=0;j<allTime.size();j++) {
        HdbData b = ret[i].getBefore(allTime.get(j)).copy();
        newSet.add(b);
      }
      HdbDataSet newDataSet = new HdbDataSet(newSet);
      newDataSet.setType(ret[i].getType());
      newDataSet.setName(ret[i].getName());
      ret[i] = newDataSet;
    }

    // Update all timetamps
    for(int i=0;i<ret.length;i++) {
      for(int j=0;j<allTime.size();j++) {
        ret[i].get(j).setDataTime(allTime.get(j));
      }
    }

  }

}

