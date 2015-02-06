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
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoApi.DeviceProxy;
import org.tango.hdbcpp.tools.ArchiverUtils;
import org.tango.hdbcpp.tools.TangoUtils;

import java.util.ArrayList;
import java.util.List;


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
    public long getStatisticsResetTime() throws DevFailed {
        DeviceAttribute attribute = read_attribute("StatisticsResetTime");
        if (attribute.hasFailed())
            return 0;
        double nbSeconds = attribute.extractDouble();
        long nbMillis = (long) nbSeconds*1000;
        long now = System.currentTimeMillis();
        return now - nbMillis;
    }
    //=======================================================
    //=======================================================
    public int getStatisticsTimeWindow() throws DevFailed {
        int value = 2*3600;
        String propertyName = "StatisticsTimeWindow";
        //  Check class property
        DbDatum datum = new DbClass(CLASS_NAME).get_property(propertyName);
        if (!datum.is_empty()) {
            //  Why sometimes value is "Not specified" ???
            try {
                 value = datum.extractLong();
            }
            catch (NumberFormatException e) {
                System.err.println(e);
            }
        }
        //  Check device property
        datum = get_property(propertyName);
        if (!datum.is_empty()) {
            try {
                value = datum.extractLong();
            }
            catch (NumberFormatException e) {
                System.err.println(e);
            }
        }
        return value;
    }
    //======================================================
    //======================================================
    List<String> getTangoHostList() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            String[] attributeList = ArchiverUtils.getAttributeList(this, "");
            for (String attributeName : attributeList) {
                String csName = TangoUtils.getOnlyTangoHost(attributeName);
                if (!((List)list).contains(csName))
                    list.add(csName);
            }
        }
        catch (DevFailed e) {
            System.err.println(e);
            //  return an empty list
        }
        return list;
    }
    //======================================================
    //======================================================
    public String toString() {
        return name;
    }
    //======================================================
    //======================================================
}

