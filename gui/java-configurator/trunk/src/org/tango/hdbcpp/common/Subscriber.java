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

import fr.esrf.Tango.AttrQuality;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.*;
import fr.esrf.TangoApi.events.ITangoChangeListener;
import fr.esrf.TangoApi.events.TangoChangeEvent;
import fr.esrf.TangoApi.events.TangoEventsAdapter;
import fr.esrf.TangoDs.TangoConst;
import org.tango.hdbcpp.tools.ArchiverUtils;
import org.tango.hdbcpp.tools.TangoUtils;
import org.tango.hdbcpp.tools.Utils;

import java.util.ArrayList;
import java.util.List;


//======================================================
//======================================================
public class Subscriber extends DeviceProxy {
    public String name;
    protected String label;
    protected String startedFilter;
    protected String stoppedFilter;
    protected String pausedFilter;
    protected String[] startedAttributes = new String[0];
    protected String[] stoppedAttributes = new String[0];
    protected String[] pausedAttributes  = new String[0];

    public static final int ATTRIBUTE_STARTED = 0;
    public static final int ATTRIBUTE_PAUSED  = 1;
    public static final int ATTRIBUTE_STOPPED = 2;
    public static final String CLASS_NAME = "HdbEventSubscriber";
    //======================================================
    //======================================================
    public Subscriber(String deviceName, String label) throws DevFailed {
        super(deviceName);
        this.name  = deviceName;
        this.label = label;
        startedFilter = "*/*/*/*/*";
        stoppedFilter = "*/*/*/*/*";
        pausedFilter  = "*/*/*/*/*";

        //  Subscribe to attribute lists events
        TangoEventsAdapter  adapter = new TangoEventsAdapter(this);
        ChangeEventListener changeListener = new ChangeEventListener();

        adapter.addTangoChangeListener(changeListener, "AttributeStartedList", TangoConst.STATELESS);
        adapter.addTangoChangeListener(changeListener, "AttributeStoppedList", TangoConst.STATELESS);
        adapter.addTangoChangeListener(changeListener, "AttributePausedList",  TangoConst.STATELESS);
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
    //======================================================
    //======================================================
    public String getPausedFilter() {
        return pausedFilter;
    }
    //======================================================
    //======================================================
    public void setPausedFilter(String pausedFilter) {
        this.pausedFilter = pausedFilter;
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
                System.err.println(e.getMessage());
            }
        }
        //  Check device property
        datum = get_property(propertyName);
        if (!datum.is_empty()) {
            try {
                value = datum.extractLong();
            }
            catch (NumberFormatException e) {
                System.err.println(e.getMessage());
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
            System.err.println(e.errors[0].desc);
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
    public String[] getAttributeList(int attributeState, boolean filtered) {
        switch (attributeState) {
            case ATTRIBUTE_STARTED:
                if (filtered)
                    return Utils.matchFilter(startedAttributes, startedFilter);
                else
                    return startedAttributes;
            case ATTRIBUTE_STOPPED:
                if (filtered)
                    return Utils.matchFilter(stoppedAttributes, stoppedFilter);
                else
                    return stoppedAttributes;
            case ATTRIBUTE_PAUSED:
                if (filtered)
                    return Utils.matchFilter(pausedAttributes, pausedFilter);
                else
                    return pausedAttributes;
        }
        return new String[] { "Unexpected type list"};
    }
    //======================================================
    //======================================================
    public void addAttribute(String attributeName) throws DevFailed {
        DeviceData argIn = new DeviceData();
        argIn.insert(attributeName);
        this.command_inout("AttributeAdd", argIn);
    }
    //======================================================
    //======================================================
    public void removeAttribute(String attributeName) throws DevFailed {
        ArchiverUtils.removeAttribute(this, attributeName);
    }
    //======================================================================
    /**
     * Start the archiving for specified attribute on specified subscriber
     * @param attributeName  specified attribute name
     * @throws DevFailed in case of read device failed.
     */
    //======================================================================
    public void startAttribute(String attributeName) throws DevFailed {
        ArchiverUtils.startAttribute(this, attributeName);
    }
    //======================================================================
    /**
     * Stop the archiving for specified attribute on specified subscriber
     * @param attributeName  specified attribute name
     * @throws DevFailed in case of read device failed.
     */
    //======================================================================
    @SuppressWarnings("unused")
    public  void stopAttribute(String attributeName) throws DevFailed {
        ArchiverUtils.stopAttribute(this, attributeName);
    }
    //======================================================================
    /**
     * Pause the archiving for specified attribute on specified subscriber
     * @param attributeName  specified attribute name
     * @throws DevFailed in case of read device failed.
     */
    //======================================================================
    public  void pauseAttribute(String attributeName) throws DevFailed {
        ArchiverUtils.pauseAttribute(this, attributeName);
    }
    //======================================================
    //======================================================
    public String getAttributeStatus(String attributeName) throws DevFailed {
        DeviceData  argIn = new DeviceData();
        argIn.insert(attributeName);
        DeviceData  argOut = command_inout("AttributeStatus", argIn);
        return argOut.extractString();
    }
    //======================================================
    //======================================================




    //=========================================================================
    /**
     * Change event listener
     */
    //=========================================================================
    public class ChangeEventListener implements ITangoChangeListener {
        //=====================================================================
        private void setError(String message) {
            startedAttributes = new String[] { "!!! " + message };
            stoppedAttributes = new String[] { "!!! " + message };
            pausedAttributes  = new String[] { "!!! " + message };
        }
        //=====================================================================
        public void change(TangoChangeEvent event) {
            try {
                //	Get the attribute value
                DeviceAttribute attribute = event.getValue();
                if (attribute.getQuality()==AttrQuality.ATTR_VALID) {
                    if (attribute.getName().contains("Started"))
                        startedAttributes = attribute.extractStringArray();
                    else
                    if (attribute.getName().contains("Stopped"))
                        stoppedAttributes = attribute.extractStringArray();
                    else
                    if (attribute.getName().contains("Paused"))
                        pausedAttributes = attribute.extractStringArray();
                }
                else {
                    setError(attribute.getName() + " is invalid");
                }

            } catch (DevFailed e) {
                setError(e.errors[0].desc);
            } catch (Exception e) {
                e.printStackTrace();
                setError(e.getMessage());
            }
        }
    }
    //===============================================================
    //===============================================================
}

