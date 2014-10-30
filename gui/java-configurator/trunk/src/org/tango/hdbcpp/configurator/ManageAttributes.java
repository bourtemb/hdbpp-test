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

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.AttributeProxy;
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoDs.Except;
import org.tango.hdbcpp.tools.ArchiverUtils;
import org.tango.hdbcpp.tools.SplashUtils;
import org.tango.hdbcpp.tools.TangoUtils;

import java.util.ArrayList;


/**
 * This class is able to add/start/stop a list of attributes to a subscriber
 *
 *  <br><br>
     <i>
     <font COLOR="#3b648b">   <!--- DeepSkyBlue4 --->
     //  Example of using the HDB++ configurator API </Font>
     <br> <br>
            package my_package;
            <br>
            <br>
    import org.Tango.hdbcpp.configurator.HdbAttribute; <br>
    import org.Tango.hdbcpp.configurator.ManageAttributes; <br>
    import fr.esrf.TangoDs.Except; <br>
    import fr.esrf.Tango.DevFailed; <br>
            <br>
    public class MyAttributeManagement {
        <ul>
        public static void main (String args[])  {
            <ul>
            try {
                <ul>
                <FONT COLOR="#3b648b">
                        //  Create a hdb attribute list<br>
                        //  These attributes are pushed by the device code and
                        they will be started later<br>
                </Font>
                        ArrayList&lt;HdbAttribute&gt; hdbAttributes = new ArrayList&lt;HdbAttribute&gt;();<br>
                        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass12",
                                 ManageAttributes.PUSHED_BY_CODE, ManageAttributes.STOP_ARCHIVING));<br>
                        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass14",
                                 ManageAttributes.PUSHED_BY_CODE, ManageAttributes.STOP_ARCHIVING));<br>
                        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass15",
                                 ManageAttributes.PUSHED_BY_CODE, ManageAttributes.STOP_ARCHIVING));<br>
                        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass16",
                                 ManageAttributes.PUSHED_BY_CODE, ManageAttributes.STOP_ARCHIVING));<br>
                <br>
                <FONT COLOR="#3b648b">
                // Add these attributes to an archiver<br>
                </Font>
                        String archiver = "tango/hdb/es-2";<br>
                        ManageAttributes.addAttributes(archiver, hdbAttributes);<br>

                <br><br>
                <FONT COLOR="#3b648b">
                //  Create a attribute name list to be started<br>
                </Font>
                        ArrayList&lt;String&gt; attributeNames = new ArrayList&lt;String&gt;();<br>
                        attributeNames.add("sr/v-rga/c1-cv6000/mass12");<br>
                        attributeNames.add("sr/v-rga/c1-cv6000/mass14");<br>
                        attributeNames.add("sr/v-rga/c1-cv6000/mass15");<br>
                        attributeNames.add("sr/v-rga/c1-cv6000/mass16");<br>
                <br>
                <FONT COLOR="#3b648b">
                // Start archiving<br>
                </Font>
                        ManageAttributes.addAttributes(attributeNames);<br>
                </ul>
            }
            catch (DevFailed e) {
                <ul>
                        Except.print_exception(e);
                </ul>
            }
            </ul>
        }
        </ul>
    }
    </i>
    <br><br>
 *
 * @author verdier
 */

public class ManageAttributes {
    /** Archive event pushed by Tango library */
    @SuppressWarnings("UnusedDeclaration")
    public static final boolean PUSHED_BY_LIB   = false;
    /** Archive event pushed by Tango device code */
    public static final boolean PUSHED_BY_CODE  = true;
    /** Do not start archiving */
    public static final boolean STOP_ARCHIVING  = false;
    /** Start archiving */
    public static final boolean START_ARCHIVING = true;

    private static boolean display = false;
    //===============================================================
    //===============================================================
    public static void setDisplay(boolean b) {
        display = b;
    }
    //===============================================================
    /**
     *  Add a list of attributes to specified subscriber
     * @param subscriberName    specified subscriber device name
     * @param hdbAttributes     specified attribute to be added.
     * @throws DevFailed in case of bad subscriber name or connection failed.
     */
    //===============================================================
    public static void addAttributes(String subscriberName,
                                     ArrayList<HdbAttribute> hdbAttributes) throws DevFailed {
        if (hdbAttributes.size()==0)
            return;

        if (display) {
            SplashUtils.startSplash();
            SplashUtils.setSplashProgress(1, "Adding attributes");
        }
        double step = 100.0/hdbAttributes.size();

        //  Get Tango objects
        DeviceProxy configurator = new DeviceProxy(TangoUtils.getConfiguratorDeviceName());

        //  Check subscriber
        DeviceProxy subscriber = getSubscriber(configurator, subscriberName);


        //  And lock configurator before adding attributes
        StringBuilder   errors = new StringBuilder();
        ArchiverUtils.lockDevice(configurator);
        int cnt = 1;
        for (HdbAttribute hdbAttribute : hdbAttributes) {
            if (display)
                SplashUtils.setSplashProgress((int) (step*cnt++), "Adding "+hdbAttribute.getName());
            else
                System.out.println("Adding " + hdbAttribute.getName() + "\tto " + subscriberName);
            try {
                //  Try if device syntax ok
                new AttributeProxy(hdbAttribute.getName());
                //  Add it to archiver
                ArchiverUtils.addAttribute(configurator, subscriberName,
                        hdbAttribute.getName(), hdbAttribute.isPushedByCode(), false);
                //  And start it if needed
                if (hdbAttribute.needsStart()) {
                    ArchiverUtils.startAttribute(subscriber, hdbAttribute.getName());
                }
            }
            catch (DevFailed e) {
                errors.append(hdbAttribute.getName());
                errors.append("\n  (").append(e.errors[0].desc).append(")\n");
            }
        }
        if (display)
            SplashUtils.stopSplash();
        configurator.unlock();
        if (errors.length()>0)
            Except.throw_exception("AddingFailed", errors.toString());
    }
    //===============================================================
    /**
     * Start a list of attributes
     * @param attributes    specified attribute list
     * @throws DevFailed in case of connection failed.
     */
    //===============================================================
    public static void startAttributes(ArrayList<String> attributes) throws DevFailed {
        if (display) {
            SplashUtils.startSplash();
            SplashUtils.setSplashProgress(10, "Adding attributes");
        }
        int step = 90/attributes.size();
        if (step<1) step = 1;
        //  Get Tango objects
        DeviceProxy configurator = new DeviceProxy(TangoUtils.getConfiguratorDeviceName());

        try {
            String  previous = null;
            DeviceProxy archiver = null;
            for (String attribute : attributes) {
                if (display)
                    SplashUtils.increaseSplashProgress(step, "Starting "+attribute);
                else
                    System.out.println("Starting " + attribute);
                //  Check if archiver is the same or another one.
                String  archiverName = ArchiverUtils.getArchiver(configurator, attribute);
                if (archiver==null || !archiverName.equals(previous)) {
                    previous = archiverName;
                    archiver = new DeviceProxy(archiverName);
                }
                ArchiverUtils.startAttribute(configurator, TangoUtils.fullName(attribute));
            }
            if (display)
                SplashUtils.stopSplash();
        }
        catch (DevFailed e) {
            if (display)
                SplashUtils.stopSplash();
            throw e;
        }
    }
    //===============================================================
    /**
     * Stop a list of attributes
     * @param attributes    specified attribute list
     * @throws DevFailed in case of connection failed.
     */
    //===============================================================
    public static void stopAttributes(ArrayList<String> attributes) throws DevFailed {
        if (display) {
            SplashUtils.startSplash();
            SplashUtils.setSplashProgress(10, "Adding attributes");
        }
        int step = 90/attributes.size();
        if (step<1) step = 1;

        try {
            //  Get Tango objects
            DeviceProxy configurator = new DeviceProxy(TangoUtils.getConfiguratorDeviceName());

            String  previous = null;
            DeviceProxy archiver = null;
            for (String attribute : attributes) {
                if (display)
                    SplashUtils.increaseSplashProgress(step, "Stopping "+attribute);
                else
                    System.out.println("Stopping " + attribute);
                //  Check if archiver is the same or another one.
                String  archiverName = ArchiverUtils.getArchiver(configurator, attribute);
                if (archiver==null || !archiverName.equals(previous)) {
                    previous = archiverName;
                    archiver = new DeviceProxy(archiverName);
                }
                ArchiverUtils.stopAttribute(configurator, TangoUtils.fullName(attribute));
            }
            if (display)
                SplashUtils.stopSplash();
        }
        catch (DevFailed e) {
            if (display)
                SplashUtils.stopSplash();
            throw e;
        }
    }
    //===============================================================
    /**
     * Remove a list of attributes to the subscriber
     * @param attributes    specified attribute list
     * @throws DevFailed in case of connection failed.
     */
    //===============================================================
    public static void removeAttributes(ArrayList<String> attributes) throws DevFailed {
        if (display) {
            SplashUtils.startSplash();
            SplashUtils.setSplashProgress(10, "Adding attributes");
        }
        int step = 90/attributes.size();
        if (step<1) step = 1;

        try {
            //  Get Tango objects
            DeviceProxy configurator = new DeviceProxy(TangoUtils.getConfiguratorDeviceName());

            for (String attribute : attributes) {
                if (display)
                    SplashUtils.increaseSplashProgress(step, "Removing "+attribute);
                else
                    System.out.println("Removing " + attribute);
                ArchiverUtils.removeAttribute(configurator, TangoUtils.fullName(attribute));
            }
            if (display)
                SplashUtils.stopSplash();
        }
        catch (DevFailed e) {
            if (display)
                SplashUtils.stopSplash();
            throw e;
        }
    }
    //===============================================================
    //===============================================================
    private static DeviceProxy getSubscriber(DeviceProxy configurator, String subscriber) throws DevFailed {
        String[]  subscriberNames = ArchiverUtils.getSubscriberList(configurator);
        String fullName = TangoUtils.fullName(subscriber.toLowerCase());
        boolean found = false;
        for (String subscriberName : subscriberNames) {
            if (subscriberName.toLowerCase().equals(fullName))
                found = true;
        }
        if (!found)
            Except.throw_exception("SubscriberNotExists",
                    "Subscriber \"" + subscriber + "\" is not managed by " + configurator.name());
        return new DeviceProxy(subscriber);
    }
    //===============================================================
    //===============================================================
    //===============================================================

    //===============================================================
    //===============================================================
    public static void main(String[] args) {

        ArrayList<String> attributes = new ArrayList<String>();
        attributes.add("sr/v-rga/c1-cv6000/mass12");
        attributes.add("sr/v-rga/c1-cv6000/mass14");
        attributes.add("sr/v-rga/c1-cv6000/mass15");
        attributes.add("sr/v-rga/c1-cv6000/mass16");
        attributes.add("sr/v-rga/c1-cv6000/mass17");
        attributes.add("sr/v-rga/c1-cv6000/mass18");
        attributes.add("sr/v-rga/c1-cv6000/mass19");

        ArrayList<HdbAttribute> hdbAttributes = new ArrayList<HdbAttribute>();
        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass12", PUSHED_BY_CODE, START_ARCHIVING));
        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass14", PUSHED_BY_CODE, START_ARCHIVING));
        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass15", PUSHED_BY_CODE, START_ARCHIVING));
        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass16", PUSHED_BY_CODE, START_ARCHIVING));
        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass17", PUSHED_BY_CODE, START_ARCHIVING));
        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass18", PUSHED_BY_CODE, START_ARCHIVING));
        hdbAttributes.add(new HdbAttribute("sr/v-rga/c1-cv6000/mass19", PUSHED_BY_CODE, START_ARCHIVING));

        try {
            ManageAttributes.addAttributes("hdb++/es/2", hdbAttributes);
            ManageAttributes.stopAttributes(attributes);
        } catch (DevFailed e) {
            Except.print_exception(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
