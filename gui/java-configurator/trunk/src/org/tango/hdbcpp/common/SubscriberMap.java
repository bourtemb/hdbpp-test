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
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoDs.Except;
import org.tango.hdbcpp.tools.ArchiverUtils;
import org.tango.hdbcpp.tools.SplashUtils;
import org.tango.hdbcpp.tools.StringComparator;
import org.tango.hdbcpp.tools.TangoUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;


//======================================================
/**
 * A class defining a Hash Table subscriber device name
 * and labels.
 * Key could be device name or its labels
 */
//======================================================
public class SubscriberMap {
    private ArrayList<String> labelList = new ArrayList<String>();
    private Hashtable<String, Subscriber> label2device = new Hashtable<String, Subscriber>();
    private Hashtable<String, String> deviceName2label = new Hashtable<String, String>();
    //======================================================
    //======================================================
    public SubscriberMap(DeviceProxy configuratorProxy) throws DevFailed {
        //  Get Subscriber labels
        ArrayList<String[]> labels = TangoUtils.getSubscriberLabels();
        //  Get Subscriber deviceName
        String[]    archivers = ArchiverUtils.getSubscriberList(configuratorProxy);
        for (String archiver : archivers) {
            SplashUtils.getInstance().increaseSplashProgressForLoop(archivers.length, "Building object " + archiver);
            put(archiver, labels);
        }
        StringComparator.sort(labelList);
    }
    //======================================================
    //======================================================
    private void put(String deviceName, ArrayList<String[]> labels) throws DevFailed {
        boolean found = false;
        //  Manage full device name
        String tgHost = "";
        if (deviceName.startsWith("tango://")) {
            int index = deviceName.indexOf('/', "tango://".length());
            tgHost = deviceName.substring(0, index)+'/';
        }
        for (String[] label : labels) {
            if (label.length>1) {
                String  devName = tgHost + label[0].toLowerCase();
                if (deviceName.toLowerCase().equals(devName)) {
                    labelList.add(label[1]);
                    label2device.put(label[1], new Subscriber(deviceName, label[1]));
                    deviceName2label.put(deviceName, label[1]);
                    found = true;
                }
            }
            else
                System.err.println("Syntax problem in \'SubscriberLabel\' property");
        }
        if (!found) {
            labelList.add(deviceName);  //  label is device name
            label2device.put(deviceName, new Subscriber(deviceName, deviceName));
            deviceName2label.put(deviceName, deviceName);
        }
    }
    //======================================================
    //======================================================
    public ArrayList<String> getLabelList() {
        return labelList;
    }
    //======================================================
    //======================================================
    public String getLabel(String deviceName) {
        return deviceName2label.get(deviceName);
    }
    //======================================================
    //======================================================
    public Subscriber getSubscriber(String label) throws DevFailed {
        Subscriber subscriber = label2device.get(label);
        if (subscriber==null)
            Except.throw_exception("NO_DEVICE",
                    "Subscriber \"" + label + "\" not found !");
        return subscriber;
    }
    //======================================================
    //======================================================
    public List<Subscriber> getSubscriberList() {
        Collection<Subscriber> collection = label2device.values();
        ArrayList<Subscriber> subscribers = new ArrayList<Subscriber>();
        for (Subscriber subscriber : collection)
            subscribers.add(subscriber);
        return subscribers;
    }
    //======================================================
    //======================================================
}
