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


package org.tango.hdbcpp.configurator;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoDs.Except;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.ErrorPane;
import org.tango.hdbcpp.common.*;
import org.tango.hdbcpp.tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

//=======================================================
/**
 *	JFrame Class to display about HDB++ configuration
 *  and a tree to browse a control system to add attribute
 *  to a selected archive event subscriber to be written in HDB++.
 *
 * @author  Pascal Verdier
 */
//=======================================================
@SuppressWarnings("MagicConstant")
public class HdbConfigurator extends JFrame {


    private DeviceProxy configuratorProxy;
    private ListPopupMenu   menu = new ListPopupMenu();
    private JScrollPane     treeScrollPane;
    private AttributeTree   attributeTree;
    private SubscriberMap   subscriberMap;
    private JFileChooser    fileChooser = null;
    private JFrame          diagnosticsPanel = null;

    //=======================================================
    /**
	 *	Creates new form HdbConfigurator
	 */
	//=======================================================
    public HdbConfigurator() throws DevFailed {
        SplashUtils.startSplash();
        SplashUtils.increaseSplashProgress(10, "Building GUI");
        setTitle("HdbConfigurator  - " + SplashUtils.revNumber);
        initComponents();
        initOwnComponents();
        ManageAttributes.setDisplay(true);
		
        pack();
        ATKGraphicsUtils.centerFrameOnScreen(this);
        SplashUtils.stopSplash();
	}
	//=======================================================
	//=======================================================
    public DeviceProxy getConfiguratorProxy() {
        return configuratorProxy;
    }
	//=======================================================
	//=======================================================
    private void initOwnComponents() throws DevFailed {
        SplashUtils.increaseSplashProgress(15, "Reading devices");
        String  configuratorDeviceName = TangoUtils.getConfiguratorDeviceName();
        configuratorProxy = new DeviceProxy(configuratorDeviceName);

        //  Get subscriber labels if any
        subscriberMap = new SubscriberMap(configuratorProxy);
        archiverComboBox.removeAllItems();
        for (String subscriber : subscriberMap.getLabelList())
            archiverComboBox.addItem(subscriber);

        //	Add Action listeners
        startedAttrJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                listMouseClicked(event);    //	for list clicked, menu,...
            }
        });
        stoppedAttrJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                listMouseClicked(event);    //	for list clicked, menu,...
            }
        });

        //  Add a tree to select attribute
        SplashUtils.increaseSplashProgress(50, "Building Tree");
        attributeTree = new AttributeTree(this, TangoUtils.getEventTangoHost());
        treeScrollPane = new JScrollPane();
        treeScrollPane.setViewportView(attributeTree);
        treeScrollPane.setPreferredSize(new Dimension(500, 500));
        mainPanel.add(treeScrollPane, BorderLayout.CENTER);

        searchButton.setText("");
        searchButton.setIcon(Utils.getInstance().getIcon("search.gif"));
        addAttributeButton.setEnabled(false);

        //  Build file chooser
        fileChooser = new JFileChooser(new File("").getAbsolutePath());

        //  Build Title
        String title = titleLabel.getText();
        String archiveName = TangoUtils.getArchiveName(configuratorProxy);
        if (!archiveName.isEmpty()) {
            title += "  ("+archiveName+")";
            titleLabel.setText(title);
        }
        ImageIcon icon = Utils.getInstance().getIcon("hdbcpp.gif", 0.75);
        titleLabel.setIcon(icon);
        setIconImage(icon.getImage());

        //  Set device filter
        String filter = System.getenv("DeviceFilter");
        if (filter!=null && !filter.isEmpty()) {
            deviceFilterText.setText(filter);
            deviceFilterTextActionPerformed(null);
        }
        else
            deviceFilterText.setText("*/*/*");
    }
	//=======================================================
	//=======================================================
    public void changeTangoHost(String tangoHost) {
        try {
            String input = tangoHost;
            boolean ok = false;
            while (!ok) {
                //  Get Tango host string
                if ((input=JOptionPane.showInputDialog(this, "New TANGO_HOST", input))==null)
                    return;
                try {
                    //  Check if it an existing tango host
                    input = TangoUtils.getTangoHost(input);
                    ok = true;
                }
                catch (DevFailed e) {
                    ErrorPane.showErrorMessage(this, null, e);
                }
            }
            tangoHost = input;
            System.out.println("Set TANGO_HOST=" + input);

            //  Remove old tree
            if (attributeTree!=null) {
                attributeTree.removeAll();
                treeScrollPane.remove(attributeTree);
            }
            //  And finally, create the new one
            attributeTree = new AttributeTree(this, tangoHost);
            treeScrollPane.setViewportView(attributeTree);
            deviceFilterText.setText("*/*/*");
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
    }
	//=======================================================
	//=======================================================
    private void listMouseClicked(MouseEvent event) {
        JList   list = (JList) event.getSource();

        String  attributeName = (String) list.getSelectedValue();
        if (attributeName==null)
            return;
        int mask = event.getModifiers();

        //  Check button clicked
        if ((mask & MouseEvent.BUTTON3_MASK) != 0) {
            menu.showMenu(event, attributeName);
        }
    }
	//=======================================================
	//=======================================================
    private void updateAttributeList(Subscriber subscriber) throws DevFailed {
        String  startFilter = subscriber.getStartedFilter();
        String[]    startedAttributeList = ArchiverUtils.getAttributeList(subscriber, "Started");
        String[]    filtered = Utils.matchFilter(startedAttributeList, startFilter);
        startedAttrJList.setListData(filtered);
        startedAttrLabel.setText(Integer.toString(filtered.length) + " Started Attributes");

        String  stopFilter = subscriber.getStoppedFilter();
        String[]    stoppedAttributeList = ArchiverUtils.getAttributeList(subscriber, "Stopped");
        filtered = Utils.matchFilter(stoppedAttributeList, stopFilter);
        stoppedAttrJList.setListData(filtered);
        stoppedAttrLabel.setText(Integer.toString(filtered.length) + " Stopped Attributes");
    }

	//=======================================================
	//=======================================================
    public void displayPathInfo(String tangoHost, String attributeName) {

        //  Check if attribute name (or just domain, family,...)
        StringTokenizer stk = new StringTokenizer(attributeName, "/");
        addAttributeButton.setEnabled(stk.countTokens()==4);
        if(stk.countTokens()<4) {
            attributeField.setText(attributeName);
            propertiesArea.setText("");
            pollingArea.setText("");
            return;
        }

        String  s = attributeField.getText();
        if (!s.equals(attributeName)) {
            //  Update attribute field
            attributeField.setText(attributeName);

            //  Display properties and polling status
            if (!attributeName.isEmpty()) {
                try {
                    propertiesArea.setText(TangoUtils.getEventProperties(tangoHost, attributeName));
                    pollingArea.setText(TangoUtils.getAttPollingInfo(tangoHost, attributeName));
                }
                catch (DevFailed e) {
                    propertiesArea.setText("");
                    pollingArea.setText(Except.str_exception(e));
                }
            }
        }
    }
	//=======================================================
	//=======================================================
    private void moveAttributeToSubscriber(String targetSubscriberLabel, JList selectedList) {
        try {
            System.out.println("Move tor "+ targetSubscriberLabel);
            Object[] attributeNames = selectedList.getSelectedValues();
            ArrayList<String>   attributeList = new ArrayList<String>();
            for (Object attributeName : attributeNames) {
                attributeList.add((String) attributeName);
            }
            Subscriber targetSubscriber = subscriberMap.getSubscriber(targetSubscriberLabel);
            Subscriber srcSubscriber =
                    subscriberMap.getSubscriber((String) archiverComboBox.getSelectedItem());

            //  If started -> stop it before
            if (selectedList==startedAttrJList)
                ManageAttributes.stopAttributes(attributeList);

            //  Then remove attributes to subscribe and add to another
            SplashUtils.startSplash();
            SplashUtils.setSplashProgress(10, "Removing/Adding attributes");
            for (String attributeName : attributeList) {
                SplashUtils.increaseSplashProgressForLoop(
                        attributeList.size(), "Removing/adding "+attributeName);
                ArchiverUtils.removeAttribute(srcSubscriber, attributeName);
                ArchiverUtils.addAttribute(targetSubscriber, attributeName);
            }

            //  And restart if tey were started
            if (selectedList==startedAttrJList)
                ManageAttributes.startAttributes(attributeList);

            //  Set combo box selection to src subscriber
            archiverComboBox.setSelectedItem(targetSubscriberLabel);

            //  And update lists
            manageSubscriberChanged(targetSubscriberLabel);
            SplashUtils.stopSplash();
        }
        catch (DevFailed e) {
            SplashUtils.stopSplash();
            ErrorPane.showErrorMessage(this, null, e);
        }
    }
	//=======================================================
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
	//=======================================================
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        javax.swing.JPanel addingPanel = new javax.swing.JPanel();
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        attributeField = new javax.swing.JTextField();
        addAttributeButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        javax.swing.JPanel statusPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        deviceFilterText = new javax.swing.JTextField();
        javax.swing.JScrollPane propertiesScrollPane = new javax.swing.JScrollPane();
        propertiesArea = new javax.swing.JTextArea();
        javax.swing.JScrollPane pollingScrollPane = new javax.swing.JScrollPane();
        pollingArea = new javax.swing.JTextArea();
        javax.swing.JPanel eastPanel = new javax.swing.JPanel();
        javax.swing.JSplitPane splitPane = new javax.swing.JSplitPane();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JScrollPane startedAttrScrollPane = new javax.swing.JScrollPane();
        startedAttrJList = new javax.swing.JList();
        javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
        startedAttrLabel = new javax.swing.JLabel();
        startedFilterText = new javax.swing.JTextField();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        javax.swing.JPanel stoppedPanel = new javax.swing.JPanel();
        stoppedAttrLabel = new javax.swing.JLabel();
        stoppedFilterText = new javax.swing.JTextField();
        javax.swing.JScrollPane stoppedAttrScrollPane = new javax.swing.JScrollPane();
        stoppedAttrJList = new javax.swing.JList();
        javax.swing.JPanel archiverPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        archiverComboBox = new javax.swing.JComboBox();
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem openItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitItem = new javax.swing.JMenuItem();
        javax.swing.JMenu viewMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem diagnosticsItem = new javax.swing.JMenuItem();
        javax.swing.JMenu toolMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem addSubscriberItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem removeSubscriberItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem releaseNoteItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutItem = new javax.swing.JMenuItem();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        mainPanel.setLayout(new java.awt.BorderLayout());

        addingPanel.setLayout(new java.awt.GridBagLayout());

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titleLabel.setText("HDB++ Configurator");
        jPanel4.add(titleLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        addingPanel.add(jPanel4, gridBagConstraints);

        attributeField.setColumns(40);
        attributeField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        attributeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attributeFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        addingPanel.add(attributeField, gridBagConstraints);

        addAttributeButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        addAttributeButton.setText(" + ");
        addAttributeButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addAttributeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAttributeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        addingPanel.add(addAttributeButton, gridBagConstraints);

        searchButton.setText("Search");
        searchButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 0);
        addingPanel.add(searchButton, gridBagConstraints);

        mainPanel.add(addingPanel, java.awt.BorderLayout.NORTH);

        statusPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Device Filter:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        statusPanel.add(jLabel1, gridBagConstraints);

        deviceFilterText.setColumns(25);
        deviceFilterText.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        deviceFilterText.setText("*/*/*");
        deviceFilterText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deviceFilterTextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        statusPanel.add(deviceFilterText, gridBagConstraints);

        propertiesArea.setEditable(false);
        propertiesArea.setColumns(30);
        propertiesArea.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        propertiesArea.setRows(7);
        propertiesScrollPane.setViewportView(propertiesArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        statusPanel.add(propertiesScrollPane, gridBagConstraints);

        pollingArea.setEditable(false);
        pollingArea.setColumns(40);
        pollingArea.setRows(7);
        pollingScrollPane.setViewportView(pollingArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        statusPanel.add(pollingScrollPane, gridBagConstraints);

        mainPanel.add(statusPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        eastPanel.setLayout(new java.awt.BorderLayout());

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel2.setLayout(new java.awt.BorderLayout());

        startedAttrScrollPane.setPreferredSize(new java.awt.Dimension(500, 400));

        startedAttrScrollPane.setViewportView(startedAttrJList);

        jPanel2.add(startedAttrScrollPane, java.awt.BorderLayout.CENTER);

        startedAttrLabel.setText("Started Attributes:       tango://");
        jPanel5.add(startedAttrLabel);

        startedFilterText.setColumns(25);
        startedFilterText.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        startedFilterText.setText("*/*/*/*/*");
        startedFilterText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startedFilterTextActionPerformed(evt);
            }
        });
        jPanel5.add(startedFilterText);

        jPanel2.add(jPanel5, java.awt.BorderLayout.NORTH);

        splitPane.setLeftComponent(jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout());

        stoppedAttrLabel.setText("Stopped Attributes:      tango://");
        stoppedPanel.add(stoppedAttrLabel);

        stoppedFilterText.setColumns(25);
        stoppedFilterText.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        stoppedFilterText.setText("*/*/*/*/*");
        stoppedFilterText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stoppedFilterTextActionPerformed(evt);
            }
        });
        stoppedPanel.add(stoppedFilterText);

        jPanel3.add(stoppedPanel, java.awt.BorderLayout.NORTH);

        stoppedAttrScrollPane.setPreferredSize(new java.awt.Dimension(500, 200));

        stoppedAttrScrollPane.setViewportView(stoppedAttrJList);

        jPanel3.add(stoppedAttrScrollPane, java.awt.BorderLayout.CENTER);

        splitPane.setRightComponent(jPanel3);

        eastPanel.add(splitPane, java.awt.BorderLayout.CENTER);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Archiver:");
        archiverPanel.add(jLabel2);

        archiverComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        archiverComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                archiverComboBoxActionPerformed(evt);
            }
        });
        archiverPanel.add(archiverComboBox);

        eastPanel.add(archiverPanel, java.awt.BorderLayout.NORTH);

        getContentPane().add(eastPanel, java.awt.BorderLayout.EAST);

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        openItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openItem.setMnemonic('E');
        openItem.setText("Open Attribute List");
        openItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openItemActionPerformed(evt);
            }
        });
        fileMenu.add(openItem);

        exitItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitItem.setMnemonic('E');
        exitItem.setText("Exit");
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        viewMenu.setMnemonic('V');
        viewMenu.setText("View");

        diagnosticsItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        diagnosticsItem.setMnemonic('D');
        diagnosticsItem.setText("HDB++ Diagnostics");
        diagnosticsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diagnosticsItemActionPerformed(evt);
            }
        });
        viewMenu.add(diagnosticsItem);

        menuBar.add(viewMenu);

        toolMenu.setText("Tools");

        addSubscriberItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, java.awt.event.InputEvent.CTRL_MASK));
        addSubscriberItem.setText("Add Subscriber");
        addSubscriberItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubscriberItemActionPerformed(evt);
            }
        });
        toolMenu.add(addSubscriberItem);

        removeSubscriberItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, java.awt.event.InputEvent.CTRL_MASK));
        removeSubscriberItem.setText("Remove Subscriber");
        removeSubscriberItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSubscriberItemActionPerformed(evt);
            }
        });
        toolMenu.add(removeSubscriberItem);

        menuBar.add(toolMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("help");

        releaseNoteItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        releaseNoteItem.setMnemonic('A');
        releaseNoteItem.setText("Release Notes");
        releaseNoteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                releaseNoteItemActionPerformed(evt);
            }
        });
        helpMenu.add(releaseNoteItem);

        aboutItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        aboutItem.setMnemonic('A');
        aboutItem.setText("About");
        aboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	//=======================================================
	//=======================================================
    @SuppressWarnings("UnusedParameters")
    private void exitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitItemActionPerformed
        doClose();
    }//GEN-LAST:event_exitItemActionPerformed

	//=======================================================
	//=======================================================
    @SuppressWarnings("UnusedParameters")
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        doClose();
    }//GEN-LAST:event_exitForm

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void aboutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutItemActionPerformed
        String  message = "This application is able to configure HDB++\n" +
                "It is used to Add attributes to subscriber and\n" +
                "Start and Stop HDB filling for selected attributes\n" +
                "\nPascal Verdier - Accelerator Control Unit";
        JOptionPane.showMessageDialog(this, message, "Help Window", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_aboutItemActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void archiverComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_archiverComboBoxActionPerformed
        String  archiverLabel = (String) archiverComboBox.getSelectedItem();
        if (archiverLabel==null)
            return;
        manageSubscriberChanged(archiverLabel);
    }
    //=======================================================
    //=======================================================
    private void manageSubscriberChanged(String archiverLabel) {
        try {
            Subscriber subscriber = subscriberMap.getSubscriber(archiverLabel);
            startedFilterText.setText(subscriber.getStartedFilter());
            stoppedFilterText.setText(subscriber.getStoppedFilter());
            updateAttributeList(subscriberMap.getSubscriber(archiverLabel));
        }
        catch (DevFailed e) {
            String[]    filtered = new String[0];
            startedAttrJList.setListData(filtered);
            stoppedAttrJList.setListData(filtered);
            ErrorPane.showErrorMessage(this, null, e);
        }
    }//GEN-LAST:event_archiverComboBoxActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void attributeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attributeFieldActionPerformed
        //  If it is an attribute name, add it, otherwise search it
        String attributeName = attributeField.getText();
        StringTokenizer stk = new StringTokenizer(attributeName, "/");
        if (stk.countTokens()==4)
            addSpecifiedAttribute();
        else
            searchPath();
    }//GEN-LAST:event_attributeFieldActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void addAttributeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAttributeButtonActionPerformed

        ArrayList<String>   attributeNames = attributeTree.getSelectedAttributes();
        if (attributeNames.size()==0)   // nothing to do
            return;
        if (attributeNames.size()==1)   {
            //  Add one attribute
            addSpecifiedAttribute();
        }
        else {
            //  Add all selected attributes
            addAttributeList(attributeNames);
        }

    }//GEN-LAST:event_addAttributeButtonActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        searchPath();
    }//GEN-LAST:event_searchButtonActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void releaseNoteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_releaseNoteItemActionPerformed
        new PopupHtml(this).show(ReleaseNote.str);
    }//GEN-LAST:event_releaseNoteItemActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void openItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openItemActionPerformed
        if (fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file!=null) {
                if (file.isFile()) {
                    try {
                        String	filename = file.getAbsolutePath();
                        ArrayList<String>   attributeNames = Utils.readFileLines(filename);
                        addAttributeList(attributeNames);
                    }
                    catch (DevFailed e) {
                        ErrorPane.showErrorMessage(this, null, e);
                    }
                }
            }
        }
    }//GEN-LAST:event_openItemActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void stoppedFilterTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stoppedFilterTextActionPerformed
        try {
            Subscriber subscriber = subscriberMap.getSubscriber(
                    (String)archiverComboBox.getSelectedItem());
            subscriber.setStoppedFilter(stoppedFilterText.getText());
            updateAttributeList(subscriber);
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
    }//GEN-LAST:event_stoppedFilterTextActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void startedFilterTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startedFilterTextActionPerformed
        try {
            Subscriber subscriber = subscriberMap.getSubscriber(
                    (String)archiverComboBox.getSelectedItem());
            subscriber.setStartedFilter(startedFilterText.getText());
            updateAttributeList(subscriber);
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
    }//GEN-LAST:event_startedFilterTextActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void deviceFilterTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deviceFilterTextActionPerformed
        try {
            if (attributeTree==null)
                return;
            String tangoHost = attributeTree.getTangoHost();
            attributeTree.removeAll();
            treeScrollPane.remove(attributeTree);

            String filter = deviceFilterText.getText();
            StringTokenizer stk = new StringTokenizer(filter, "/");
            if (stk.countTokens()<3)
                Except.throw_exception("SyntaxError",
                        "Syntax error in device filter: " + filter + "\n" +
                                "Device name needs 3 fields : <domain>/<family>/<member>");

            attributeTree = new AttributeTree(this, tangoHost, filter);
            treeScrollPane.setViewportView(attributeTree);
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
    }//GEN-LAST:event_deviceFilterTextActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void diagnosticsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diagnosticsItemActionPerformed
        try {
            diagnosticsPanel = (JFrame) Utils.getInstance().startExternalApplication(
                    this, "org.tango.hdbcpp.diagnostics.HdbDiagnostics");
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, e.toString(), e);
        }
    }//GEN-LAST:event_diagnosticsItemActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void addSubscriberItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubscriberItemActionPerformed
        try {
            if (new CreateSubscriberPanel(this, configuratorProxy,
                    CreateSubscriberPanel.CREATE).showDialog()==JOptionPane.OK_OPTION) {
                // TODO reset application
                restartApplication();
            }
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
    }//GEN-LAST:event_addSubscriberItemActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void removeSubscriberItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSubscriberItemActionPerformed
        try {
            if (new CreateSubscriberPanel(this,  configuratorProxy,
                    CreateSubscriberPanel.REMOVE).showDialog()==JOptionPane.OK_OPTION) {
                // TODO reset application
                restartApplication();
            }
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
    }//GEN-LAST:event_removeSubscriberItemActionPerformed

	//=======================================================
	//=======================================================
    private void restartApplication() {
        try {
            new HdbConfigurator().setVisible(true);
            if (diagnosticsPanel!=null) {
                diagnosticsPanel.setVisible(false);
                diagnosticsPanel.dispose();
            }
            setVisible(false);
            dispose();
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
    }
	//=======================================================
	//=======================================================
    private void searchPath() {
        String  path = attributeField.getText();
        attributeTree.goToNode(path);
    }
    //=======================================================
    //=======================================================
    private void addAttributeList(ArrayList<String> attributeNames) {
         //  Get info for selected attributes
        PropertyDialog  propertyDialog = new PropertyDialog(
                this, attributeNames, subscriberMap.getLabelList(),
                (String) archiverComboBox.getSelectedItem());
        propertyDialog.setVisible(true);
        if (propertyDialog.isCanceled())
            return;

        //  If OK, add them
        String archiverName = propertyDialog.getSubscriber();

        try {
            Subscriber  subscriber = subscriberMap.getSubscriber(archiverName);
            boolean pushedByCode = propertyDialog.isPushedByCode();
            boolean startArchiving = propertyDialog.startArchiving();

            //  Build Hdb Attribute objects
            ArrayList<HdbAttribute> attributes = new ArrayList<HdbAttribute>();
            for (String attributeName : attributeNames) {
                attributes.add(new HdbAttribute(attributeName, pushedByCode, startArchiving));
            }
            //  Then add them
            ManageAttributes.addAttributes(subscriber.getName(), attributes);

        }
        catch (DevFailed e) {
            if (e.errors[0].desc.contains("\n"))
                new PopupHtml(this).show(
                        PopupHtml.toHtml(e.errors[0].desc), "Cannot Add Attributes");
            else
                ErrorPane.showErrorMessage(this, null, e);
        }
        //  And select archiver to display results
        selectArchiver(archiverName);
        attributeTree.updateAttributeInfo(attributeNames);
    }
	//=======================================================
	//=======================================================
    public void selectArchiver(String archiverName) {
        archiverComboBox.setSelectedItem(archiverName);
        manageSubscriberChanged(archiverName);
    }
	//=======================================================
	//=======================================================
    private boolean selectAttributeInList(String attributeName, JList jList) {
        ListModel  model = jList.getModel();
        for (int i=0 ; i<model.getSize() ; i++) {
            String  item = (String) model.getElementAt(i);
            if (item.equalsIgnoreCase(attributeName)) {
                jList.setSelectedValue(item, true);
                return true;
            }
        }
        return false;
    }
	//=======================================================
	//=======================================================
    public void selectAttributeInList(String attributeName) {
        if (!selectAttributeInList(attributeName, startedAttrJList))
            selectAttributeInList(attributeName, stoppedAttrJList);
    }
	//=======================================================
	//=======================================================
    private void addSpecifiedAttribute() {
        String  attributeName = attributeField.getText();
        String  fullName = TangoUtils.fullName(attributeTree.getTangoHost(), attributeName);
        addSpecifiedAttribute(fullName);
    }
	//=======================================================
	//=======================================================
    public void addSpecifiedAttribute(String attributeName) {
        try {
            PropertyDialog  propertyDialog = new PropertyDialog(
                    this, attributeName, subscriberMap.getLabelList(),
                    (String) archiverComboBox.getSelectedItem());
            propertyDialog.setVisible(true);

            if (propertyDialog.isCanceled())
                return;

            boolean pushedByCode = propertyDialog.isPushedByCode();
            boolean startArchiving = propertyDialog.startArchiving();
            Subscriber subscriber  = subscriberMap.getSubscriber(propertyDialog.getSubscriber());

            //  If OK add the attribute
            ArchiverUtils.addAttribute(configuratorProxy,
                    subscriber.getName(), attributeName, pushedByCode, true/*Lock*/);
            if (startArchiving)
                ArchiverUtils.startAttribute(subscriber, attributeName);

            updateAttributeList(subscriber);
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
    }
	//=======================================================
	//=======================================================
    public String getArchiverLabel(String deviceName) {
        return subscriberMap.getLabel(deviceName);
    }
	//=======================================================
	//=======================================================
    private void doClose() {
        if (diagnosticsPanel!=null && diagnosticsPanel.isVisible()) {
            setVisible(false);
            dispose();
        }
        else
            System.exit(0);
    }
	//=======================================================
    /**
     * @param args the command line arguments
     */
	//=======================================================
    public static void main(String args[]) {
		try {
      		new HdbConfigurator().setVisible(true);
		}
		catch(DevFailed e) {
            SplashUtils.stopSplash();
            ErrorPane.showErrorMessage(new Frame(), null, e);
            System.exit(0);
		}
    }


	//=======================================================
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAttributeButton;
    private javax.swing.JComboBox archiverComboBox;
    private javax.swing.JTextField attributeField;
    private javax.swing.JTextField deviceFilterText;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextArea pollingArea;
    private javax.swing.JTextArea propertiesArea;
    private javax.swing.JButton searchButton;
    private javax.swing.JList startedAttrJList;
    private javax.swing.JLabel startedAttrLabel;
    private javax.swing.JTextField startedFilterText;
    private javax.swing.JList stoppedAttrJList;
    private javax.swing.JLabel stoppedAttrLabel;
    private javax.swing.JTextField stoppedFilterText;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
	//=======================================================








    //======================================================
    /**
     * Popup menu class
     */
    //======================================================
    private static final int START_ARCHIVING  = 0;
    private static final int STOP_ARCHIVING   = 1;
    private static final int REMOVE_ATTRIBUTE = 2;
    private static final int MOVE_TO = 3;
    private static final int COPY_AS_TEXT = 4;

    private static final int OFFSET = 2;    //	Label And separator

    private static String[] menuLabels = {
            "Start Archiving", "Stop Archiving", "Remove Attribute", "Move Attribute To ", "Copy as Text",
    };
    //=======================================================
    //=======================================================
    private class ListPopupMenu extends JPopupMenu {
        private JLabel title;
        private JList  selectedList;
        private JMenu  subscriberMenu = new JMenu(menuLabels[MOVE_TO]);
        //======================================================
        private ListPopupMenu() {
            title = new JLabel();
            title.setFont(new java.awt.Font("Dialog", Font.BOLD, 12));
            add(title);
            add(new JPopupMenu.Separator());

            int i=0;
            for (String menuLabel : menuLabels) {
                if (menuLabel == null)
                    add(new Separator());
                else
                if (i++==MOVE_TO){
                    add(subscriberMenu);
                }
                else {
                    JMenuItem btn = new JMenuItem(menuLabel);
                    btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            menuActionPerformed(evt);
                        }
                    });
                    add(btn);
                }
            }
        }
        //======================================================
        private void setSubscriberMenu() {

            ArrayList<String> subscriberList = subscriberMap.getLabelList();
            String  selected = (String) archiverComboBox.getSelectedItem();
            subscriberMenu.removeAll();
            //subscriberList.remove(selected);

            for (String subscriber : subscriberList) {
                if (!subscriber.equals(selected)) {
                    JMenuItem item = new JMenuItem(subscriber);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            menuActionPerformed(evt);
                        }
                    });
                    subscriberMenu.add(item);
                }
            }
            subscriberMenu.setVisible(subscriberList.size()>1);
        }
        //======================================================
        private void showMenu(MouseEvent event, String attributeName) {
            title.setText(attributeName);
            setSubscriberMenu();

            selectedList = (JList) event.getSource();
            //noinspection PointlessArithmeticExpression
            getComponent(OFFSET + START_ARCHIVING).setVisible(selectedList==stoppedAttrJList);
            getComponent(OFFSET + STOP_ARCHIVING).setVisible(selectedList==startedAttrJList);
            show(selectedList, event.getX(), event.getY());
        }
        //======================================================
        private void menuActionPerformed(ActionEvent evt) {
            //	Check component source
            Object obj = evt.getSource();
            int itemIndex = -1;
            String  targetSubscriber = null;
            for (int i=0; i<menuLabels.length; i++)
                if (getComponent(OFFSET + i) == obj)
                    itemIndex = i;
            if (itemIndex<0) {
                //  Check subscriberMenu items
                JMenuItem item = (JMenuItem) evt.getSource();
                targetSubscriber = item.getText();
                itemIndex = MOVE_TO;
            }

            try {
                Subscriber  archiver =
                        subscriberMap.getSubscriber((String) archiverComboBox.getSelectedItem());
                Object[] attributeNames = selectedList.getSelectedValues();
                ArrayList<String>   attributeList = new ArrayList<String>();
                for (Object attributeName : attributeNames) {
                    attributeList.add((String) attributeName);
                }

                switch (itemIndex) {
                    case START_ARCHIVING:
                        ManageAttributes.startAttributes(attributeList);
                        updateAttributeList(archiver);
                        break;
                    case STOP_ARCHIVING:
                        ManageAttributes.stopAttributes(attributeList);
                       updateAttributeList(archiver);
                        break;
                    case REMOVE_ATTRIBUTE:
                        ManageAttributes.removeAttributes(attributeList);
                        updateAttributeList(archiver);
                        attributeTree.updateAttributeInfo(attributeList);
                        break;
                    case MOVE_TO:
                        moveAttributeToSubscriber(targetSubscriber, selectedList);
                        attributeTree.updateAttributeInfo(attributeList);
                        break;
                    case COPY_AS_TEXT:
                        copyAttributeAsText();
                        break;
                }
            }
            catch (DevFailed e) {
                ErrorPane.showErrorMessage(this, null, e);
            }
        }
        //======================================================
        private void copyAttributeAsText() {
            //  Put selection in a text area, select and copy to clipboard
            Object[] attributeNames = selectedList.getSelectedValues();
            StringBuilder   sb = new StringBuilder();
            for (Object attributeName : attributeNames) {
                sb.append(attributeName).append('\n');
            }
            JTextArea   textArea = new JTextArea(sb.toString());
            textArea.setSelectionStart(0);
            textArea.setSelectionEnd(sb.length());
            textArea.copy();
        }
        //======================================================
    }
    //======================================================
    //======================================================
}
