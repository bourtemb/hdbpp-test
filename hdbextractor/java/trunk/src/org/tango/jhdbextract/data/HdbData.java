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

package org.tango.jhdbextract.data;

import org.tango.jhdbextract.HdbFailed;
import org.tango.jhdbextract.HdbSigInfo;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *  HdbData base class
 */
public abstract class HdbData {

  final static SimpleDateFormat dfr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  int    type;
  long   dataTime;
  long   recvTime;
  long   insertTime;
  int    qualityFactor;
  String errorMessage=null;

  /**
   * Return time of this datum
   */
  public long getDataTime() {
    return dataTime;
  }

  /**
   * Return reeive time of this datum
   */
  public long getRecvTime() {
    return recvTime;
  }

  /**
   * Return reeive time of this datum
   */
  public long getInsertTime() {
    return insertTime;
  }

  /**
   * Returns quality factor
   */
  public int getQualityFactor() {
    return qualityFactor;
  }

  /**
   * Returns true if this record has failed
   */
  public boolean hasFailed() {
    return errorMessage!=null;
  }

  /**
   * Returns error message if this record has failed
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Parse value
   * @param value Value to be parsed (separated by SEP for array)
   */
  public abstract void parseValue(ArrayList<String> value) throws HdbFailed;

  /**
   * Parse write value
   * @param value Value to be parsed (separated by SEP for array)
   */
  public abstract void parseWriteValue(ArrayList<String> value) throws HdbFailed;

  /**
   * Parse value
   * @param data_time Tango timestamp
   * @param recv_time Event recieve timestamp
   * @param insert_time Recording timestamp
   * @param error_desc Error string
   * @param quality Quality value
   * @param value_r Read value
   * @param value_w Write value
   */
  public void parse(long data_time,long recv_time,long insert_time,String error_desc,String quality,
                    ArrayList<String> value_r,ArrayList<String> value_w) throws HdbFailed {

    dataTime = data_time;
    recvTime = recv_time;
    insertTime = insert_time;
    errorMessage = error_desc;
    try {
      if(quality==null)
        qualityFactor = 0;
      else
        qualityFactor = Integer.parseInt(quality);
    } catch(NumberFormatException e) {
      throw new HdbFailed("Invalid number syntax for quality factor");
    }
    if(!hasFailed()) {
      parseValue(value_r);
      parseWriteValue(value_w);
    }

  }

  /**
   * Return time representation of the give time (ex: 22/07/2015 08:12:15.718908)
   * @param time Number of micro second since epoch
   */
  public String timeToStr(long time) {

    long ms = time/1000;
    Date d = new Date(ms);
    String dStr = dfr.format(d);
    String sStr = String.format("%06d",time%1000000);
    return dStr+"."+sStr;

  }

  public String qualitytoStr(int quality) {

    switch(quality) {
      case 0:
        return "ATTR_VALID";
      case 1:
        return "ATTR_INVALID";
      case 2:
        return "ATTR_ALARM";
      case 3:
        return "ATTR_CHANGING";
      case 4:
        return "ATTR_WARNING";
      default:
        return "UNKNOWN QUALITY";
    }

  }

  // Convenience functions

  /**
   * Create HdbData accroding to the given type
   * @param type Data type
   * @throws HdbFailed In case of failure
   */
  public static HdbData createData(int type) throws HdbFailed {

    switch(type) {
      case HdbSigInfo.TYPE_SCALAR_DOUBLE_RO:
      case HdbSigInfo.TYPE_SCALAR_DOUBLE_RW:
        return new HdbDouble(type);
      case HdbSigInfo.TYPE_ARRAY_DOUBLE_RO:
      case HdbSigInfo.TYPE_ARRAY_DOUBLE_RW:
        return new HdbDoubleArray(type);
      case HdbSigInfo.TYPE_SCALAR_INT64_RO:
      case HdbSigInfo.TYPE_SCALAR_INT64_RW:
        return new HdbLong(type);
      case HdbSigInfo.TYPE_ARRAY_INT64_RO:
      case HdbSigInfo.TYPE_ARRAY_INT64_RW:
        return new HdbLongArray(type);
      case HdbSigInfo.TYPE_SCALAR_INT8_RO:
      case HdbSigInfo.TYPE_SCALAR_INT8_RW:
        return new HdbByte(type);
      case HdbSigInfo.TYPE_ARRAY_INT8_RO:
      case HdbSigInfo.TYPE_ARRAY_INT8_RW:
        return new HdbByteArray(type);
      case HdbSigInfo.TYPE_SCALAR_STRING_RO:
      case HdbSigInfo.TYPE_SCALAR_STRING_RW:
        return new HdbString(type);
      case HdbSigInfo.TYPE_ARRAY_STRING_RO:
      case HdbSigInfo.TYPE_ARRAY_STRING_RW:
        return new HdbStringArray(type);
      default:
        throw new HdbFailed("Unknown signal type");
    }

  }

}
