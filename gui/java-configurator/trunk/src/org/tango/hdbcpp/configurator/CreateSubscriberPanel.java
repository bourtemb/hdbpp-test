//+======================================================================
// $Source:  $
//
// Project:   Tango
//
// Description:  Basic Dialog Class to display info
//
// $Author: pascal_verdier $
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

package org.tango.hdbcpp.configurator;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.*;
import fr.esrf.TangoDs.Except;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.ErrorPane;
import org.tango.hdbcpp.common.Subscriber;
import org.tango.hdbcpp.common.SubscriberMap;
import org.tango.hdbcpp.tools.ArchiverUtils;
import org.tango.hdbcpp.tools.TangoUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


//===============================================================
/**
 *	JDialog Class to display info
 *
 *	@author  Pascal Verdier
 */
//===============================================================


@SuppressWarnings("MagicConstant")
public class CreateSubscriberPanel extends JDialog {

	private JFrame	parent;
    private DeviceProxy configuratorProxy;
    private ArrayList<String>   instances = new ArrayList<String>();
    private ArrayList<String>   archivers = new ArrayList<String>();
    private ArrayList<String>   labels = new ArrayList<String>();
    private SubscriberMap       subscriberMap = null;   //  Used only to remove
    private int action;

    private int option = JOptionPane.OK_OPTION;
    private String exeFile = null;
    private static final String CLASS_NAME = "HdbEventSubscriber";
    public static final int CREATE = 0;
    public static final int REMOVE = 1;
	//===============================================================
	/**
	 *	Creates new form CreateSubscriberPanel
	 */
	//===============================================================
	public CreateSubscriberPanel(JFrame parent, DeviceProxy configuratorProxy, int action) throws DevFailed {
		super(parent, true);
		this.parent = parent;
        this.configuratorProxy = configuratorProxy;
        this.action = action;
		initComponents();

        subscriberMap = new SubscriberMap(configuratorProxy);
        if (action==CREATE) {
            String[]    archiverDevices = ArchiverUtils.getSubscriberList(configuratorProxy);
            deviceComboBox.addItem("");
            instanceComboBox.addItem("");
            labelComboBox.addItem("");
            for (String archiver : archiverDevices) {
                archivers.add(archiver);
                deviceComboBox.addItem(TangoUtils.getOnlyDeviceName(archiver));
                String  instance = getServerInstance(archiver);
                if (!instances.contains(instance)) {
                    instances.add(instance);
                    instanceComboBox.addItem(instance);
                }
            }
        }
        else {
            titleLabel.setText("Remove a Subscriber");
            instanceComboBox.setVisible(false);
            instanceLabel.setVisible(false);
            deviceComboBox.setVisible(false);
            deviceLabel.setVisible(false);
            labelComboBox.setEditable(false);
        }

        //  Put in a list to sort before
        labels = subscriberMap.getLabelList();//TangoUtils.getSubscriberLabels();
        //Collections.sort(labels, new StringComparator());
        for (String label : labels) {
            labelComboBox.addItem(label);
        }
        pack();
 		ATKGraphicsUtils.centerDialog(this);
	}

	//===============================================================
	//===============================================================
    private String getServerInstance(String deviceName) throws DevFailed {
        String serverName = new DeviceProxy(deviceName).get_server_name();
        int idx = serverName.indexOf('/');
        if (idx>0) {
            exeFile = serverName.substring(0, idx);
            return serverName.substring(idx+1);
        }
        else {
            exeFile = deviceName;
            return serverName;
        }
    }
	//===============================================================
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
	//===============================================================
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JPanel topPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        javax.swing.JPanel centerPanel = new javax.swing.JPanel();
        instanceLabel = new javax.swing.JLabel();
        deviceLabel = new javax.swing.JLabel();
        javax.swing.JLabel aliasLabel = new javax.swing.JLabel();
        instanceComboBox = new javax.swing.JComboBox();
        deviceComboBox = new javax.swing.JComboBox();
        labelComboBox = new javax.swing.JComboBox();
        javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
        javax.swing.JButton okBtn = new javax.swing.JButton();
        javax.swing.JButton cancelBtn = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        titleLabel.setText("Create a new Subscriber");
        topPanel.add(titleLabel);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        centerPanel.setLayout(new java.awt.GridBagLayout());

        instanceLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        instanceLabel.setText("Server instance:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        centerPanel.add(instanceLabel, gridBagConstraints);

        deviceLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        deviceLabel.setText("Device name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 12, 8, 12);
        centerPanel.add(deviceLabel, gridBagConstraints);

        aliasLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        aliasLabel.setText("Label:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 12);
        centerPanel.add(aliasLabel, gridBagConstraints);

        instanceComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        centerPanel.add(instanceComboBox, gridBagConstraints);

        deviceComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 12, 8, 12);
        centerPanel.add(deviceComboBox, gridBagConstraints);

        labelComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 12);
        centerPanel.add(labelComboBox, gridBagConstraints);

        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        okBtn.setText("OK");
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });
        bottomPanel.add(okBtn);

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });
        bottomPanel.add(cancelBtn);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	//===============================================================
	//===============================================================
    @SuppressWarnings("UnusedParameters")
	private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        try {
            if (action==CREATE) {
                if (addSubscriber())
                    doClose();
            }
            else {
                if (removeSubscriber())
                    doClose();
            }
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
	}//GEN-LAST:event_okBtnActionPerformed

	//===============================================================
	//===============================================================
    private boolean removeSubscriber() throws DevFailed {
        //  ToDo
        String label = ((String) labelComboBox.getSelectedItem()).trim();
        //  Check if field filled.
        if (label.isEmpty())
            Except.throw_exception("SyntaxError", "Label is not defined");

        //  Get confirmation
        Subscriber subscriber  = subscriberMap.getSubscriber(label);
        checkManagedAttributes(subscriber);

        String message = "Remove subscriber  " + label + "  (" + subscriber.name + ") ?";
        if (JOptionPane.showConfirmDialog(this,
                message, "Confirmation", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            DeviceData argIn = new DeviceData();
            argIn.insert(subscriber.name);
            configuratorProxy.command_inout("ArchiverRemove", argIn);
            manageArchiveLabels(TangoUtils.getOnlyDeviceName(subscriber.name), label);
            message = "Subscriber  " + label + "  (" + subscriber.name + " has been removed.\n\n" +
                    "WARNING: device " + subscriber.name + "  still exists in TANGO database";
            JOptionPane.showMessageDialog(this, message);
            return true;
        }
        else
            return false;

    }
	//===============================================================
	//===============================================================
    private void checkManagedAttributes(Subscriber subscriber) throws DevFailed {
        DbDatum datum = subscriber.get_property("AttributeList");
        if (!datum.is_empty())
            Except.throw_exception("NotEmpty",
                    subscriber.getLabel() + " still manage attribute(s).\nRemove attribute(s) before");
    }
	//===============================================================
	//===============================================================
    private boolean addSubscriber() throws DevFailed {
        //  ToDo

        String instance   = ((String) instanceComboBox.getSelectedItem()).trim();
        String deviceName = ((String) deviceComboBox.getSelectedItem()).trim();
        String label      = ((String) labelComboBox.getSelectedItem()).trim();
        //  Check if all fields filled.
        if (deviceName.isEmpty())
            Except.throw_exception("SyntaxError", "Device is not defined");
        if (label.isEmpty())
            Except.throw_exception("SyntaxError", "Label is not defined");
        if (instance.isEmpty())
            Except.throw_exception("SyntaxError", "Instance is not defined");

        //  Check instance
        boolean serverExists = instances.contains(instance);
        System.out.println(instance);
        //  Check device name (with tango host !!!)
        //if (archivers.contains(deviceName))
        boolean deviceExists = false;
        for (String archiver : archivers) {
            if (TangoUtils.getOnlyDeviceName(archiver).equals(deviceName))
                deviceExists = true;
        }
        if (deviceExists)
            Except.throw_exception("AlreadyExists", "Device " + deviceName + " already defined as subscriber");
        System.out.println(deviceName);
        //  Check label
        if (labels.contains(label))
            Except.throw_exception("AlreadyExists", "Label " + label+ " already defined");
        System.out.println(label);

        //  Build message to be displayed
        String message = "Device " + deviceName + " will be created ";
        if (serverExists)
            message += "in existing ";
        else
            message += "in new ";
        message += exeFile + '/' + instance + " server as " + CLASS_NAME + " class\n";
        message += "This device will be used with " + label + " label.";

        //  Get confirmation.
        if (JOptionPane.showConfirmDialog(this,
                message, "Confirmation", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            createArchiverDevice(instance, deviceName, serverExists);
            manageArchiveLabels(deviceName, label);
            addToManager(deviceName);
            if (serverExists)
                message = exeFile+'/'+instance + "  has been updated.\nYou can re-start it.";
            else
                message = exeFile+'/'+instance + "  has been created.\nYou can start it.";
            JOptionPane.showMessageDialog(this, message);
            return true;
        }
        else
            return false;
    }
	//===============================================================
	//===============================================================
    private void addToManager(String deviceName) throws DevFailed {
        DeviceData  argIn = new DeviceData();
        argIn.insert(deviceName);
        configuratorProxy.command_inout("ArchiverAdd", argIn);
    }
	//===============================================================
	//===============================================================
    private void manageArchiveLabels(String deviceName, String archiverLabel) throws DevFailed {

        String objectName = "HdbConfigurator";
        String propertyName = "ArchiverLabels";
        DbDatum   datum = ApiUtil.get_db_obj().get_property(objectName, propertyName);

        ArrayList<String>   labelList = new ArrayList<String>();
        String[]    labelArray;
        if (datum.is_empty()) {
            System.err.println("No archiver label found !");
        }
        else {
            //  Copy to a list to be sorted
            labelArray = datum.extractStringArray();
            Collections.addAll(labelList, labelArray);
        }

        if (action==CREATE) {
            //  Add new one and sort
            labelList.add(deviceName + ":  " + archiverLabel);
            Collections.sort(labelList, new StringComparator());
        }
        else {
            //  Remove
            for (String line : labelList)
                if (line.startsWith(deviceName+':')) {
                    labelList.remove(line);
                    break;
                }
        }
        //  Re-put in array to write property
        int i = 0;
        labelArray = new String[labelList.size()];
        for (String label : labelList) {
            labelArray[i++] = label;
        }
        datum = new DbDatum(propertyName, labelArray);
        ApiUtil.get_db_obj().put_property(objectName, new DbDatum[]{ datum });
    }
	//===============================================================
	//===============================================================
    private void createArchiverDevice(String instance, String deviceName, boolean severExists) throws DevFailed {
        String serverName = exeFile + '/' + instance;
        if (!severExists) {
            String adminName = "dserver/"+exeFile.toLowerCase() + "/" + instance;
            ApiUtil.get_db_obj().add_device(new DbDevInfo(adminName,  "DServer", serverName));
        }
        ApiUtil.get_db_obj().add_device(new DbDevInfo(deviceName, CLASS_NAME, serverName));

        //  Copy properties from another archiver device
        DeviceProxy archiverProxy = new DeviceProxy(deviceName);
        if (archivers.size()>0) {
            DeviceProxy anotherDevice = new DeviceProxy(archivers.get(0));
            String[]    propertyNames = anotherDevice.get_property_list("*");
            for (String propertyName : propertyNames) {
                if (!propertyName.toLowerCase().equals("attributelist") &&
                    !propertyName.startsWith("__")) {
                    archiverProxy.put_property(anotherDevice.get_property(propertyName));
                }
            }
        }
        System.out.println(serverName + " Created");
    }
    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        option = JOptionPane.CANCEL_OPTION;
        doClose();
    }//GEN-LAST:event_cancelBtnActionPerformed

    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        option = JOptionPane.CANCEL_OPTION;
        doClose();
    }//GEN-LAST:event_closeDialog

    //===============================================================
	//===============================================================
	private void doClose() {
	    if (parent==null)
			System.exit(0);
		else {
			setVisible(false);
			dispose();
		}
	}
    //===============================================================
    //===============================================================
    public int showDialog() {
        setVisible(true);
        return option;
    }
	//===============================================================
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox deviceComboBox;
    private javax.swing.JLabel deviceLabel;
    private javax.swing.JComboBox instanceComboBox;
    private javax.swing.JLabel instanceLabel;
    private javax.swing.JComboBox labelComboBox;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
	//===============================================================




	//===============================================================
	/**
	* @param args the command line arguments
	*/
	//===============================================================
	public static void main(String args[]) {
		try {
            String deviceName = System.getenv("HdbManager");
            if (deviceName==null)
                Except.throw_exception("DeviceNotDefined",
                        "HDB manager device not defined");
            new CreateSubscriberPanel(null,
                    new DeviceProxy(deviceName), CreateSubscriberPanel.REMOVE).showDialog();
		}
		catch(DevFailed e) {
            ErrorPane.showErrorMessage(new Frame(), null, e);
			System.exit(0);
		}
	}
    //=======================================================
    //=======================================================




    //======================================================
    /**
     * Comparator class to sort by labels
     */
    //======================================================
    @SuppressWarnings("UnusedDeclaration")
    private class LabelComparator implements Comparator<String> {
        public int compare(String s1, String s2) {

            if (s1 == null)      return 1;
            else if (s2 == null) return -1;
            else {
                int idx1 = s1.indexOf(':');
                if (idx1<0) return 1;
                int idx2 = s2.indexOf(':');
                if (idx2<0) return 1;
                String label1 = s1.substring(idx1 + 1).trim();
                String label2 = s2.substring(idx2 + 1).trim();
                return label1.compareTo(label2);
            }
        }
    }
   //======================================================
    /**
     * Comparator class to sort by Strings
     */
    //======================================================
    class StringComparator implements Comparator<String> {
        public int compare(String s1, String s2) {

            if (s1 == null)      return 1;
            else if (s2 == null) return -1;
            else {
                return s1.compareTo(s2);
            }
        }
    }
    //=======================================================
    //=======================================================
}
