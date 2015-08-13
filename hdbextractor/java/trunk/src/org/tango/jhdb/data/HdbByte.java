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
 * HDB byte data (8bit integer)
 */
public class HdbByte extends HdbData {

  byte value = 0;
  byte wvalue = 0;

  public HdbByte(int type) {
    this.type = type;
  }

  public HdbByte(int type,byte value) {
    this.type = type;
    this.value = value;
  }

  public byte getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public byte getWriteValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return wvalue;

  }

  public void parseValue(ArrayList<Object> value) throws HdbFailed {

    this.value = parseByte(value.get(0));

  }

  public void parseWriteValue(ArrayList<Object> value) throws HdbFailed {

    if(value!=null)
      this.wvalue = parseByte(value.get(0));

  }

  private Byte parseByte(Object value) throws HdbFailed {

    byte ret;

    if( value instanceof String ) {

      // Value given as string
      try {
        String str = (String)value;
        if(str==null)
          ret = 0;
        else
          ret = Byte.parseByte(str);
      } catch(NumberFormatException e) {
        throw new HdbFailed("parseByte: Invalid number syntax for value");
      }

    } else {

      Byte b = (Byte)value;
      ret = b.byteValue();

    }

    return ret;

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_SCALAR_CHAR_RO)
      return timeToStr(dataTime)+": "+Byte.toString(value)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": "+Byte.toString(value)+";"+Byte.toString(wvalue)+" "+
          qualitytoStr(qualityFactor);

  }

  // Convenience function
  void copyData(HdbData src) {
    this.value = ((HdbByte)src).value;
    this.wvalue = ((HdbByte)src).wvalue;
  }

  public String getValueAsString() {
    if(hasFailed())
      return errorMessage;
    return Byte.toString(value);
  }

  public String getWriteValueAsString() {
    if(hasFailed())
      return errorMessage;
    if(hasWriteValue())
      return Byte.toString(wvalue);
    else
      return "";
  }

  public double getValueAsDouble() throws HdbFailed {
    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return (double)value;
  }

  public double getWriteValueAsDouble() throws HdbFailed {
    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    if(hasWriteValue()) {
      return (double)wvalue;
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
