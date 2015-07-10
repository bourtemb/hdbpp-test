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

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * HDB double data
 */
public class HdbDouble extends HdbData {

  double value = Double.NaN;
  double wvalue = Double.NaN;

  public HdbDouble(int type) {
    this.type = type;
  }

  public double getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public double getWriteValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return wvalue;

  }

  public void parseValue(ArrayList<String> value) throws HdbFailed {

    try {
      String str = value.get(0);
      if(str==null)
        this.value = Double.NaN;
      else
        this.value = Double.parseDouble(str);
    } catch(NumberFormatException e) {
      throw new HdbFailed("Invalid number syntax for value");
    }

  }

  public void parseWriteValue(ArrayList<String> value) throws HdbFailed {

    if(value!=null) {
      try {
        String str = value.get(0);
        if(str==null)
          this.wvalue = Double.NaN;
        else
          this.wvalue = Double.parseDouble(str);
      } catch(NumberFormatException e) {
        throw new HdbFailed("Invalid number syntax for write value");
      }
    }

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_SCALAR_DOUBLE_RO)
      return timeToStr(dataTime)+": "+Double.toString(value)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": "+Double.toString(value)+";"+Double.toString(wvalue)+" "+
             qualitytoStr(qualityFactor);

  }

}
