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

import java.util.ArrayList;

/**
 * HDB string array data
 */
public class HdbStringArray extends HdbData {

  String[] value = null;
  String[] wvalue = null;

  public HdbStringArray(int type) {
    this.type = type;
  }

  public String[] getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public String[] getWriteValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return wvalue;

  }

  public void parseValue(ArrayList<Object> value) throws HdbFailed {

    this.value = parseArray(value);

  }

  public void parseWriteValue(ArrayList<Object> value) throws HdbFailed {

    if(value!=null)
      this.wvalue = parseArray(value);

  }

  private String[] parseArray(ArrayList<Object> value) throws HdbFailed {

    String[] ret = new String[value.size()];

    for(int i=0;i<value.size();i++) {
      String str = (String)value.get(i);
      if(str==null) {
        ret[i] = "NULL";
      } else {
        ret[i] = str;
      }
    }

    return ret;

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_ARRAY_STRING_RO)
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+","+Integer.toString(wvalue.length)+" "+
          qualitytoStr(qualityFactor);

  }

  // Convenience function
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
