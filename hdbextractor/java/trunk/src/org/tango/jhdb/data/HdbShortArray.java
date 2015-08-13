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
 * HDB short array data (16 bits integer)
 */
public class HdbShortArray extends HdbData {

  short[] value = null;
  short[] wvalue = null;

  public HdbShortArray(int type) {
    this.type = type;
  }

  public HdbShortArray(int type,short[] value) {
    this.type = type;
    this.value = value;
  }

  public short[] getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public short[] getWriteValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return wvalue;

  }

  public void parseValue(ArrayList<Object> value) throws HdbFailed {

    this.value = parseShortArray(value);

  }

  public void parseWriteValue(ArrayList<Object> value) throws HdbFailed {

    if(value!=null)
      this.wvalue = parseShortArray(value);

  }

  private short[] parseShortArray(ArrayList<Object> value) throws HdbFailed {

    short[] ret = new short[value.size()];
    if(value.size()==0)
      return ret;

    if( value.get(0) instanceof String ) {

      // Value given as string
      try {
        for(int i=0;i<value.size();i++) {
          String str = (String)value.get(i);
          if(str==null) {
            ret[i] = 0;
          } else {
            ret[i] = Short.parseShort(str);
          }
        }
      } catch(NumberFormatException e) {
        throw new HdbFailed("parseShortArray: Invalid number syntax");
      }

    } else {

      for(int i=0;i<value.size();i++) {
        Short s = (Short)value.get(i);
        ret[i] = s.shortValue();
      }

    }

    return ret;

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_ARRAY_SHORT_RO)
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+","+Integer.toString(wvalue.length)+" "+
          qualitytoStr(qualityFactor);

  }

  // Convenience function
  void copyData(HdbData src) {
    this.value = ((HdbShortArray)src).value.clone();
    this.wvalue = ((HdbShortArray)src).wvalue.clone();
  }

  public String getValueAsString() {
    if(hasFailed())
      return errorMessage;
    return arrayValue(value);
  }

  public String getWriteValueAsString() {
    if(hasFailed())
      return errorMessage;
    if(hasWriteValue())
      return arrayValue(wvalue);
    else
      return "";
  }

  private String arrayValue(short[] b) {
    StringBuffer ret = new StringBuffer();
    ret.append("Short["+b.length+"]\n");
    for(int i=0;i<b.length;i++) {
      ret.append(Short.toString(b[i]));
      if(i<b.length-1)
        ret.append("\n");
    }
    return ret.toString();
  }

  public double getValueAsDouble() throws HdbFailed {
    throw new HdbFailed("This datum is not scalar");
  }

  public double getWriteValueAsDouble() throws HdbFailed {
    throw new HdbFailed("This datum is not scalar");
  }

  public double[] getValueAsDoubleArray() throws HdbFailed {
    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    double[] ret = new double[value.length];
    for(int i=0;i<value.length;i++)
      ret[i] = (double)value[i];
    return ret;
  }

  public double[] getWriteValueAsDoubleArray() throws HdbFailed {
    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    if(!hasWriteValue())
      throw new HdbFailed("This datum has no write value");
    double[] ret = new double[wvalue.length];
    for(int i=0;i<wvalue.length;i++)
      ret[i] = (double)wvalue[i];
    return ret;
  }

}
