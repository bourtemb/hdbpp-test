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

package org.tango.hdbcpp.diagnostics;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoDs.Except;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import jive3.MainPanel;
import org.tango.hdbcpp.common.Subscriber;
import org.tango.hdbcpp.common.SubscriberMap;
import org.tango.hdbcpp.tools.SplashUtils;
import org.tango.hdbcpp.tools.TangoUtils;
import org.tango.hdbcpp.tools.Utils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;


//===============================================================
/**
 *	JDialog Class to display info
 *
 *	@author  Pascal Verdier
 */
//===============================================================


@SuppressWarnings("MagicConstant")
public class StatisticsDialog extends JDialog {

    private ArrayList<HdbAttribute> filteredHdbAttributes = new ArrayList<HdbAttribute>();
    private ArrayList<HdbAttribute> hdbAttributes = new ArrayList<HdbAttribute>();
    private JTable table;
    private DataTableModel model;
    private TablePopupMenu popupMenu = new TablePopupMenu();
    private MainPanel jive = null;
    private static List<String> defaultTangoHosts;

    private static final int columnWidth[] = { 200, 40, 60 };
    private static final  String[] columnNames = {
            "Attribute Names", "Events", "Av.Period." };

    private static final Color selectionBackground   = new Color(0xe0e0ff);
    private static final Color firstColumnBackground = new Color(0xe0e0e0);
	//===============================================================
	/**
	 *	Creates new form StatisticsDialog for several subscribers
	 */
	//===============================================================
    public StatisticsDialog(JFrame parent, SubscriberMap subscriberMap) throws DevFailed {
        super(parent, false);
        SplashUtils.startSplash();
        try {
            defaultTangoHosts = TangoUtils.getDefaultTangoHostList();
            initComponents();
            List<Subscriber> subscribers = subscriberMap.getSubscriberList();

            buildRecords(subscribers);
            buildTableComponent();
            setTitle("All Subscribers ");
            displayTitle();

            pack();
            ATKGraphicsUtils.centerDialog(this);
        }
        catch (DevFailed e) {
            SplashUtils.stopSplash();
            throw e;
        }
        SplashUtils.stopSplash();
    }
	//===============================================================
	/**
	 *	Creates new form StatisticsDialog for one subscriber
	 */
	//===============================================================
	public StatisticsDialog(JFrame parent, Subscriber subscriber) throws DevFailed {
		super(parent, false);
        SplashUtils.startSplash();
        try {
            defaultTangoHosts = TangoUtils.getDefaultTangoHostList();
            initComponents();
            ArrayList<Subscriber>   subscribers = new ArrayList<Subscriber>(1);
            subscribers.add(subscriber);
            buildRecords(subscribers);
            buildTableComponent();
            setTitle("Subscriber " + subscriber.getLabel());
            displayTitle();

            pack();
            ATKGraphicsUtils.centerDialog(this);
        }
        catch (DevFailed e) {
            SplashUtils.stopSplash();
            throw e;
        }
        SplashUtils.stopSplash();
	}
    //===============================================================
    //===============================================================
    private void displayTitle() {
        String  title = "Statistics  -  ";
        if (filteredHdbAttributes.size()!=hdbAttributes.size()) {
            title += Integer.toString(filteredHdbAttributes.size()) + " filtered / ";
        }
        title += Integer.toString(hdbAttributes.size()) + " Attributes";

        titleLabel.setText(title);
    }
    //===============================================================
    //===============================================================
    private void buildRecords(List<Subscriber> subscribers) throws DevFailed {
        String[] statAttributeNames = {
                "AttributeList", "AttributeRecordFreqList", "AttributeEventNumberList" };

        for (Subscriber subscriber : subscribers) {
            //  Read statistic attributes
            SplashUtils.increaseSplashProgressForLoop(
                    subscribers.size(), "Reading " + subscriber.getLabel());
            DeviceAttribute[] deviceAttributes = subscriber.read_attribute(statAttributeNames);

            //  Extract values
            int i=0;
            String[] hdbAttributeNames = new String[0];
            if (!deviceAttributes[i].hasFailed())
                hdbAttributeNames = deviceAttributes[i].extractStringArray();
            i++;
            double[] frequencies = new double[0];
            if (!deviceAttributes[i].hasFailed())
                frequencies = deviceAttributes[i].extractDoubleArray();
            i++;
            int[] eventNumbers = new int[0];
            if (!deviceAttributes[i].hasFailed())
                eventNumbers = deviceAttributes[i].extractLongArray();

            //  Duration is passed to have just one DB read per subscriber
            int duration = subscriber.getStatisticsTimeWindow();
            //System.out.println(subscriber.getLabel()+":\t"+duration);

            //  Build filteredHdbAttributes and store in a list
            for (int x=0 ; x<hdbAttributeNames.length &&
                           x<frequencies.length && x<eventNumbers.length ; x++) {
                hdbAttributes.add(new HdbAttribute(hdbAttributeNames[x],
                        frequencies[x], eventNumbers[x], subscriber, duration));
             }
        }
        Collections.sort(hdbAttributes, new AttributeComparator());

        //  Copy to filtered (no filter at start up)
        for (HdbAttribute hdbAttribute : hdbAttributes) {
            filteredHdbAttributes.add(hdbAttribute);
        }
    }
    //===============================================================
    //===============================================================
    private void buildTableComponent() throws DevFailed {
        try {
            model = new DataTableModel();

            // Create the table
            table = new JTable(model);
            table.setRowSelectionAllowed(true);
            table.setColumnSelectionAllowed(true);
            table.setDragEnabled(false);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getTableHeader().setFont(new java.awt.Font("Dialog", Font.BOLD, 12));
            table.setDefaultRenderer(String.class, new LabelCellRenderer());
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    tableActionPerformed(evt);
                }
            });

            //	Put it in scrolled pane
            JScrollPane scrollPane = new JScrollPane(table);
            model.fireTableDataChanged();
            getContentPane().add(scrollPane, BorderLayout.CENTER);

            //  Set column width
            final Enumeration columnEnum = table.getColumnModel().getColumns();
            int i = 0;
            TableColumn tableColumn;
            while (columnEnum.hasMoreElements()) {
                tableColumn = (TableColumn) columnEnum.nextElement();
                tableColumn.setPreferredWidth(columnWidth[i++]);
            }

            //  Compute size to display
            pack();
            int width  = table.getWidth();
            int height = table.getHeight();
            if (height>800) height = 800;
            scrollPane.setPreferredSize(new Dimension(width, height+20));
        }
        catch (Exception e) {
            e.printStackTrace();
            Except.throw_exception("INIT_ERROR", e.toString());
        }
    }
    //===============================================================
    //===============================================================
    private int selectedRow = -1;
    private void tableActionPerformed(java.awt.event.MouseEvent event) {

        //	get selected signal
        Point clickedPoint = new Point(event.getX(), event.getY());
        int row = table.rowAtPoint(clickedPoint);
        selectedRow = row;
        table.repaint();

        if (event.getClickCount() == 2) {
            JOptionPane.showMessageDialog(this, filteredHdbAttributes.get(row).getInfo());
        }
        else {
            int mask = event.getModifiers();

            //  Check button clicked
            if ((mask & MouseEvent.BUTTON3_MASK) != 0) {
                popupMenu.showMenu(event, filteredHdbAttributes.get(row));
            }
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

        javax.swing.JPanel topPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        filterTextField = new javax.swing.JTextField();
        javax.swing.JButton applyButton = new javax.swing.JButton();
        javax.swing.JMenuBar jMenuBar1 = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem dismissItem = new javax.swing.JMenuItem();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        titleLabel.setText("Dialog Title");
        topPanel.add(titleLabel);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel1.setText("Filter:   ");
        bottomPanel.add(jLabel1);

        filterTextField.setColumns(25);
        filterTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterTextFieldActionPerformed(evt);
            }
        });
        bottomPanel.add(filterTextField);

        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(applyButton);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        fileMenu.setText("File");

        dismissItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        dismissItem.setText("Dismiss");
        dismissItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dismissItemActionPerformed(evt);
            }
        });
        fileMenu.add(dismissItem);

        jMenuBar1.add(fileMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //===============================================================
	//===============================================================
    @SuppressWarnings("UnusedParameters")
    private void dismissItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dismissItemActionPerformed
        doClose();
    }//GEN-LAST:event_dismissItemActionPerformed
    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
    }//GEN-LAST:event_closeDialog

    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        applyFilter();
    }//GEN-LAST:event_applyButtonActionPerformed

    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void filterTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterTextFieldActionPerformed
        applyFilter();
    }//GEN-LAST:event_filterTextFieldActionPerformed

	//===============================================================
	//===============================================================
    private void applyFilter() {
        String  filter = filterTextField.getText();
        filteredHdbAttributes = new ArrayList<HdbAttribute>();
        for (HdbAttribute hdbAttribute : hdbAttributes) {
            if (hdbAttribute.shortName.contains(filter)) {
                filteredHdbAttributes.add(hdbAttribute);
            }
        }
        model.fireTableDataChanged();
        displayTitle();
    }
	//===============================================================
	/**
	 *	Closes the dialog
	 */
	//===============================================================
	private void doClose() {
        setVisible(false);
        dispose();
	}
    //===============================================================
    //===============================================================

    //===============================================================
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField filterTextField;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
	//===============================================================




    //===============================================================
    /**
     * Attribute read from HDB object.
     */
    //===============================================================
    private class HdbAttribute {
        private String name;
        private String shortName;
        private double nbStatistics;
        private int nbEvents;
        private int duration;
        private String averagePeriodString;
        private double averagePeriod = -1;
        private Subscriber subscriber;
        private String deviceName;
        private String tangoHost;
        private boolean useDefaultTangoHost = false;
        //===========================================================
        private HdbAttribute(String name, double frequency, int nbEvents, Subscriber subscriber, int duration) {
            this.name = name;
            this.shortName = TangoUtils.getOnlyDeviceName(name);
            this.nbStatistics = frequency;
            this.nbEvents  = nbEvents;
            this.subscriber = subscriber;
            this.duration = duration;
            if (frequency==0.0)
                averagePeriodString = "---";
            else {
                averagePeriod = (double)duration/frequency;
                averagePeriodString = Utils.strPeriod(averagePeriod);
            }

            deviceName = shortName.substring(0, shortName.lastIndexOf('/'));
            tangoHost = TangoUtils.getOnlyTangoHost(name);
            for (String defaultTangoHost : defaultTangoHosts) {
                if (tangoHost.equals(defaultTangoHost))
                    useDefaultTangoHost = true;
            }
        }
        //===========================================================
        private String getInfo() {
            return name + ":\n\n" + "Archived by " + subscriber.getLabel() +
                    "    (" + subscriber.getName() + "\n" +
                    nbStatistics + " events during " + Utils.strPeriod(duration);
        }
        //===========================================================
        private void configureEvent() {

            //  Start jive and go to the device node
            if (jive==null) {
                jive = new MainPanel(false, false);
            }
            jive.setVisible(true);
            jive.goToDeviceNode(deviceName);
            System.out.println("Go to device node "+deviceName);
        }
        //===========================================================
        public String toString() {
            return shortName + ":\t" + nbEvents;
        }
        //===========================================================
    }
    //===============================================================
    //===============================================================


    //=========================================================================
    /**
     * The Table model
     */
    //=========================================================================
    public class DataTableModel extends AbstractTableModel {
        //==========================================================
        public int getColumnCount() {
            return columnNames.length;
        }

        //==========================================================
        public int getRowCount() {
            return filteredHdbAttributes.size();
        }

        //==========================================================
        public String getColumnName(int columnIndex) {
            String title;
            if (columnIndex >= getColumnCount())
                title = columnNames[getColumnCount()-1];
            else
                title = columnNames[columnIndex];

            // remove tango host if any
            if (title.startsWith("tango://")) {
                int index = title.indexOf('/', "tango://".length());
                title = title.substring(index+1);
            }

            return title;
        }

        //==========================================================
        public Object getValueAt(int row, int column) {
            //  Value to display is returned by
            // LabelCellRenderer.getTableCellRendererComponent()
            return "";
        }
        //==========================================================
        /**
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         *
         * @param  column the specified co;umn number
         * @return the cell class at first row for specified column.
         */
        //==========================================================
        public Class getColumnClass(int column) {
            if (isVisible())
                return getValueAt(0, column).getClass();
            else
                return null;
        }
        //==========================================================
        //==========================================================
    }
    //=========================================================================
    //=========================================================================



    //=========================================================================
    /**
     * Renderer to set cell color
     */
    //=========================================================================
    public class LabelCellRenderer extends JLabel implements TableCellRenderer {

        //==========================================================
        public LabelCellRenderer() {
            //setFont(new Font("Dialog", Font.BOLD, 11));
            setOpaque(true); //MUST do this for background to show up.
        }

        //==========================================================
        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            setBackground(getBackground(row, column));
            switch (column) {
                case 0:
                    setText(filteredHdbAttributes.get(row).shortName);
                    break;
                case 1:
                    setText(Double.toString(filteredHdbAttributes.get(row).nbStatistics));
                    //setText(Double.toString(filteredHdbAttributes.get(row).nbEvents));
                    break;
                case 2:
                    setText(filteredHdbAttributes.get(row).averagePeriodString);
                    break;
            }
            return this;
        }
        //==========================================================
        private Color getBackground(int row, int column) {
            switch (column) {
                case 0:
                    return firstColumnBackground;

                default:
                    if (selectedRow>=0) {
                        if (row==selectedRow)
                            return selectionBackground;
                    }
                    return Color.white;
            }
        }
        //==========================================================
    }
    //=========================================================================
    //=========================================================================


    //======================================================
    /**
     * Popup menu class
     */
    //======================================================
    private static final int STATUS  = 0;
    private static final int CONFIGURE   = 1;
    private static final int OFFSET = 2;    //	Label And separator

    private static String[] menuLabels = { "Status", "Configure Polling/Events" };
    //=======================================================
    //=======================================================
    private class TablePopupMenu extends JPopupMenu {
        private JLabel title;
        private HdbAttribute selectedAttribute;
        //======================================================
        private TablePopupMenu() {
            title = new JLabel();
            title.setFont(new java.awt.Font("Dialog", Font.BOLD, 12));
            add(title);
            add(new JPopupMenu.Separator());

            for (String menuLabel : menuLabels) {
                if (menuLabel == null)
                    add(new Separator());
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
        //======================================================
        private void showMenu(MouseEvent event, HdbAttribute hdbAttribute) {
            title.setText(hdbAttribute.shortName);
            selectedAttribute = hdbAttribute;

            getComponent(OFFSET + CONFIGURE).setEnabled(hdbAttribute.useDefaultTangoHost);
            show(table, event.getX(), event.getY());
        }
        //======================================================
        private void menuActionPerformed(ActionEvent evt) {
            //	Check component source
            Object obj = evt.getSource();
            int itemIndex = -1;
            for (int i=0; i<menuLabels.length; i++)
                if (getComponent(OFFSET + i) == obj)
                    itemIndex = i;
            switch (itemIndex){
                case STATUS:
                    JOptionPane.showMessageDialog(this, selectedAttribute.getInfo());
                    break;
                case CONFIGURE:
                    selectedAttribute.configureEvent();
                    break;
            }
        }
    }



    //=========================================================================
    /**
     * Comparator to sort attribute list
     */
    //=========================================================================
    private class AttributeComparator implements Comparator<HdbAttribute> {

        //======================================================
        public int compare(HdbAttribute hdbAttribute1, HdbAttribute hdbAttribute2) {
            if (hdbAttribute1.averagePeriod==hdbAttribute2.averagePeriod)
                return 0;
            else
            if (hdbAttribute1.averagePeriod<0)
                return 1;
            else
            if (hdbAttribute2.averagePeriod<0)
                return -1;
            else
                return ((hdbAttribute1.averagePeriod > hdbAttribute2.averagePeriod)? 1 : -1);
        }
        //======================================================
    }
    //===============================================================
    //===============================================================
}
