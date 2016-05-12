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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
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
    private List<String>    tangoHostList;
    private UpdateListThread updateListThread;

    private static final String[]  strAttributeState = {
            "Started Attributes","Paused Attributes", "Stopped Attributes"
    };
    private static final Dimension treeDimension = new Dimension(350, 400);
    //=======================================================
    /**
	 *	Creates new form HdbConfigurator
	 */
	//=======================================================
    public HdbConfigurator() throws DevFailed {
        SplashUtils.getInstance().startSplash();
        SplashUtils.getInstance().increaseSplashProgress(10, "Building GUI");
        setTitle("HdbConfigurator  - " + SplashUtils.revNumber);
        initComponents();
        initOwnComponents();
        ManageAttributes.setDisplay(true);
        updateListThread = new UpdateListThread();
        updateListThread.start();

        pack();
        ATKGraphicsUtils.centerFrameOnScreen(this);
        SplashUtils.getInstance().stopSplash();
	}
	//=======================================================
	//=======================================================
    public DeviceProxy getConfiguratorProxy() {
        return configuratorProxy;
    }
	//=======================================================
	//=======================================================
    private void initOwnComponents() throws DevFailed {
        SplashUtils.getInstance().increaseSplashProgress(15, "Reading devices");
        String  configuratorDeviceName = TangoUtils.getConfiguratorDeviceName();
        configuratorProxy = new DeviceProxy(configuratorDeviceName);

        //  Get subscriber labels if any
        subscriberMap = new SubscriberMap(configuratorProxy);
        archiverComboBox.removeAllItems();
        for (String subscriber : subscriberMap.getLabelList())
            archiverComboBox.addItem(subscriber);

        //	Add Action listeners for list clicked, menu,...
        startedAttrJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                listMouseClicked(event);
            }
        });
        stoppedAttrJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                listMouseClicked(event);
            }
        });
        pausedAttrJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                listMouseClicked(event);
            }
        });

        //  Get used tango host list (used later to change tango host)
        tangoHostList = subscriberMap.getTangoHostList();

        //  Add a tree to select attribute
        SplashUtils.getInstance().increaseSplashProgress(50, "Building Tree");
        attributeTree = new AttributeTree(this, TangoUtils.getEventTangoHost());
        treeScrollPane = new JScrollPane();
        treeScrollPane.setViewportView(attributeTree);
        treeScrollPane.setPreferredSize(treeDimension);
        attrTreePanel.add(treeScrollPane, BorderLayout.CENTER);

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

            //  Check if extraction available
            String s = System.getenv("HdbExtraction");
            if (s!=null && s.equals("true"))
                System.setProperty("HDB_TYPE", archiveName);
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

        if (subscriberMap.size()==0)
            archiverLabel.setText("No subscriber defined ");
        else
       if (subscriberMap.size()==1)
           archiverLabel.setText("1 subscriber: ");
        else
           archiverLabel.setText(subscriberMap.size() + " subscribers: ");
    }
	//=======================================================
	//=======================================================
    public void changeTangoHost(String tangoHost) {
        try {
            Selector    selector = new Selector(this,
                    "Change Control System", "TANGO_HOST ?", tangoHostList, tangoHost);
            String  newTangoHost = selector.showDialog();
            if (newTangoHost!=null && !newTangoHost.isEmpty() && !newTangoHost.equals(tangoHost)) {
                tangoHost = TangoUtils.getTangoHost(newTangoHost);

                //  Check if it is a new one
                if (!tangoHostList.contains(tangoHost))
                    tangoHostList.add(tangoHost);

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
        //  Check if attribute or error
        if (attributeName.startsWith("!!!"))
            return;

        //  Check button clicked
        if ((event.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
            menu.showMenu(event, attributeName);
        }
    }
	//=======================================================
	//=======================================================
    private void changeArchivingStrategy(List<String> attributeList) throws DevFailed {

    }
	//=======================================================
	//=======================================================
    private void updateAttributeList(Subscriber subscriber) throws DevFailed {
        //  Get displayed list
        int selection =  tabbedPane.getSelectedIndex();
        String[] attributeList = subscriber.getAttributeList(selection, true);
        switch (selection) {
            case Subscriber.ATTRIBUTE_STARTED:
                updateAttributeList(startedAttrJList,
                        attributeList, startedAttrLabel, startedAttrScrollPane);
                break;
            case Subscriber.ATTRIBUTE_STOPPED:
                updateAttributeList(stoppedAttrJList,
                        attributeList, stoppedAttrLabel, stoppedAttrScrollPane);
                break;
            case Subscriber.ATTRIBUTE_PAUSED:
                updateAttributeList(pausedAttrJList,
                        attributeList, pausedAttrLabel, pausedAttrScrollPane);
                break;
        }
        //  Update pane label
       updatePaneTitle(subscriber, Subscriber.ATTRIBUTE_STARTED);
       updatePaneTitle(subscriber, Subscriber.ATTRIBUTE_PAUSED);
       updatePaneTitle(subscriber, Subscriber.ATTRIBUTE_STOPPED);
    }

	//=======================================================
	//=======================================================
    private void updatePaneTitle(Subscriber subscriber, int index) {
        int nb = subscriber.getAttributeList(index, false).length;
        tabbedPane.setTitleAt(index, nb + " " + strAttributeState[index]);
    }
	//=======================================================
	//=======================================================
    private void updateAttributeList(JList<String > jList, String[] attributes, JLabel jLabel, JScrollPane scrollPane) {

        //  Display attributes in jList
        attributes = StringComparator.sortArray(attributes);
        jList.setListData(attributes);
        String s = (attributes.length>1) ? "s" : "";
        jLabel.setText(Integer.toString(attributes.length) + " attribute"+s);

        //  move horizontal scroll bar to see end of attribute name
        JScrollBar horizontal = scrollPane.getVerticalScrollBar();
        horizontal.setValue(horizontal.getMaximum());
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
            return;
        }

        String  s = attributeField.getText();
        if (!s.equals(attributeName)) {
            //  Update attribute field
            attributeField.setText(attributeName);

            //  Display properties and polling status
            if (!attributeName.isEmpty()) {
                try {
                    propertiesArea.setText(TangoUtils.getAttPollingInfo(tangoHost, attributeName) +"\n" +
                            TangoUtils.getEventProperties(tangoHost, attributeName));
                }
                catch (DevFailed e) {
                    propertiesArea.setText(Except.str_exception(e));
                }
            }
        }
    }
	//=======================================================
	//=======================================================
    private void moveAttributeToSubscriber(String targetSubscriberLabel, JList<String> selectedList) {
        try {
            System.out.println("Move to " + targetSubscriberLabel);


            List<String>   attributeList = selectedList.getSelectedValuesList();
            Subscriber targetSubscriber = subscriberMap.getSubscriberByLabel(targetSubscriberLabel);
            Subscriber srcSubscriber =
                    subscriberMap.getSubscriberByLabel((String) archiverComboBox.getSelectedItem());

            //  Before everything, check if target is alive
            targetSubscriber.ping();

            //  If started -> stop it before (not necessary anymore)
            //if (selectedList==startedAttrJList)  ManageAttributes.stopAttributes(attributeList);

            //  Then remove attributes to subscribe and add to another
            SplashUtils.getInstance().startSplash();
            SplashUtils.getInstance().setSplashProgress(10, "Removing/Adding attributes");
            for (String attributeName : attributeList) {
                SplashUtils.getInstance().increaseSplashProgressForLoop(
                        attributeList.size(), "Removing/adding "+attributeName);
                //  Not bug less !
                //ArchiverUtils.moveAttribute(configuratorProxy, attributeName, srcSubscriber.name);
                srcSubscriber.removeAttribute(attributeName);
                targetSubscriber.addAttribute(attributeName);
            }
            //  Wait a bit. Add is done by a thread --> DevFailed(Not subscribed)
            //  Will be fine to do move sequence in manager device class !!!
            SplashUtils.getInstance().startSplash();
            try { Thread.sleep(1000); } catch (InterruptedException e) { /* */ }

            //  And restart if they were started
            if (selectedList==startedAttrJList) {
                for (String attribute : attributeList)
                    targetSubscriber.startAttribute(attribute);
            }
            else
            //  or pause if they were paused
            if (selectedList==pausedAttrJList) {
                for (String attribute : attributeList) {
                    //  Start before (cannot set to pause from stopped)
                    targetSubscriber.startAttribute(attribute);
                    targetSubscriber.pauseAttribute(attribute);
                }
            }

            //  Set combo box selection to src subscriber
            archiverComboBox.setSelectedItem(targetSubscriberLabel);

            //  And update lists
            manageSubscriberChanged(targetSubscriberLabel);
            SplashUtils.getInstance().stopSplash();
        }
        catch (DevFailed e) {
            SplashUtils.getInstance().stopSplash();
            ErrorPane.showErrorMessage(this, null, e);
        }
    }
    @SuppressWarnings("Convert2Diamond")
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

        javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
        attrTreePanel = new javax.swing.JPanel();
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
        javax.swing.JPanel rightPanel = new javax.swing.JPanel();
        javax.swing.JPanel archiverPanel = new javax.swing.JPanel();
        archiverLabel = new javax.swing.JLabel();
        archiverComboBox = new javax.swing.JComboBox<String>();
        tabbedPane = new javax.swing.JTabbedPane();
        javax.swing.JPanel startedPanel = new javax.swing.JPanel();
        javax.swing.JPanel startedTopPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        startedFilterText = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        startedAttrLabel = new javax.swing.JLabel();
        startedAttrScrollPane = new javax.swing.JScrollPane();
        startedAttrJList = new javax.swing.JList<String>();
        javax.swing.JPanel pausedPanel = new javax.swing.JPanel();
        javax.swing.JPanel pausedTopPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        pausedFilterText = new javax.swing.JTextField();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        pausedAttrLabel = new javax.swing.JLabel();
        pausedAttrScrollPane = new javax.swing.JScrollPane();
        pausedAttrJList = new javax.swing.JList<String>();
        javax.swing.JPanel stoppedPanel = new javax.swing.JPanel();
        javax.swing.JPanel stoppedTopPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        stoppedFilterText = new javax.swing.JTextField();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        stoppedAttrLabel = new javax.swing.JLabel();
        stoppedAttrScrollPane = new javax.swing.JScrollPane();
        stoppedAttrJList = new javax.swing.JList<String>();
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem openItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem changeCsItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitItem = new javax.swing.JMenuItem();
        javax.swing.JMenu viewMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem diagnosticsItem = new javax.swing.JMenuItem();
        javax.swing.JMenu toolMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem addSubscriberItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem removeSubscriberItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem manageAliasesItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem releaseNoteItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutItem = new javax.swing.JMenuItem();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        attrTreePanel.setMinimumSize(new java.awt.Dimension(400, 167));
        attrTreePanel.setLayout(new java.awt.BorderLayout());

        addingPanel.setLayout(new java.awt.GridBagLayout());

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titleLabel.setText("HDB++ Configurator");
        jPanel4.add(titleLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        addingPanel.add(jPanel4, gridBagConstraints);

        attributeField.setColumns(26);
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
        addAttributeButton.setToolTipText("Add selection to HDB");
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

        attrTreePanel.add(addingPanel, java.awt.BorderLayout.NORTH);

        statusPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Device Filter:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        statusPanel.add(jLabel1, gridBagConstraints);

        deviceFilterText.setColumns(20);
        deviceFilterText.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        deviceFilterText.setText("*/*/*");
        deviceFilterText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deviceFilterTextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        statusPanel.add(deviceFilterText, gridBagConstraints);

        propertiesArea.setEditable(false);
        propertiesArea.setColumns(35);
        propertiesArea.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        propertiesArea.setRows(6);
        propertiesScrollPane.setViewportView(propertiesArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        statusPanel.add(propertiesScrollPane, gridBagConstraints);

        attrTreePanel.add(statusPanel, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(attrTreePanel);

        rightPanel.setLayout(new java.awt.BorderLayout());

        archiverLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        archiverLabel.setText("Archivers:");
        archiverPanel.add(archiverLabel);

        archiverComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                archiverComboBoxActionPerformed(evt);
            }
        });
        archiverPanel.add(archiverComboBox);

        rightPanel.add(archiverPanel, java.awt.BorderLayout.NORTH);

        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        startedPanel.setLayout(new java.awt.BorderLayout());

        jLabel3.setText("Filter    tango://");
        startedTopPanel.add(jLabel3);

        startedFilterText.setColumns(25);
        startedFilterText.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        startedFilterText.setText("*/*/*/*/*");
        startedFilterText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startedFilterTextActionPerformed(evt);
            }
        });
        startedTopPanel.add(startedFilterText);

        jLabel2.setText("   ");
        startedTopPanel.add(jLabel2);

        startedAttrLabel.setText("Attributes");
        startedTopPanel.add(startedAttrLabel);

        startedPanel.add(startedTopPanel, java.awt.BorderLayout.NORTH);

        startedAttrScrollPane.setPreferredSize(new java.awt.Dimension(550, 400));

        startedAttrScrollPane.setViewportView(startedAttrJList);

        startedPanel.add(startedAttrScrollPane, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Started Attributes", startedPanel);

        pausedPanel.setLayout(new java.awt.BorderLayout());

        jLabel4.setText("Filter    tango://");
        pausedTopPanel.add(jLabel4);

        pausedFilterText.setColumns(25);
        pausedFilterText.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        pausedFilterText.setText("*/*/*/*/*");
        pausedFilterText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pausedFilterTextActionPerformed(evt);
            }
        });
        pausedTopPanel.add(pausedFilterText);

        jLabel6.setText("   ");
        pausedTopPanel.add(jLabel6);

        pausedAttrLabel.setText("Attributes");
        pausedTopPanel.add(pausedAttrLabel);

        pausedPanel.add(pausedTopPanel, java.awt.BorderLayout.NORTH);

        pausedAttrScrollPane.setPreferredSize(new java.awt.Dimension(550, 200));

        pausedAttrScrollPane.setViewportView(pausedAttrJList);

        pausedPanel.add(pausedAttrScrollPane, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Paused Attributes", pausedPanel);

        stoppedPanel.setLayout(new java.awt.BorderLayout());

        jLabel5.setText("Filter    tango://");
        stoppedTopPanel.add(jLabel5);

        stoppedFilterText.setColumns(25);
        stoppedFilterText.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        stoppedFilterText.setText("*/*/*/*/*");
        stoppedFilterText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stoppedFilterTextActionPerformed(evt);
            }
        });
        stoppedTopPanel.add(stoppedFilterText);

        jLabel7.setText("   ");
        stoppedTopPanel.add(jLabel7);

        stoppedAttrLabel.setText("Attributes");
        stoppedTopPanel.add(stoppedAttrLabel);

        stoppedPanel.add(stoppedTopPanel, java.awt.BorderLayout.NORTH);

        stoppedAttrScrollPane.setPreferredSize(new java.awt.Dimension(550, 200));

        stoppedAttrScrollPane.setViewportView(stoppedAttrJList);

        stoppedPanel.add(stoppedAttrScrollPane, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Stopped Attributes", stoppedPanel);

        rightPanel.add(tabbedPane, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(rightPanel);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        openItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openItem.setMnemonic('O');
        openItem.setText("Open Attribute List");
        openItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openItemActionPerformed(evt);
            }
        });
        fileMenu.add(openItem);

        changeCsItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        changeCsItem.setMnemonic('T');
        changeCsItem.setText("Change TANGO_HOST");
        changeCsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeCsItemActionPerformed(evt);
            }
        });
        fileMenu.add(changeCsItem);

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

        manageAliasesItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        manageAliasesItem.setText("Manage Subscriber Aliases");
        manageAliasesItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageAliasesItemActionPerformed(evt);
            }
        });
        toolMenu.add(manageAliasesItem);

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
                "\nIt manages " + subscriberMap.size() + " event subscriber devices\n" +
                "\nPascal Verdier - ESRF - Software Group";
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
            Subscriber subscriber = subscriberMap.getSubscriberByLabel(archiverLabel);
            startedFilterText.setText(subscriber.getStartedFilter());
            stoppedFilterText.setText(subscriber.getStoppedFilter());
            pausedFilterText.setText(subscriber.getPausedFilter());

            updateAttributeList(subscriberMap.getSubscriberByLabel(archiverLabel));
        }
        catch (DevFailed e) {
            String[]    filtered = new String[0];
            startedAttrJList.setListData(filtered);
            stoppedAttrJList.setListData(filtered);
            pausedAttrJList.setListData(filtered);
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

        List<String>   attributeNames = attributeTree.getSelectedAttributes();
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
        if (subscriberMap.size()==0) {
            Utils.popupError(this, "No Subscriber defined");
            return;
        }
        if (fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file!=null) {
                if (file.isFile()) {
                    try {
                        String	filename = file.getAbsolutePath();
                        List<String>   attributeNames = Utils.readFileLines(filename);
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
            Subscriber subscriber = subscriberMap.getSubscriberByLabel(
                    (String) archiverComboBox.getSelectedItem());
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
    private void pausedFilterTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pausedFilterTextActionPerformed
        try {
            Subscriber subscriber = subscriberMap.getSubscriberByLabel(
                    (String) archiverComboBox.getSelectedItem());
            subscriber.setPausedFilter(pausedFilterText.getText());
            updateAttributeList(subscriber);
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
    }//GEN-LAST:event_pausedFilterTextActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void startedFilterTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startedFilterTextActionPerformed
        try {
            Subscriber subscriber = subscriberMap.getSubscriberByLabel(
                    (String) archiverComboBox.getSelectedItem());
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
            if (subscriberMap.size()==0) {
                Utils.popupError(this, "No Subscriber to remove");
                return;
            }

            if (new CreateSubscriberPanel(this,  configuratorProxy,
                    CreateSubscriberPanel.REMOVE).showDialog()==JOptionPane.OK_OPTION) {
                restartApplication();
            }
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, null, e);
        }
    }//GEN-LAST:event_removeSubscriberItemActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void changeCsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeCsItemActionPerformed
        changeTangoHost(attributeTree.getTangoHost());
    }//GEN-LAST:event_changeCsItemActionPerformed

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        // Check if already initialized
        if (subscriberMap==null)
            return;
        try {
            Subscriber subscriber = subscriberMap.getSubscriberByLabel(
                    (String) archiverComboBox.getSelectedItem());
            updateAttributeList(subscriber);
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, e.getMessage(), e);
        }
    }//GEN-LAST:event_tabbedPaneStateChanged

    //=======================================================
    //=======================================================
    @SuppressWarnings("UnusedParameters")
    private void manageAliasesItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageAliasesItemActionPerformed
        try {
            ArchiverAliasesDialog aliasesDialog = new ArchiverAliasesDialog(this, subscriberMap);
            if (aliasesDialog.showDialog()==JOptionPane.OK_OPTION) {
                restartApplication();
            }
        }
        catch (DevFailed e) {
            ErrorPane.showErrorMessage(this, e.getMessage(), e);
        }
    }//GEN-LAST:event_manageAliasesItemActionPerformed

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
    private void addAttributeList(List<String> attributeNames) {
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
            Subscriber  subscriber = subscriberMap.getSubscriberByLabel(archiverName);
            boolean pushedByCode = propertyDialog.isPushedByCode();
            boolean startArchiving = propertyDialog.startArchiving();

            //  Build Hdb Attribute objects
            ArrayList<HdbAttribute> attributes = new ArrayList<>();
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
            if(!selectAttributeInList(attributeName, stoppedAttrJList))
                selectAttributeInList(attributeName, pausedAttrJList);
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
            Subscriber subscriber  = subscriberMap.getSubscriberByLabel(propertyDialog.getSubscriber());

            //  If OK add the attribute
            ArchiverUtils.addAttribute(configuratorProxy,
                    subscriber.getName(), attributeName, pushedByCode, true/*Lock*/);
            if (startArchiving)
                subscriber.startAttribute(attributeName);

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
        updateListThread.stopIt = true;
        if (diagnosticsPanel!=null && diagnosticsPanel.isVisible()) {
            setVisible(false);
            dispose();
        }
        else {
            try { updateListThread.join(); }
            catch (InterruptedException e) { System.err.println(e.getMessage()); }
            System.exit(0);
        }
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
            SplashUtils.getInstance().stopSplash();
            ErrorPane.showErrorMessage(new Frame(), null, e);
            System.exit(0);
		}
		catch(Exception e) {
            SplashUtils.getInstance().stopSplash();
            e.printStackTrace();
            ErrorPane.showErrorMessage(new Frame(), null, e);
            System.exit(0);
		}
		catch(Error e) {
            SplashUtils.getInstance().stopSplash();
            e.printStackTrace();
            ErrorPane.showErrorMessage(new Frame(), null, new Exception(e));
            System.exit(0);
		}
    }


	//=======================================================
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAttributeButton;
    private javax.swing.JComboBox<String> archiverComboBox;
    private javax.swing.JLabel archiverLabel;
    private javax.swing.JPanel attrTreePanel;
    private javax.swing.JTextField attributeField;
    private javax.swing.JTextField deviceFilterText;
    private javax.swing.JList<String> pausedAttrJList;
    private javax.swing.JLabel pausedAttrLabel;
    private javax.swing.JScrollPane pausedAttrScrollPane;
    private javax.swing.JTextField pausedFilterText;
    private javax.swing.JTextArea propertiesArea;
    private javax.swing.JButton searchButton;
    private javax.swing.JList<String> startedAttrJList;
    private javax.swing.JLabel startedAttrLabel;
    private javax.swing.JScrollPane startedAttrScrollPane;
    private javax.swing.JTextField startedFilterText;
    private javax.swing.JList<String> stoppedAttrJList;
    private javax.swing.JLabel stoppedAttrLabel;
    private javax.swing.JScrollPane stoppedAttrScrollPane;
    private javax.swing.JTextField stoppedFilterText;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
	//=======================================================


/*
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

 */





    //======================================================
    /**
     * Popup menu class
     */
    //======================================================
    private static final int ARCHIVING_STRATEGY  = 0;
    private static final int START_ARCHIVING  = 1;
    private static final int STOP_ARCHIVING   = 2;
    private static final int PAUSE_ARCHIVING  = 3;
    private static final int REMOVE_ATTRIBUTE = 4;
    private static final int MOVE_TO = 5;
    private static final int COPY_AS_TEXT = 6;

    private static final int OFFSET = 2;    //	Label And separator

    private static String[] menuLabels = {
            "Change Archiving Strategy",
            "Start Archiving", "Stop Archiving", "Pause Archiving",
            "Remove Attribute", "Move Attribute To ", "Copy as Text",
    };
    //=======================================================
    //=======================================================
    private class ListPopupMenu extends JPopupMenu {
        private JLabel title;
        private JList<String>  selectedList;
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

            List<String> subscriberList = subscriberMap.getLabelList();
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
        //noinspection PointlessArithmeticExpression
        private void showMenu(MouseEvent event, String attributeName) {
            title.setText(attributeName);
            setSubscriberMenu();

            Object object = event.getSource();
            if (object==startedAttrJList) {
                selectedList = startedAttrJList;
                getComponent(OFFSET + START_ARCHIVING).setVisible(false);
                getComponent(OFFSET + STOP_ARCHIVING).setVisible(true);
                getComponent(OFFSET + PAUSE_ARCHIVING).setVisible(true);
            }
            else
            if (object==stoppedAttrJList) {
                selectedList = stoppedAttrJList;
                getComponent(OFFSET + START_ARCHIVING).setVisible(true);
                getComponent(OFFSET + STOP_ARCHIVING).setVisible(false);
                getComponent(OFFSET + PAUSE_ARCHIVING).setVisible(false);
            }
            else
            if (object==pausedAttrJList) {
                selectedList = pausedAttrJList;
                getComponent(OFFSET + START_ARCHIVING).setVisible(true);
                getComponent(OFFSET + STOP_ARCHIVING).setVisible(true);
                getComponent(OFFSET + PAUSE_ARCHIVING).setVisible(false);
            }
            getComponent(OFFSET + ARCHIVING_STRATEGY).setVisible(PropertyDialog.useStrategy);
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
                        subscriberMap.getSubscriberByLabel((String) archiverComboBox.getSelectedItem());
                List<String> attributeList = selectedList.getSelectedValuesList();
                switch (itemIndex) {
                    case ARCHIVING_STRATEGY:
                        changeArchivingStrategy(attributeList);
                        break;
                    case START_ARCHIVING:
                        ManageAttributes.startAttributes(attributeList);
                        updateAttributeList(archiver);
                        break;
                    case STOP_ARCHIVING:
                        ManageAttributes.stopAttributes(attributeList);
                        updateAttributeList(archiver);
                        break;
                    case PAUSE_ARCHIVING:
                        ManageAttributes.pauseAttributes(attributeList);
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
            List<String> attributeNames = selectedList.getSelectedValuesList();
            StringBuilder   sb = new StringBuilder();
            for (String attributeName : attributeNames) {
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



    //=======================================================
    /**
     * A thread to update Started/Stopped/Paused attribute lists
     * on selected subscriber if they have changed
     */
    //=======================================================
    private class UpdateListThread extends Thread {
        private String[] displayedList = null;
        private boolean stopIt = false;
        //===================================================
        public void run() {
            while (!stopIt) {
                try { sleep(1000); } catch (InterruptedException e) { /* */ }

                //  Get selected subscriber
                String  archiverLabel = (String) archiverComboBox.getSelectedItem();
                if (archiverLabel!=null) {
                    try {
                        Subscriber subscriber = subscriberMap.getSubscriberByLabel(archiverLabel);

                        //  Get displayed list
                        int selection = tabbedPane.getSelectedIndex();
                        //  Get attribute list and check if has changed
                        String[] attributeList = subscriber.getAttributeList(selection, false);
                        if (attributeList!=displayedList) {
                            displayedList = attributeList;
                            updateAttributeList(subscriber);
                        }
                    }
                    catch (DevFailed e) {
                        System.err.println(e.errors[0].desc);
                    }
                }
            }
        }
    }
    //=======================================================
    //=======================================================
}
