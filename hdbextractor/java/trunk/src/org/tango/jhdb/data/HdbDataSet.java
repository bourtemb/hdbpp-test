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

import java.util.ArrayList;

/**
 * Set of HDB Data
 */
public class HdbDataSet {

  ArrayList<HdbData> data;
  String name;
  int type;
  long invalidValue=Long.MIN_VALUE;

  /**
   * Construct an empty HdbDataSet
   */
  public HdbDataSet() {
    name = "";
    data = new ArrayList<HdbData>();
  }

  /**
   * Construct a HdbDataSet with the given HdbData
   */
  public HdbDataSet(ArrayList<HdbData> data) {
    name = "";
    this.data = data;
  }

  /**
   * Set the type of this dataset
   * @param type Type
   */
  public void setType(int type) {
    this.type = type;
  }

  /**
   * Get the name of this dataset
   * @return
   */
  public int getType() {
    return type;
  }
  /**
   * Set the name of this dataset
   * @param name Name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the name of this dataset
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Return size of this HdbDataSet
   * @return
   */
  public int size() {
    return data.size();
  }

  /**
   * Apply a conversion factor to the data set.
   * If the type cannot be converted, nothing happens.
   * @param f Conversion factor
   */
  public void applyConversionFactor(double f) {
    for(int i=0;i<data.size();i++)
      data.get(i).applyConversionFactor(f);
  }

  /**
   * Return HdbData at the specified index
   * @param idx
   * @return
   */
  public HdbData get(int idx) {
    return data.get(idx);
  }

  /**
   * Get last data of this data set
   */
  public HdbData getLast() {
    int s = size();
    if(s>0) {
      return data.get(s-1);
    } else {
      return null;
    }
  }

  /**
   * Returns true whether this dataset is empty
   */
  public boolean isEmpty() {
    return data.size()==0;
  }

  /**
   * Remove all HdbData which have failed
   */
  public void removeHasFailed() {

    int i = 0;
    while(i<size()) {
      HdbData d = get(i);
      if(d.hasFailed())
        data.remove(i);
      else
        i++;
    }

  }

  /**
   * Remove first item
   */
  public void removeFirst() {
    data.remove(0);
  }

  /**
   * Return the HdbData just before the given time
   * if no data exists before, return the first item, null if the list is empty
   * @param time time stamps in us since epoch
   */
  public HdbData getBefore(long time) {

    int low = 0;
    int high = size()-1;

    if(size()<=0)
      return null;

    while (low <= high) {
      int mid = (low + high) >>> 1;
      long midVal = data.get(mid).getDataTime();

      if (midVal < time)
        low = mid + 1;
      else if (midVal > time)
        high = mid - 1;
      else
        return data.get(mid); // Exact match
    }

    // r is the insertion position for an asc sort
    int r = -(low + 1);
    if(r<0) r = -(r+1);

    if(r==0) {
      // Nothing before
      return get(0);
    } else {
      return get(r-1);
    }

  }


  /** Returns all timestamp of this dataset as an array */
  public long[] getDataTimeArray() {
    long[] ret = new long[size()];
    for(int i=0;i<size();i++)
      ret[i] = get(i).getDataTime();
    return ret;
  }

  /** Returns all read value of this dataset as a double array, for scalar and numerical type only.
   * If a data has failed, Double.NaN is returned.
   * @throws HdbFailed If the type cannot be converted or is not a scalar type
   */
  public double[] getValueAsDoubleArray() throws HdbFailed {

    if(HdbSigInfo.isArrayType(getType()))
      throw new HdbFailed("Not a scalar type ");
    if(!HdbSigInfo.isNumericType(getType()))
      throw new HdbFailed("Not a numerical type ");

    double[] ret = new double[size()];
    for(int i=0;i<size();i++)
      if(get(i).hasFailed())
        ret[i] = Double.NaN;
      else
        ret[i] = get(i).getValueAsDouble();
    return ret;


  }

  /** Returns all write value of this dataset as a double array, for scalar and numerical type only.
   * If a data has failed, Double.NaN is returned.
   * @throws HdbFailed If the type cannot be converted or is not a scalar type
   */
  public double[] getWriteValueAsDoubleArray() throws HdbFailed {

    if(HdbSigInfo.isArrayType(getType()))
      throw new HdbFailed("Not a scalar type ");
    if(!HdbSigInfo.isNumericType(getType()))
      throw new HdbFailed("Not a numerical type ");

    double[] ret = new double[size()];
    for(int i=0;i<size();i++)
      if(get(i).hasFailed())
        ret[i] = Double.NaN;
      else
        ret[i] = get(i).getWriteValueAsDouble();
    return ret;


  }

  /**
   * Sets the value returned if a data has failed when calling getDataAsLongIntArray.
   * Default is Long.MIN_VALUE.
   * @param invalid Invalid value
   */
  public void setInvalidValueForInteger(long invalid) {
    invalidValue = invalid;
  }

  /** Returns all read value of this dataset as a long array, for scalar and integer type only.
   * If a data has failed, INVALID_VAL is returned see #setInvalidValueForInteger.
   * @throws HdbFailed If the type cannot be converted or is not a scalar type
   */
  public long[] getValueAsLongArray() throws HdbFailed {

    if(HdbSigInfo.isArrayType(getType()))
      throw new HdbFailed("Not a scalar type ");
    if(!HdbSigInfo.isIntegerType(getType()))
      throw new HdbFailed("Not an integer type ");

    long[] ret = new long[size()];
    for(int i=0;i<size();i++)
      if(get(i).hasFailed())
        ret[i] = invalidValue;
      else
        ret[i] = get(i).getValueAsLong();
    return ret;

  }

  /** Returns all write value of this dataset as a long array, for scalar and integer type only.
   * If a data has failed, INVALID_VAL is returned see #setInvalidValueForInteger.
   * @throws HdbFailed If the type cannot be converted or is not a scalar type
   */
  public long[] getWriteValueAsLongArray() throws HdbFailed {

    if(HdbSigInfo.isArrayType(getType()))
      throw new HdbFailed("Not a scalar type ");
    if(!HdbSigInfo.isIntegerType(getType()))
      throw new HdbFailed("Not an integer type ");

    long[] ret = new long[size()];
    for(int i=0;i<size();i++)
      if(get(i).hasFailed())
        ret[i] = invalidValue;
      else
        ret[i] = get(i).getWriteValueAsLong();
    return ret;

  }

}


