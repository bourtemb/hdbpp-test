//+======================================================================
// :  $
//
// Project:   Tango
//
// Description:  java source code for Tango manager tool..
//
// : pascal_verdier $
//
// Copyright (C) :      2004,2005,2006,2007,2008,2009,2010,2011,2012,2013,
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
// :  $
//
//-======================================================================

package org.tango.hdbcpp.configurator;

/**
 * This class defines an attribute to be added to a subscriber
 *
 * @author verdier
 */

public class HdbAttribute {

    private String  name;
    private boolean pushedByCode;
    private boolean startIt;
    //===============================================================
    /**
     * Create a HdbAttribute object.
     * If it is used to add, the attribute will be subscribe without
     * pushed by code option and not started.
     *
     * @param name  specified attribute name.
     */
    //===============================================================
    public HdbAttribute(String name) {
        this.name = name;
    }
    //===============================================================
    /**
     * Create a HdbAttribute object
     * @param name            specified attribute name.
     * @param pushedByCode    true if event will be pushed by device code.
     * @param startIt         true if attribute archiving must be started.
     */
    //===============================================================
    public HdbAttribute(String name, boolean pushedByCode, boolean startIt) {
        this.name = name;
        this.pushedByCode = pushedByCode;
        this.startIt = startIt;
    }
    //===============================================================
    /**
     * Returns the attribute name
     * @return the attribute name
     */
    //===============================================================
    public String getName() {
        return name;
    }
    //===============================================================
    /**
     * Returns true if the event is pushed by the device class code.
     * @return true if the event is pushed by the device class code.
     */
    //===============================================================
    public boolean isPushedByCode() {
        return pushedByCode;
    }
    //===============================================================
    /**
     * Returns true if the attribute must be started when added to subscriber.
     * @return true if the attribute must be started when added to subscriber.
     */
    //===============================================================
    public boolean needsStart() {
        return startIt;
    }
    //===============================================================
    //===============================================================
}
