//+======================================================================
// $Source:  $
//
// Project:   Tango
//
// Description:  java source code for Tango manager tool..
//
// $Author: pascal_verdier $
//
// Copyright (C) :      2004,2005,2006,2007,2008,2009,2010,2011,2012,2013,2014,
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
// $Revision: 25293 $
//
//-======================================================================


package org.tango.hdbcpp.configurator;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.AttributeInfoEx;
import fr.esrf.TangoApi.AttributeProxy;
import fr.esrf.TangoDs.Except;
import fr.esrf.TangoDs.TangoConst;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.ErrorPane;
import org.tango.hdbcpp.common.Utils;
import org.tango.hdbcpp.configurator.strategy.Strategy;
import org.tango.hdbcpp.configurator.strategy.StrategyPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


//===============================================================
/**
 * Class Description: Basic dialog archive event properties.
 *
 * @author verdier pascal
 */
//===============================================================


@SuppressWarnings({"MagicConstant", "Convert2Diamond"})
public class PropertyDialog extends JDialog implements TangoConst {
    private JFrame parent;
    private String attributeName;
    private AttributeProxy  attributeProxy = null;
    private AttributeInfoEx attributeInfoEx;
    private boolean manageProperties;

    private boolean canceled = false;
    private static final int MaxRows = 30;
    static final boolean useStrategy = false;
    //===============================================================
    /**
     * Creates new form PropertyDialog for one attribute
     */
    //===============================================================
    public PropertyDialog(JFrame parent, String attributeName,
                          List<String> subscribers, String defaultItem) throws DevFailed {
        super(parent, true);
        this.parent = parent;
        this.attributeName = attributeName;
        initComponents();
        initOwnComponents();

        subscriberComboBox.removeAllItems();
        for (String subscriber : subscribers)
            subscriberComboBox.addItem(subscriber);
        subscriberComboBox.setSelectedItem(defaultItem);

        manageProperties = true;
        displayProperty();

        titleLabel.setText(attributeName);
        attributeListScrollPane.setVisible(false);
        pack();
        ATKGraphicsUtils.centerDialog(this);
    }
    //===============================================================
    /**
     * Creates new form PropertyDialog for a list of attributes
     */
    //===============================================================
    public PropertyDialog(JFrame parent, List<String> attributeNames,
                          List<String> subscribers, String defaultItem) {
        super(parent, true);
        this.parent = parent;
        initComponents();
        initOwnComponents();

        subscriberComboBox.removeAllItems();
        for (String subscriber : subscribers)
            subscriberComboBox.addItem(subscriber);
        subscriberComboBox.setSelectedItem(defaultItem);

        manageProperties = false;
        propertyPanel.setVisible(false);

        titleLabel.setVisible(false);
        StringBuilder   sb = new StringBuilder();
        int length = 0;
        for (String attributeName : attributeNames) {
            sb.append(attributeName).append("\n");
            if (attributeName.length()>length)
                length = attributeName.length();
        }
        int nbRows = attributeNames.size();
        if (attributeNames.size()>MaxRows)
            nbRows = MaxRows;
        attributeListArea.setRows(nbRows);
        attributeListArea.setColumns(length + 1);
        attributeListArea.setText(sb.toString().trim());
        attributeListArea.setEditable(false);

        pack();
        ATKGraphicsUtils.centerDialog(this);
    }
    //===============================================================
    //===============================================================
    private void initOwnComponents() {
        //  Change panel look for property and subscription panels
        propertyPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Event Properties"));
        subscriptionPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Events Subscription"));

        // Add tooltips
        pushedByCodeButton.setToolTipText(Utils.buildTooltip("Select if event is pushed by code"));
        startArchivingButton.setToolTipText(Utils.buildTooltip("Select to start archiving at subscription"));
        archiverLabel.setToolTipText(Utils.buildTooltip("Select archiver to manage storage"));
        subscriberComboBox.setToolTipText(Utils.buildTooltip("Select archiver to manage storage"));

        //  Add strategy panel
        if (useStrategy)
            addStrategyPanel();
    }
    //===============================================================
    //===============================================================
    private void addStrategyPanel() {
        //  ToDo get strategy list/info from property ?
        List<Strategy>	list = new ArrayList<>();
        list.add(new Strategy("Shutdown", true,
                "Accelerator is shutdown\n" +
                        "A majority of equipments are down\nand attributes must not be stored in HDB"));
        list.add(new Strategy("USM", true,
                "User mode\nAccelerator is running for users."));
        list.add(new Strategy("MDT", true,
                "Machine Dedicated Day\nAccelerator is used for tests or short maintenance"));
        list.add(new Strategy("Safety tests", false, "Safety group is doing measurements"));
        list.add(new Strategy("ID tests", false, "Insertion devices group is doing measurements"));

        StrategyPanel strategyPanel = new StrategyPanel(list);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.insets = new java.awt.Insets(10, 30, 0, 0);
        subscriptionPanel.add(strategyPanel, gbc);
    }
    //===============================================================
    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    //===============================================================
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JPanel topPanel = new javax.swing.JPanel();
        javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
        javax.swing.JLabel titleLabel1 = new javax.swing.JLabel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        attributeListScrollPane = new javax.swing.JScrollPane();
        attributeListArea = new javax.swing.JTextArea();
        javax.swing.JPanel centerPanel = new javax.swing.JPanel();
        subscriptionPanel = new javax.swing.JPanel();
        startArchivingButton = new javax.swing.JRadioButton();
        pushedByCodeButton = new javax.swing.JRadioButton();
        subscriberComboBox = new javax.swing.JComboBox<String>();
        archiverLabel = new javax.swing.JLabel();
        propertyPanel = new javax.swing.JPanel();
        javax.swing.JLabel absLbl = new javax.swing.JLabel();
        javax.swing.JLabel relLbl = new javax.swing.JLabel();
        javax.swing.JLabel periodLbl = new javax.swing.JLabel();
        absTxt = new javax.swing.JTextField();
        relTxt = new javax.swing.JTextField();
        eventPeriodTxt = new javax.swing.JTextField();
        javax.swing.JButton resetAbsBtn = new javax.swing.JButton();
        javax.swing.JButton resetRelBtn = new javax.swing.JButton();
        javax.swing.JButton resetPerBtn = new javax.swing.JButton();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        pollingPeriodTxt = new javax.swing.JTextField();
        javax.swing.JLabel dummyLabel = new javax.swing.JLabel();
        javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
        javax.swing.JButton updateBtn = new javax.swing.JButton();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JButton cancelBtn = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        topPanel.setLayout(new java.awt.BorderLayout());

        titleLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        titleLabel1.setText("Archive  Events  for");
        jPanel5.add(titleLabel1);

        topPanel.add(jPanel5, java.awt.BorderLayout.NORTH);

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        titleLabel.setText("Attribute Name");
        jPanel2.add(titleLabel);

        attributeListArea.setBackground(new java.awt.Color(240, 240, 240));
        attributeListArea.setColumns(20);
        attributeListArea.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        attributeListArea.setRows(5);
        attributeListScrollPane.setViewportView(attributeListArea);

        jPanel2.add(attributeListScrollPane);

        topPanel.add(jPanel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        centerPanel.setLayout(new java.awt.BorderLayout());

        subscriptionPanel.setLayout(new java.awt.GridBagLayout());

        startArchivingButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        startArchivingButton.setSelected(true);
        startArchivingButton.setText("Start Archiving");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        subscriptionPanel.add(startArchivingButton, gridBagConstraints);

        pushedByCodeButton.setText("Event pushed by code");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        subscriptionPanel.add(pushedByCodeButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        subscriptionPanel.add(subscriberComboBox, gridBagConstraints);

        archiverLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        archiverLabel.setText("Archiver: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        subscriptionPanel.add(archiverLabel, gridBagConstraints);

        centerPanel.add(subscriptionPanel, java.awt.BorderLayout.NORTH);

        propertyPanel.setLayout(new java.awt.GridBagLayout());

        absLbl.setText("absolute change:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        propertyPanel.add(absLbl, gridBagConstraints);

        relLbl.setText("relative change:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        propertyPanel.add(relLbl, gridBagConstraints);

        periodLbl.setText("event period (ms):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 20, 10);
        propertyPanel.add(periodLbl, gridBagConstraints);

        absTxt.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        propertyPanel.add(absTxt, gridBagConstraints);

        relTxt.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        propertyPanel.add(relTxt, gridBagConstraints);

        eventPeriodTxt.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 20, 10);
        propertyPanel.add(eventPeriodTxt, gridBagConstraints);

        resetAbsBtn.setText("Reset");
        resetAbsBtn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        resetAbsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetAbsBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        propertyPanel.add(resetAbsBtn, gridBagConstraints);

        resetRelBtn.setText("Reset");
        resetRelBtn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        resetRelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetRelBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        propertyPanel.add(resetRelBtn, gridBagConstraints);

        resetPerBtn.setText("Reset");
        resetPerBtn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        resetPerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetPerBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 20, 10);
        propertyPanel.add(resetPerBtn, gridBagConstraints);

        jLabel4.setText("Attribute polling period (ms):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 15, 10);
        propertyPanel.add(jLabel4, gridBagConstraints);

        pollingPeriodTxt.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 15, 10);
        propertyPanel.add(pollingPeriodTxt, gridBagConstraints);

        centerPanel.add(propertyPanel, java.awt.BorderLayout.SOUTH);

        dummyLabel.setText("     ");
        centerPanel.add(dummyLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        updateBtn.setText("Subscribe");
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });
        bottomPanel.add(updateBtn);

        jLabel1.setText("        ");
        bottomPanel.add(jLabel1);

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
    private void resetPerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPerBtnActionPerformed
        eventPeriodTxt.setText(Tango_AlrmValueNotSpec);
    }//GEN-LAST:event_resetPerBtnActionPerformed

    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void resetRelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetRelBtnActionPerformed
        relTxt.setText(Tango_AlrmValueNotSpec);
    }//GEN-LAST:event_resetRelBtnActionPerformed

    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void resetAbsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetAbsBtnActionPerformed
        absTxt.setText(Tango_AlrmValueNotSpec);
    }//GEN-LAST:event_resetAbsBtnActionPerformed

    //===============================================================
    /**
     * Verify if value set are coherent and if at least one is set.
     */
    //===============================================================
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean checkValues() {
        if (manageProperties) {
            try {
                String value;
                value = absTxt.getText().trim();
                if (!value.equals(Tango_AlrmValueNotSpec)) {
                    Double.parseDouble(value);
                }
                value = relTxt.getText().trim();
                if (!value.equals(Tango_AlrmValueNotSpec)) {
                    Double.parseDouble(value);
                }
                value = eventPeriodTxt.getText().trim();
                if (!value.equals(Tango_AlrmValueNotSpec)) {
                    Integer.parseInt(value);
                }
                value = pollingPeriodTxt.getText().trim();
                if (!value.equals("Not Polled")) {
                    Integer.parseInt(value);
                }
            } catch (Exception e) {
                ErrorPane.showErrorMessage(this, null, e);
                return false;
            }
        }
        return true;
    }

    //===============================================================
    //===============================================================
    private boolean writeValues() {
        if (manageProperties) {
            try {
                //	Get property values.
                boolean changed = false;
                if (attributeInfoEx.events.arch_event.abs_change!=null &&
                   !attributeInfoEx.events.arch_event.abs_change.equals(absTxt.getText().trim())) {
                    attributeInfoEx.events.arch_event.abs_change = absTxt.getText().trim();
                    changed = true;
                }
                if (attributeInfoEx.events.arch_event.rel_change!=null &&
                   !attributeInfoEx.events.arch_event.rel_change.equals(relTxt.getText().trim())) {
                    attributeInfoEx.events.arch_event.rel_change = relTxt.getText().trim();
                     changed = true;
                }
                if (attributeInfoEx.events.arch_event.period!=null &&
                   !attributeInfoEx.events.arch_event.period.equals(eventPeriodTxt.getText().trim())) {
                    attributeInfoEx.events.arch_event.period = eventPeriodTxt.getText().trim();
                    changed = true;
                }

                //	And set them if have changed
                if (changed)
                    attributeProxy.set_info(new AttributeInfoEx[]{ attributeInfoEx });

                //  Check for polling period
                String s = pollingPeriodTxt.getText().trim();
                if (!s.equals("Not Polled")) {
                    try {
                        int value = Integer.parseInt(s);
                        if (value!=pollingPeriod) {
                            attributeProxy.poll(value);
                        }
                    }
                    catch (NumberFormatException e) {
                        Except.throw_exception("SyntaxError", e.getMessage());
                    }
                }
                return true;
            } catch (DevFailed e) {
                ErrorPane.showErrorMessage(this, null, e);
                return false;
            }
        }
        else
            return true;
    }

    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        if (checkValues()) {
            if (writeValues())
                canceled = false;
                doClose();
        }
    }//GEN-LAST:event_updateBtnActionPerformed

    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        canceled = true;
        doClose();
    }//GEN-LAST:event_cancelBtnActionPerformed

    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        canceled = true;
        doClose();
    }//GEN-LAST:event_closeDialog

    //===============================================================
    /**
     * Closes the dialog
     */
    //===============================================================
    private void doClose() {
        if (parent==null)
            System.exit(0);

        setVisible(false);
        dispose();
    }
    //===============================================================
    //===============================================================
    private int pollingPeriod = 0;
    private void displayProperty() throws DevFailed {
        if (!manageProperties)
            return;
// ToDo
        if (attributeProxy== null)
            attributeProxy = new AttributeProxy(attributeName);
        attributeInfoEx = attributeProxy.get_info_ex();

        String abs_change;
        String rel_change;
        String period;
        if (attributeInfoEx.events != null && attributeInfoEx.events.arch_event != null) {
            abs_change = attributeInfoEx.events.arch_event.abs_change;
            rel_change = attributeInfoEx.events.arch_event.rel_change;
            period = attributeInfoEx.events.arch_event.period;
        } else {
            abs_change = Tango_AlrmValueNotSpec;
            rel_change = Tango_AlrmValueNotSpec;
            period = Tango_AlrmValueNotSpec;
        }
        absTxt.setText(abs_change);
        relTxt.setText(rel_change);
        eventPeriodTxt.setText(period);

        //  Add polling period
        try {
            pollingPeriod = attributeProxy.get_polling_period();
            pollingPeriodTxt.setText(Integer.toString(pollingPeriod));
        }
        catch (DevFailed e) {
            if (e.errors[0].desc.contains("not polled"))
                pollingPeriodTxt.setText("Not Polled");
            else
                throw e;
        }
    }

    //===============================================================
    //===============================================================
    public boolean startArchiving() {
        return startArchivingButton.getSelectedObjects()!=null;
    }
    //===============================================================
    //===============================================================
    public boolean isCanceled() {
        return canceled;
    }
    //===============================================================
    //===============================================================
    public boolean isPushedByCode() {
        return pushedByCodeButton.getSelectedObjects()!=null;
    }
    //===============================================================
    //===============================================================
    public String getSubscriber() {
        return (String) subscriberComboBox.getSelectedItem();
    }
    //===============================================================
    //===============================================================


    //===============================================================
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField absTxt;
    private javax.swing.JLabel archiverLabel;
    private javax.swing.JTextArea attributeListArea;
    private javax.swing.JScrollPane attributeListScrollPane;
    private javax.swing.JTextField eventPeriodTxt;
    private javax.swing.JTextField pollingPeriodTxt;
    private javax.swing.JPanel propertyPanel;
    private javax.swing.JRadioButton pushedByCodeButton;
    private javax.swing.JTextField relTxt;
    private javax.swing.JRadioButton startArchivingButton;
    private javax.swing.JComboBox<String> subscriberComboBox;
    private javax.swing.JPanel subscriptionPanel;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
    //===============================================================



    //===============================================================
    //===============================================================
    public static void main(String[] args) {

        try {
            String signal = "sy/ps-rips/manager/state";
            List<String> signals = new ArrayList<>();
            signals.add("sy/ps-rips/manager/state");
            signals.add("sy/ps-rips/manager/status");
            List<String> subscribers = new ArrayList<>();
            subscribers.add("Subscriber 1");
            subscribers.add("Subscriber 2");
            subscribers.add("Subscriber 3");
            new PropertyDialog(null, signal, subscribers, subscribers.get(2)).setVisible(true);
        } catch (Exception e) {
            ErrorPane.showErrorMessage(null, null, e);
        }

    }

}
