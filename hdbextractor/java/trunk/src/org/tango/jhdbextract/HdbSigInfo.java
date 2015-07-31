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

  public final static int TYPE_SCALAR_LONG64_RO = 5;
  public final static int TYPE_SCALAR_LONG64_RW = 6;
  public final static int TYPE_ARRAY_LONG64_RO = 7;
  public final static int TYPE_ARRAY_LONG64_RW = 8;

  public final static int TYPE_SCALAR_CHAR_RO = 9;
  public final static int TYPE_SCALAR_CHAR_RW = 10;
  public final static int TYPE_ARRAY_CHAR_RO = 11;
  public final static int TYPE_ARRAY_CHAR_RW = 12;

  public final static int TYPE_SCALAR_STRING_RO = 13;
  public final static int TYPE_SCALAR_STRING_RW = 14;
  public final static int TYPE_ARRAY_STRING_RO = 15;
  public final static int TYPE_ARRAY_STRING_RW = 16;

  public final static int TYPE_SCALAR_FLOAT_RO = 17;
  public final static int TYPE_SCALAR_FLOAT_RW = 18;
  public final static int TYPE_ARRAY_FLOAT_RO = 19;
  public final static int TYPE_ARRAY_FLOAT_RW = 20;

  public final static int TYPE_SCALAR_UCHAR_RO = 21;
  public final static int TYPE_SCALAR_UCHAR_RW = 22;
  public final static int TYPE_ARRAY_UCHAR_RO = 23;
  public final static int TYPE_ARRAY_UCHAR_RW = 24;

  public final static int TYPE_SCALAR_SHORT_RO = 25;
  public final static int TYPE_SCALAR_SHORT_RW = 26;
  public final static int TYPE_ARRAY_SHORT_RO = 27;
  public final static int TYPE_ARRAY_SHORT_RW = 28;

  public final static int TYPE_SCALAR_USHORT_RO = 29;
  public final static int TYPE_SCALAR_USHORT_RW = 30;
  public final static int TYPE_ARRAY_USHORT_RO = 31;
  public final static int TYPE_ARRAY_USHORT_RW = 32;

  public final static int TYPE_SCALAR_LONG_RO = 33;
  public final static int TYPE_SCALAR_LONG_RW = 34;
  public final static int TYPE_ARRAY_LONG_RO = 35;
  public final static int TYPE_ARRAY_LONG_RW = 36;

  public final static int TYPE_SCALAR_ULONG_RO = 37;
  public final static int TYPE_SCALAR_ULONG_RW = 38;
  public final static int TYPE_ARRAY_ULONG_RO = 39;
  public final static int TYPE_ARRAY_ULONG_RW = 40;

  public final static int TYPE_SCALAR_STATE_RO = 41;
  public final static int TYPE_SCALAR_STATE_RW = 42;
  public final static int TYPE_ARRAY_STATE_RO = 43;
  public final static int TYPE_ARRAY_STATE_RW = 44;

  public final static int TYPE_SCALAR_BOOLEAN_RO = 45;
  public final static int TYPE_SCALAR_BOOLEAN_RW = 46;
  public final static int TYPE_ARRAY_BOOLEAN_RO = 47;
  public final static int TYPE_ARRAY_BOOLEAN_RW = 48;

  public final static int TYPE_SCALAR_ENCODED_RO = 49;
  public final static int TYPE_SCALAR_ENCODED_RW = 50;
  public final static int TYPE_ARRAY_ENCODED_RO = 51;
  public final static int TYPE_ARRAY_ENCODED_RW = 52;

  public final static int TYPE_SCALAR_ULONG64_RO = 53;
  public final static int TYPE_SCALAR_ULONG64_RW = 54;
  public final static int TYPE_ARRAY_ULONG64_RO = 55;
  public final static int TYPE_ARRAY_ULONG64_RW = 56;

  public final static String[] typeStr = {
      "NONE",
      "TYPE_SCALAR_DOUBLE_RO",
      "TYPE_SCALAR_DOUBLE_RW",
      "TYPE_ARRAY_DOUBLE_RO",
      "TYPE_ARRAY_DOUBLE_RW",
      "TYPE_SCALAR_LONG64_RO",
      "TYPE_SCALAR_LONG64_RW",
      "TYPE_ARRAY_LONG64_RO",
      "TYPE_ARRAY_LONG64_RW",
      "TYPE_SCALAR_CHAR_RO",
      "TYPE_SCALAR_CHAR_RW",
      "TYPE_ARRAY_CHAR_RO",
      "TYPE_ARRAY_CHAR_RW",
      "TYPE_SCALAR_STRING_RO",
      "TYPE_SCALAR_STRING_RW",
      "TYPE_ARRAY_STRING_RO",
      "TYPE_ARRAY_STRING_RW",
      "TYPE_SCALAR_FLOAT_RO",
      "TYPE_SCALAR_FLOAT_RW",
      "TYPE_ARRAY_FLOAT_RO",
      "TYPE_ARRAY_FLOAT_RW",
      "TYPE_SCALAR_UCHAR_RO",
      "TYPE_SCALAR_UCHAR_RW",
      "TYPE_ARRAY_UCHAR_RO",
      "TYPE_ARRAY_UCHAR_RW",
      "TYPE_SCALAR_SHORT_RO",
      "TYPE_SCALAR_SHORT_RW",
      "TYPE_ARRAY_SHORT_RO",
      "TYPE_ARRAY_SHORT_RW",
      "TYPE_SCALAR_USHORT_RO",
      "TYPE_SCALAR_USHORT_RW",
      "TYPE_ARRAY_USHORT_RO",
      "TYPE_ARRAY_USHORT_RW",
      "TYPE_SCALAR_LONG_RO",
      "TYPE_SCALAR_LONG_RW",
      "TYPE_ARRAY_LONG_RO",
      "TYPE_ARRAY_LONG_RW",
      "TYPE_SCALAR_ULONG_RO",
      "TYPE_SCALAR_ULONG_RW",
      "TYPE_ARRAY_ULONG_RO",
      "TYPE_ARRAY_ULONG_RW",
      "TYPE_SCALAR_STATE_RO",
      "TYPE_SCALAR_STATE_RW",
      "TYPE_ARRAY_STATE_RO",
      "TYPE_ARRAY_STATE_RW",
      "TYPE_SCALAR_BOOLEAN_RO",
      "TYPE_SCALAR_BOOLEAN_RW",
      "TYPE_ARRAY_BOOLEAN_RO",
      "TYPE_ARRAY_BOOLEAN_RW",
      "TYPE_SCALAR_ENCODED_RO",
      "TYPE_SCALAR_ENCODED_RW",
      "TYPE_ARRAY_ENCODED_RO",
      "TYPE_ARRAY_ENCODED_RW",
      "TYPE_SCALAR_ULONG64_RO",
      "TYPE_SCALAR_ULONG64_RW",
      "TYPE_ARRAY_ULONG64_RO",
      "TYPE_ARRAY_ULONG64_RW"
  };

  /**
   * Returns true if type is a string type
   * @param type Attribute type
   */
  public static boolean isStringType(int type) {

    switch(type) {
      case TYPE_SCALAR_STRING_RO:
      case TYPE_SCALAR_STRING_RW:
      case TYPE_ARRAY_STRING_RO:
      case TYPE_ARRAY_STRING_RW:
        return true;
      default:
        return false;
    }

  }

  /**
  * Returns true if type is a numeric type
  * @param type Attribute type
  */
  public static boolean isNumericType(int type) {

    switch(type) {
      case TYPE_SCALAR_ENCODED_RO:
      case TYPE_SCALAR_ENCODED_RW:
      case TYPE_ARRAY_ENCODED_RO:
      case TYPE_ARRAY_ENCODED_RW:
      case TYPE_SCALAR_STRING_RO:
      case TYPE_SCALAR_STRING_RW:
      case TYPE_ARRAY_STRING_RO:
      case TYPE_ARRAY_STRING_RW:
        return false;
      default:
        return true;
    }

  }

  /**
   * Returns true if type is a Read/Write type
   * @param type Attribute type
   */
  public static boolean isRWType(int type) {

    switch(type) {
      case TYPE_SCALAR_DOUBLE_RW:
      case TYPE_ARRAY_DOUBLE_RW:
      case TYPE_SCALAR_LONG64_RW:
      case TYPE_ARRAY_LONG64_RW:
      case TYPE_SCALAR_CHAR_RW:
      case TYPE_ARRAY_CHAR_RW:
      case TYPE_SCALAR_STRING_RW:
      case TYPE_ARRAY_STRING_RW:
      case TYPE_SCALAR_FLOAT_RW:
      case TYPE_ARRAY_FLOAT_RW:
      case TYPE_SCALAR_UCHAR_RW:
      case TYPE_ARRAY_UCHAR_RW:
      case TYPE_SCALAR_SHORT_RW:
      case TYPE_ARRAY_SHORT_RW:
      case TYPE_SCALAR_USHORT_RW:
      case TYPE_ARRAY_USHORT_RW:
      case TYPE_SCALAR_LONG_RW:
      case TYPE_ARRAY_LONG_RW:
      case TYPE_SCALAR_ULONG_RW:
      case TYPE_ARRAY_ULONG_RW:
      case TYPE_SCALAR_STATE_RW:
      case TYPE_ARRAY_STATE_RW:
      case TYPE_SCALAR_BOOLEAN_RW:
      case TYPE_ARRAY_BOOLEAN_RW:
      case TYPE_SCALAR_ENCODED_RW:
      case TYPE_ARRAY_ENCODED_RW:
      case TYPE_SCALAR_ULONG64_RW:
      case TYPE_ARRAY_ULONG64_RW:
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
      case TYPE_ARRAY_LONG64_RO:
      case TYPE_ARRAY_CHAR_RO:
      case TYPE_ARRAY_STRING_RO:
      case TYPE_ARRAY_DOUBLE_RW:
      case TYPE_ARRAY_LONG64_RW:
      case TYPE_ARRAY_CHAR_RW:
      case TYPE_ARRAY_STRING_RW:
      case TYPE_ARRAY_FLOAT_RO:
      case TYPE_ARRAY_FLOAT_RW:
      case TYPE_ARRAY_UCHAR_RO:
      case TYPE_ARRAY_UCHAR_RW:
      case TYPE_ARRAY_SHORT_RO:
      case TYPE_ARRAY_SHORT_RW:
      case TYPE_ARRAY_USHORT_RO:
      case TYPE_ARRAY_USHORT_RW:
      case TYPE_ARRAY_LONG_RO:
      case TYPE_ARRAY_LONG_RW:
      case TYPE_ARRAY_ULONG_RO:
      case TYPE_ARRAY_ULONG_RW:
      case TYPE_ARRAY_STATE_RO:
      case TYPE_ARRAY_STATE_RW:
      case TYPE_ARRAY_BOOLEAN_RO:
      case TYPE_ARRAY_BOOLEAN_RW:
      case TYPE_ARRAY_ENCODED_RO:
      case TYPE_ARRAY_ENCODED_RW:
      case TYPE_ARRAY_ULONG64_RO:
      case TYPE_ARRAY_ULONG64_RW:
        return true;
      default:
        return false;
    }

  }

  /**
   * Convert a Type name from HDB att_conf to int
   * @param type Type name
   */
  public static int typeFromName(String type) throws HdbFailed {

    if (type.equalsIgnoreCase("scalar_devulong64_rw")) {
      return TYPE_SCALAR_ULONG64_RW;
    } else if (type.equalsIgnoreCase("array_devstring_rw")) {
      return TYPE_ARRAY_STRING_RW;
    } else if (type.equalsIgnoreCase("scalar_devlong64_ro")) {
      return TYPE_SCALAR_LONG64_RO;
    } else if (type.equalsIgnoreCase("scalar_devfloat_rw")) {
      return TYPE_SCALAR_FLOAT_RW;
    } else if (type.equalsIgnoreCase("array_devfloat_ro")) {
      return TYPE_ARRAY_FLOAT_RO;
    } else if (type.equalsIgnoreCase("array_devdouble_ro")) {
      return TYPE_ARRAY_DOUBLE_RO;
    } else if (type.equalsIgnoreCase("scalar_devstring_ro")) {
      return TYPE_SCALAR_STRING_RO;
    } else if (type.equalsIgnoreCase("array_devlong_ro")) {
      return TYPE_ARRAY_LONG_RO;
    } else if (type.equalsIgnoreCase("scalar_devuchar_ro")) {
      return TYPE_SCALAR_UCHAR_RO;
    } else if (type.equalsIgnoreCase("scalar_devlong_ro")) {
      return TYPE_SCALAR_LONG_RO;
    } else if (type.equalsIgnoreCase("array_devencoded_ro")) {
      return TYPE_ARRAY_ENCODED_RO;
    } else if (type.equalsIgnoreCase("array_devlong64_rw")) {
      return TYPE_ARRAY_LONG64_RW;
    } else if (type.equalsIgnoreCase("array_devlong_rw")) {
      return TYPE_ARRAY_LONG_RW;
    } else if (type.equalsIgnoreCase("array_devushort_rw")) {
      return TYPE_ARRAY_USHORT_RW;
    } else if (type.equalsIgnoreCase("scalar_devlong64_rw")) {
      return TYPE_SCALAR_LONG64_RW;
    } else if (type.equalsIgnoreCase("scalar_devboolean_ro")) {
      return TYPE_SCALAR_BOOLEAN_RO;
    } else if (type.equalsIgnoreCase("array_devstate_rw")) {
      return TYPE_ARRAY_STATE_RW;
    } else if (type.equalsIgnoreCase("scalar_devdouble_rw")) {
      return TYPE_SCALAR_DOUBLE_RW;
    } else if (type.equalsIgnoreCase("scalar_devencoded_ro")) {
      return TYPE_SCALAR_ENCODED_RO;
    } else if (type.equalsIgnoreCase("array_devdouble_rw")) {
      return TYPE_ARRAY_DOUBLE_RW;
    } else if (type.equalsIgnoreCase("scalar_devshort_rw")) {
      return TYPE_SCALAR_SHORT_RW;
    } else if (type.equalsIgnoreCase("scalar_devlong_rw")) {
      return TYPE_SCALAR_LONG_RW;
    } else if (type.equalsIgnoreCase("scalar_devushort_rw")) {
      return TYPE_SCALAR_USHORT_RW;
    } else if (type.equalsIgnoreCase("array_devulong64_ro")) {
      return TYPE_ARRAY_ULONG64_RO;
    } else if (type.equalsIgnoreCase("scalar_devulong64_ro")) {
      return TYPE_SCALAR_ULONG64_RO;
    } else if (type.equalsIgnoreCase("array_devulong_rw")) {
      return TYPE_ARRAY_ULONG_RW;
    } else if (type.equalsIgnoreCase("array_devlong64_ro")) {
      return TYPE_ARRAY_LONG64_RO;
    } else if (type.equalsIgnoreCase("scalar_devfloat_ro")) {
      return TYPE_SCALAR_FLOAT_RO;
    } else if (type.equalsIgnoreCase("array_devuchar_rw")) {
      return TYPE_ARRAY_UCHAR_RW;
    } else if (type.equalsIgnoreCase("scalar_devdouble_ro")) {
      return TYPE_SCALAR_DOUBLE_RO;
    } else if (type.equalsIgnoreCase("scalar_devstring_rw")) {
      return TYPE_SCALAR_STRING_RW;
    } else if (type.equalsIgnoreCase("array_devstring_ro")) {
      return TYPE_ARRAY_STRING_RO;
    } else if (type.equalsIgnoreCase("scalar_devshort_ro")) {
      return TYPE_SCALAR_SHORT_RO;
    } else if (type.equalsIgnoreCase("scalar_devboolean_rw")) {
      return TYPE_SCALAR_BOOLEAN_RW;
    } else if (type.equalsIgnoreCase("scalar_devulong_ro")) {
      return TYPE_SCALAR_ULONG_RO;
    } else if (type.equalsIgnoreCase("array_devulong64_rw")) {
      return TYPE_ARRAY_ULONG64_RW;
    } else if (type.equalsIgnoreCase("array_devencoded_rw")) {
      return TYPE_ARRAY_ENCODED_RW;
    } else if (type.equalsIgnoreCase("scalar_devushort_ro")) {
      return TYPE_SCALAR_USHORT_RO;
    } else if (type.equalsIgnoreCase("array_devshort_ro")) {
      return TYPE_ARRAY_SHORT_RO;
    } else if (type.equalsIgnoreCase("scalar_devstate_ro")) {
      return TYPE_SCALAR_STATE_RO;
    } else if (type.equalsIgnoreCase("scalar_devuchar_rw")) {
      return TYPE_SCALAR_UCHAR_RW;
    } else if (type.equalsIgnoreCase("array_devfloat_rw")) {
      return TYPE_ARRAY_FLOAT_RW;
    } else if (type.equalsIgnoreCase("scalar_devstate_rw")) {
      return TYPE_SCALAR_STATE_RW;
    } else if (type.equalsIgnoreCase("array_devulong_ro")) {
      return TYPE_ARRAY_ULONG_RO;
    } else if (type.equalsIgnoreCase("array_devboolean_ro")) {
      return TYPE_ARRAY_BOOLEAN_RO;
    } else if (type.equalsIgnoreCase("array_devshort_rw")) {
      return TYPE_ARRAY_SHORT_RW;
    } else if (type.equalsIgnoreCase("array_devuchar_ro")) {
      return TYPE_ARRAY_UCHAR_RO;
    } else if (type.equalsIgnoreCase("scalar_devulong_rw")) {
      return TYPE_SCALAR_ULONG_RW;
    } else if (type.equalsIgnoreCase("array_devboolean_rw")) {
      return TYPE_ARRAY_BOOLEAN_RW;
    } else if (type.equalsIgnoreCase("array_devushort_ro")) {
      return TYPE_ARRAY_USHORT_RO;
    } else if (type.equalsIgnoreCase("array_devstate_ro")) {
      return TYPE_ARRAY_STATE_RO;
    } else if (type.equalsIgnoreCase("scalar_devencoded_rw")) {
      return TYPE_SCALAR_ENCODED_RW;
    } else {
      throw new HdbFailed("'" + type + "' : Unknown type");
    }

  }

  public String name;   // Attribute name
  public String sigId;  // Identifier
  public int    type;   // Data type

  public String toString() {
    return "Id=" + sigId + ",Type=" + Integer.toString(type);
  }

}
