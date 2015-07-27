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
 * HDB int data (32 bits unsigned integer)
 */
public class HdbULong extends HdbData {

  long value = 0;
  long wvalue = 0;

  public HdbULong(int type) {
    this.type = type;
  }

  public long getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public long getWriteValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return wvalue;

  }

  public void parseValue(ArrayList<Object> value) throws HdbFailed {

    this.value = parseULong(value.get(0));

  }

  public void parseWriteValue(ArrayList<Object> value) throws HdbFailed {

    if(value!=null)
      this.wvalue = parseULong(value.get(0));

  }

  private long parseULong(Object value) throws HdbFailed {

    long ret;

    if (value instanceof String) {

      // Value given as string
      try {
        String str = (String) value;
        if (str == null)
          ret = 0;
        else
          ret = Long.parseLong(str);
      } catch (NumberFormatException e) {
        throw new HdbFailed("parseULong: Invalid number syntax for value");
      }

    } else {

      Long l = (Long) value;
      ret = l.longValue();

    }

    return ret;

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_SCALAR_ULONG_RO)
      return timeToStr(dataTime)+": "+Long.toString(value)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": "+Long.toString(value)+";"+Long.toString(wvalue)+" "+
          qualitytoStr(qualityFactor);

  }

  // Convenience function
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