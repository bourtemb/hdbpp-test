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
 * HDB boolean data (8bit integer)
 */
public class HdbBoolean extends HdbData {

  boolean value = false;
  boolean wvalue = false;

  public HdbBoolean(int type) {
    this.type = type;
  }

  public HdbBoolean(int type,boolean value) {
    this.type = type;
    this.value = value;
  }

  public boolean getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public boolean getWriteValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return wvalue;

  }

  public void parseValue(ArrayList<Object> value) throws HdbFailed {

    this.value = parseBoolean(value.get(0));

  }

  public void parseWriteValue(ArrayList<Object> value) throws HdbFailed {

    if(value!=null)
      this.wvalue = parseBoolean(value.get(0));

  }

  private boolean parseBoolean(Object value) throws HdbFailed {

    boolean ret;

    if( value instanceof String ) {

      // Value given as string
      try {
        String str = (String)value;
        if(str==null)
          ret = false;
        else
          ret = Boolean.parseBoolean(str);
      } catch(NumberFormatException e) {
        throw new HdbFailed("parseBoolean: Invalid number syntax for value");
      }

    } else {

      Boolean b = (Boolean)value;
      ret = b.booleanValue();

    }

    return ret;

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_SCALAR_BOOLEAN_RO)
      return timeToStr(dataTime)+": "+Boolean.toString(value)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": "+Boolean.toString(value)+";"+Boolean.toString(wvalue)+" "+
          qualitytoStr(qualityFactor);

  }

  // Convenience function
  void copyData(HdbData src) {
    this.value = ((HdbBoolean)src).value;
    this.wvalue = ((HdbBoolean)src).wvalue;
  }

  public String getValueAsString() {
    if(hasFailed())
      return errorMessage;
    return Boolean.toString(value);
  }

  public String getWriteValueAsString() {
    if(hasFailed())
      return errorMessage;
    if(hasWriteValue())
      return Boolean.toString(wvalue);
    else
      return "";
  }

  public double getValueAsDouble() throws HdbFailed {
    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return (value)?1:0;
  }

  public double getWriteValueAsDouble() throws HdbFailed {
    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    if(hasWriteValue()) {
      return (wvalue)?1:0;
    } else {
      throw new HdbFailed("This datum has no write value");
    }
  }

  public double[] getValueAsDoubleArray() throws HdbFailed {
    throw new HdbFailed("This datum is not an array");
  }

  public double[] getWriteValueAsDoubleArray() throws HdbFailed {
    throw new HdbFailed("This datum is not an array");
  }

}
