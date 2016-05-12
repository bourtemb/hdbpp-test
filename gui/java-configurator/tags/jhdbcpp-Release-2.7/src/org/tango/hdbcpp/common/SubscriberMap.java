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
    private List<String> labelList = new ArrayList<>();
    private Hashtable<String, Subscriber> label2device = new Hashtable<>();
    private Hashtable<String, String> deviceName2label = new Hashtable<>();
    private List<String> tangoHostList = new ArrayList<>();
    //======================================================
    //======================================================
    public SubscriberMap(DeviceProxy configuratorProxy) throws DevFailed {
        //  Get Subscriber labels
        List<String[]> labels = TangoUtils.getSubscriberLabels();
        //  Get Subscriber deviceName
        String[]    subscribers = ArchiverUtils.getSubscriberList(configuratorProxy);
        for (String subscriber : subscribers) {
            SplashUtils.getInstance().increaseSplashProgressForLoop(subscribers.length, "Building object " + subscriber);
            put(subscriber, labels);
        }
        StringComparator.sort(labelList);
    }
    //======================================================
    //======================================================
    private void put(String deviceName, List<String[]> labels) throws DevFailed {
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
    public int size() {
        return labelList.size();
    }
    //======================================================
    //======================================================
    public List<String> getLabelList() {
        return labelList;
    }
    //======================================================
    //======================================================
    public String getLabel(String deviceName) {
        return deviceName2label.get(deviceName);
    }
    //======================================================
    //======================================================
    public Subscriber getSubscriberByLabel(String label) throws DevFailed {
        Subscriber subscriber = label2device.get(label);
        if (subscriber==null)
            Except.throw_exception("NO_ARCHIVER",
                    "Subscriber \"" + label + "\" not found !");
        return subscriber;
    }
    //======================================================
    //======================================================
    public Subscriber getSubscriberByDevice(String deviceName) throws DevFailed {
        String label = getLabel(deviceName);
        if (label==null)
            Except.throw_exception("NO_ARCHIVER",
                    "Subscriber \"" + deviceName + "\" not found !");
        return getSubscriberByLabel(label);
    }
    //======================================================
    //======================================================
    public List<Subscriber> getSubscriberList() {
        Collection<Subscriber> collection = label2device.values();
        List<Subscriber> subscribers = new ArrayList<>();
        for (Subscriber subscriber : collection)
            subscribers.add(subscriber);
        return subscribers;
    }
    //======================================================
    //======================================================
    public List<String> getTangoHostList() {
        if (tangoHostList.isEmpty()) {
            Collection<Subscriber> collection = label2device.values();
            for (Subscriber subscriber : collection) {
                List<String> csList = subscriber.getTangoHostList();
                for (String cs : csList)
                    if (!((List) tangoHostList).contains(cs))
                        tangoHostList.add(cs);
            }
        }
        return tangoHostList;
    }
    //======================================================
    //======================================================
}
