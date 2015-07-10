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

package org.tango.jhdbextract;

/**
 * Signal info structure
 */
public class HdbSigInfo {

  public final static int TYPE_SCALAR_DOUBLE_RO = 1;
  public final static int TYPE_SCALAR_DOUBLE_RW = 2;
  public final static int TYPE_ARRAY_DOUBLE_RO = 3;
  public final static int TYPE_ARRAY_DOUBLE_RW = 4;

  public final static int TYPE_SCALAR_INT64_RO = 5;
  public final static int TYPE_SCALAR_INT64_RW = 6;
  public final static int TYPE_ARRAY_INT64_RO = 7;
  public final static int TYPE_ARRAY_INT64_RW = 8;

  public final static int TYPE_SCALAR_INT8_RO = 9;
  public final static int TYPE_SCALAR_INT8_RW = 10;
  public final static int TYPE_ARRAY_INT8_RO = 11;
  public final static int TYPE_ARRAY_INT8_RW = 12;

  public final static int TYPE_SCALAR_STRING_RO = 13;
  public final static int TYPE_SCALAR_STRING_RW = 14;
  public final static int TYPE_ARRAY_STRING_RO = 15;
  public final static int TYPE_ARRAY_STRING_RW = 16;

  /**
   * Returns true if type is a Read/Write type
   * @param type Attribute type
   */
  public static boolean isRWType(int type) {

    switch(type) {
      case TYPE_SCALAR_DOUBLE_RW:
      case TYPE_ARRAY_DOUBLE_RW:
      case TYPE_SCALAR_INT64_RW:
      case TYPE_ARRAY_INT64_RW:
      case TYPE_SCALAR_INT8_RW:
      case TYPE_ARRAY_INT8_RW:
      case TYPE_SCALAR_STRING_RW:
      case TYPE_ARRAY_STRING_RW:
        return true;
      default:
        return false;
    }

  }

  /**
   * Returns true if type is an array type
   * @param type Attribute type
   */
  public static boolean isArrayType(int type) {

    switch(type) {
      case TYPE_ARRAY_DOUBLE_RO:
      case TYPE_ARRAY_INT64_RO:
      case TYPE_ARRAY_INT8_RO:
      case TYPE_ARRAY_STRING_RO:
      case TYPE_ARRAY_DOUBLE_RW:
      case TYPE_ARRAY_INT64_RW:
      case TYPE_ARRAY_INT8_RW:
      case TYPE_ARRAY_STRING_RW:
        return true;
      default:
        return false;
    }

  }

  public String sigId;
  public int type;

  public String toString() {
    return "Id=" + sigId + ",Type=" + Integer.toString(type);
  }

}
