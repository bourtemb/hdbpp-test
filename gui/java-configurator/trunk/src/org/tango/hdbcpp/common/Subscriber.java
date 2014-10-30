//+======================================================================
// $Source:  $
//
// Project:   Tango
//
// Description:  java source code for main swing class.
//
// $Author: verdier $
//
// Copyright (C) :      2004,2005,2006,2007,2008,2009,2009
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
// $Revision:  $
//
// $Log:  $
//
//-======================================================================


package org.tango.hdbcpp.common;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DbClass;
import fr.esrf.TangoApi.DbDatum;
import fr.esrf.TangoApi.DeviceProxy;


//======================================================
//======================================================
public class Subscriber extends DeviceProxy {
    public String name;
    protected String label;
    protected String startedFilter;
    protected String stoppedFilter;

    private static final String CLASS_NAME = "HdbEventSubscriber";
    //======================================================
    //======================================================
    public Subscriber(String deviceName, String label) throws DevFailed {
        super(deviceName);
        this.name  = deviceName;
        this.label = label;
        startedFilter = "*/*/*/*/*";
        stoppedFilter = "*/*/*/*/*";
    }
    //======================================================
    //======================================================
    public String getName() {
        return name;
    }
    //======================================================
    //======================================================
    public String getLabel() {
        return label;
    }
    //======================================================
    //======================================================
    public String getStartedFilter() {
        return startedFilter;
    }
    //======================================================
    //======================================================
    public void setStartedFilter(String startedFilter) {
        this.startedFilter = startedFilter;
    }
    //======================================================
    //======================================================
    public String getStoppedFilter() {
        return stoppedFilter;
    }
    //======================================================
    //======================================================
    public void setStoppedFilter(String stoppedFilter) {
        this.stoppedFilter = stoppedFilter;
    }
    //=======================================================
    //=======================================================
    public int getStatisticsTimeWindow() throws DevFailed {
        int value = 3600;
        String propertyName = "StatisticsTimeWindow";
        //  Check class property
        DbDatum datum = new DbClass(CLASS_NAME).get_property(propertyName);
        if (!datum.is_empty()) {
            value = datum.extractLong();
        }
        //  Check device property
        datum = get_property(propertyName);
        if (!datum.is_empty()) {
            value = datum.extractLong();
        }
        return value;
    }
    //=======================================================
    //======================================================
    public String toString() {
        return name;
    }
    //======================================================
    //======================================================
}

