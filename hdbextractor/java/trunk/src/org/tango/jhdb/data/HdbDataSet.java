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

import java.util.ArrayList;

/**
 * Set of HDB Data
 */
public class HdbDataSet {

  ArrayList<HdbData> data;

  /**
   * Construct an empty HdbDataSet
   */
  public HdbDataSet() {
    data = new ArrayList<HdbData>();
  }

  /**
   * Construct a HdbDataSet with the given HdbData
   */
  public HdbDataSet(ArrayList<HdbData> data) {
    this.data = data;
  }

  /**
   * Return size of this HdbDataSet
   * @return
   */
  public int size() {
    return data.size();
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

}


