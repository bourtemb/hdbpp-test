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

package org.tango.jhdb.data;

import org.tango.jhdb.HdbFailed;
import org.tango.jhdb.HdbSigInfo;

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
   * Set the dataTime
   */
  public void setDataTime(long time) {
    dataTime = time;
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
   * Return data type
   */
  public int getType() {
    return type;
  }

  /**
   * Returns true if this record has failed
   */
  public boolean hasFailed() {
    return errorMessage!=null;
  }

  /**
   * Return number of item of the read value of this HdbData (1 is returned in case of scalar data).
   */
  public int size() {

    if(hasFailed())
      return 0;
    return dataSize();

  }

  /**
   * Return number of item of the write value of this HdbData (1 is returned in case of scalar data).
   */
  public int sizeW() {

    if(hasFailed())
      return 0;
    return dataSizeW();

  }

  /**
   * Returns error message if this record has failed
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Returns true whether this datum has a write value
   */
  public boolean hasWriteValue() {
    return HdbSigInfo.isRWType(type);
  }

  /**
   * Returns true whether this datum is an array
   */
  public boolean isArray() {
    return HdbSigInfo.isArrayType(type);
  }

  /**
   * Returns the value as double if it can be converted.
   * @throws HdbFailed In case of failure
   */
  public abstract double getValueAsDouble() throws HdbFailed;

  /**
   * Returns the value as double array it can be converted.
   * @throws HdbFailed In case of failure
   */
  public abstract double[] getValueAsDoubleArray() throws HdbFailed;

  /**
   * Returns the write value as double if it can be converted.
   * @throws HdbFailed In case of failure
   */
  public abstract double getWriteValueAsDouble() throws HdbFailed;

  /**
   * Returns the write value as double array it can be converted.
   * @throws HdbFailed In case of failure
   */
  public abstract double[] getWriteValueAsDoubleArray() throws HdbFailed;

  /**
   * Parse value
   * @param value Value to be parsed
   */
  public abstract void parseValue(ArrayList<Object> value) throws HdbFailed;

  /**
   * Parse write value
   * @param value Value to be parsed
   */
  public abstract void parseWriteValue(ArrayList<Object> value) throws HdbFailed;


  /**
   * Returns the value as a String
   */
  public abstract String getValueAsString();

  /**
   * Returns the write value as a String
   */
  public abstract String getWriteValueAsString();

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
  public void parse(long data_time,long recv_time,long insert_time,String error_desc,int quality,
                    ArrayList<Object> value_r,ArrayList<Object> value_w) throws HdbFailed {

    dataTime = data_time;
    recvTime = recv_time;
    insertTime = insert_time;
    if(error_desc!=null)
      if(error_desc.isEmpty())
        error_desc = null;
    errorMessage = error_desc;
    qualityFactor = quality;

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
  abstract int dataSize();
  abstract int dataSizeW();

  abstract void copyData(HdbData src);

  public HdbData copy() throws HdbFailed {

    HdbData ret = HdbData.createData(type);
    ret.dataTime=dataTime;
    ret.recvTime=recvTime;
    ret.insertTime=insertTime;
    ret.qualityFactor=qualityFactor;
    ret.errorMessage=errorMessage;
    ret.copyData(this);
    return ret;

  }

  public abstract void applyConversionFactor(double f);

  /**
   * Create HdbData accroding to the given type
   * @param type Data type
   * @throws HdbFailed In case of failure
   */
  public static HdbData createData(int type) throws HdbFailed {

    switch(type) {

      case HdbSigInfo.TYPE_SCALAR_BOOLEAN_RO:
      case HdbSigInfo.TYPE_SCALAR_BOOLEAN_RW:
        return new HdbBoolean(type);
      case HdbSigInfo.TYPE_ARRAY_BOOLEAN_RO:
      case HdbSigInfo.TYPE_ARRAY_BOOLEAN_RW:
        return new HdbBooleanArray(type);

      case HdbSigInfo.TYPE_SCALAR_CHAR_RO:
      case HdbSigInfo.TYPE_SCALAR_CHAR_RW:
        return new HdbByte(type);
      case HdbSigInfo.TYPE_ARRAY_CHAR_RO:
      case HdbSigInfo.TYPE_ARRAY_CHAR_RW:
        return new HdbByteArray(type);

      case HdbSigInfo.TYPE_SCALAR_UCHAR_RO:
      case HdbSigInfo.TYPE_SCALAR_UCHAR_RW:
        return new HdbUChar(type);
      case HdbSigInfo.TYPE_ARRAY_UCHAR_RO:
      case HdbSigInfo.TYPE_ARRAY_UCHAR_RW:
        return new HdbUCharArray(type);

      case HdbSigInfo.TYPE_SCALAR_SHORT_RO:
      case HdbSigInfo.TYPE_SCALAR_SHORT_RW:
        return new HdbShort(type);
      case HdbSigInfo.TYPE_ARRAY_SHORT_RO:
      case HdbSigInfo.TYPE_ARRAY_SHORT_RW:
        return new HdbShortArray(type);

      case HdbSigInfo.TYPE_SCALAR_USHORT_RO:
      case HdbSigInfo.TYPE_SCALAR_USHORT_RW:
        return new HdbUShort(type);
      case HdbSigInfo.TYPE_ARRAY_USHORT_RO:
      case HdbSigInfo.TYPE_ARRAY_USHORT_RW:
        return new HdbUShortArray(type);

      case HdbSigInfo.TYPE_SCALAR_LONG_RO:
      case HdbSigInfo.TYPE_SCALAR_LONG_RW:
        return new HdbLong(type);
      case HdbSigInfo.TYPE_ARRAY_LONG_RO:
      case HdbSigInfo.TYPE_ARRAY_LONG_RW:
        return new HdbLongArray(type);

      case HdbSigInfo.TYPE_SCALAR_ULONG_RO:
      case HdbSigInfo.TYPE_SCALAR_ULONG_RW:
        return new HdbULong(type);
      case HdbSigInfo.TYPE_ARRAY_ULONG_RO:
      case HdbSigInfo.TYPE_ARRAY_ULONG_RW:
        return new HdbULongArray(type);

      case HdbSigInfo.TYPE_SCALAR_LONG64_RO:
      case HdbSigInfo.TYPE_SCALAR_LONG64_RW:
        return new HdbLong64(type);
      case HdbSigInfo.TYPE_ARRAY_LONG64_RO:
      case HdbSigInfo.TYPE_ARRAY_LONG64_RW:
        return new HdbLong64Array(type);

      case HdbSigInfo.TYPE_SCALAR_DOUBLE_RO:
      case HdbSigInfo.TYPE_SCALAR_DOUBLE_RW:
        return new HdbDouble(type);
      case HdbSigInfo.TYPE_ARRAY_DOUBLE_RO:
      case HdbSigInfo.TYPE_ARRAY_DOUBLE_RW:
        return new HdbDoubleArray(type);

      case HdbSigInfo.TYPE_SCALAR_FLOAT_RO:
      case HdbSigInfo.TYPE_SCALAR_FLOAT_RW:
        return new HdbFloat(type);
      case HdbSigInfo.TYPE_ARRAY_FLOAT_RO:
      case HdbSigInfo.TYPE_ARRAY_FLOAT_RW:
        return new HdbFloatArray(type);

      case HdbSigInfo.TYPE_SCALAR_STATE_RO:
      case HdbSigInfo.TYPE_SCALAR_STATE_RW:
        return new HdbState(type);
      case HdbSigInfo.TYPE_ARRAY_STATE_RO:
      case HdbSigInfo.TYPE_ARRAY_STATE_RW:
        return new HdbStateArray(type);

      case HdbSigInfo.TYPE_SCALAR_STRING_RO:
      case HdbSigInfo.TYPE_SCALAR_STRING_RW:
        return new HdbString(type);
      case HdbSigInfo.TYPE_ARRAY_STRING_RO:
      case HdbSigInfo.TYPE_ARRAY_STRING_RW:
        return new HdbStringArray(type);

      case HdbSigInfo.TYPE_SCALAR_ENCODED_RO:
      case HdbSigInfo.TYPE_SCALAR_ENCODED_RW:
      case HdbSigInfo.TYPE_ARRAY_ENCODED_RO:
      case HdbSigInfo.TYPE_ARRAY_ENCODED_RW:
      case HdbSigInfo.TYPE_SCALAR_ULONG64_RO:
      case HdbSigInfo.TYPE_SCALAR_ULONG64_RW:
      case HdbSigInfo.TYPE_ARRAY_ULONG64_RO:
      case HdbSigInfo.TYPE_ARRAY_ULONG64_RW:
        throw new HdbFailed("Type " + HdbSigInfo.typeStr[type] + " not supported yet !");

      default:
        throw new HdbFailed("Unknown signal type code=" + type);
    }

  }

}
