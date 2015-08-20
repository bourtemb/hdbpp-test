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
 * HDB String data
 */
public class HdbString extends HdbData {

  String value = null;
  String wvalue = null;

  public HdbString(int type) {
    this.type = type;
  }

  public HdbString(int type,String value) {
    this.type = type;
    this.value = value;
  }

  public String getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public String getWriteValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return wvalue;

  }

  public void parseValue(ArrayList<Object> value) throws HdbFailed {

    String str = (String)value.get(0);
    if(str==null)
      this.value = "NULL";
    else
      this.value = str;

  }

  public void parseWriteValue(ArrayList<Object> value) throws HdbFailed {

    if(value!=null) {
      String str = (String)value.get(0);
      if(str==null)
        this.wvalue = "NULL";
      else
        this.wvalue = str;
    }

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_SCALAR_STRING_RO)
      return timeToStr(dataTime)+": "+value+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": "+value+";"+wvalue+" "+
          qualitytoStr(qualityFactor);

  }

  // Convenience function
  int dataSize() {
    return 1;
  }
  int dataSizeW() {
    if(HdbSigInfo.isRWType(type))
      return 1;
    else
      return 0;
  }


  void copyData(HdbData src) {
    this.value = new String(((HdbString)src).value);
    this.wvalue = new String(((HdbString)src).wvalue);
  }

  public String getValueAsString() {
    if(hasFailed())
      return errorMessage;
    return value;
  }

  public String getWriteValueAsString() {
    if(hasFailed())
      return errorMessage;
    if(hasWriteValue())
      return wvalue;
    else
      return "";
  }

  public double getValueAsDouble() throws HdbFailed {
    throw new HdbFailed("This datum cannot be converted to double");
  }

  public double getWriteValueAsDouble() throws HdbFailed {
    throw new HdbFailed("This datum cannot be converted to double");
  }

  public double[] getValueAsDoubleArray() throws HdbFailed {
    throw new HdbFailed("This datum cannot be converted to double");
  }

  public double[] getWriteValueAsDoubleArray() throws HdbFailed {
    throw new HdbFailed("This datum cannot be converted to double");
  }

}
