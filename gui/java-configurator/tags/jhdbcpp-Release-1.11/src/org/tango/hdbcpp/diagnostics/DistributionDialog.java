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
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.ErrorPane;
import fr.esrf.tangoatk.widget.util.chart.*;
import org.tango.hdbcpp.common.Subscriber;
import org.tango.hdbcpp.common.SubscriberMap;
import org.tango.hdbcpp.tools.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;


//===============================================================
/**
 *	JDialog Class to display attributes distribution as a bar chart
 *
 *	@author  Pascal Verdier
 */
//===============================================================


@SuppressWarnings("MagicConstant")
public class DistributionDialog extends JDialog {

	private JFrame	parent;
	private JDialog	thisDialog;
    private SubscriberMap subscriberMap;
    private DistributionChart distributionChart;
    private ArrayList<Archiver> archivers = new ArrayList<Archiver>();
    private JTable table;
    private int selectedColumn = SUBSCRIBER_NAME;

    private static final int SUBSCRIBER_NAME  = 0;
    private static final int ATTRIBUTE_NUMBER = 1;
    private static final int EVENT_NUMBER     = 2;
    private static final int RESET_TIME       = 3;
    private static final int RESET_DURATION   = 4;
    private static final String[] columnNames = {
            "Subscriber", "Attributes", "Nb Events", "Reset Time", "Duration"
    };
    private static final int[] columnWidth = { 300, 60, 60, 120, 120 };
	//===============================================================
	/**
	 *	Creates new form DistributionDialog
	 */
	//===============================================================
	public DistributionDialog(JFrame parent, SubscriberMap subscriberMap) throws DevFailed {
		super(parent, true);
		this.parent = parent;
		thisDialog  = this;
        SplashUtils.getInstance().startSplash();
        initComponents();

        try {
            if (subscriberMap==null) {
                String  configuratorDeviceName = TangoUtils.getConfiguratorDeviceName();
                subscriberMap = new SubscriberMap(new DeviceProxy(configuratorDeviceName));
            }
            this.subscriberMap = subscriberMap;
            distributionChart = new DistributionChart();
            chartPanel.add(distributionChart, BorderLayout.CENTER);
            buildTable();

            titleLabel.setText(distributionChart.attributeCount + " Attributes   distributed   in " +
                    subscriberMap.getLabelList().size() + " Subscribers");

            pack();
            ATKGraphicsUtils.centerDialog(this);
            SplashUtils.getInstance().stopSplash();
        }
        catch (DevFailed e) {
            SplashUtils.getInstance().stopSplash();
            throw e;
        }
	}

	//===============================================================
	//===============================================================
    private void buildTable() {

        DataTableModel model = new DataTableModel();

        // Create the table
        //noinspection NullableProblems
        table = new JTable(model) {
            //	Implements table cell tool tip
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                if (isVisible()) {
                    Point p = e.getPoint();
                    int column = columnAtPoint(p);
                    int row = rowAtPoint(p);
                    if (column==SUBSCRIBER_NAME) {
                        Archiver archiver = archivers.get(row);
                        String text = "<b>"+archiver.subscriber.getLabel() + "</b><ul>( " +
                                archiver.subscriber.name() +" )</ul>";
                        tip = Utils.buildTooltip(text);
                    }
                }
                return tip;
            }
        };
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setDragEnabled(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new java.awt.Font("Dialog", Font.BOLD, 12));
        table.setDefaultRenderer(String.class, new LabelCellRenderer());
        /*
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableActionPerformed(evt);
            }
        });
        */
        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableHeaderActionPerformed(evt);
            }
        });
        //	Put it in scrolled pane
        JScrollPane scrollPane = new JScrollPane(table);
        model.fireTableDataChanged();
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        //  Set column width
        final Enumeration columnEnum = table.getColumnModel().getColumns();
        int i = 0;
        TableColumn tableColumn;
        while (columnEnum.hasMoreElements()) {
            tableColumn = (TableColumn) columnEnum.nextElement();
            tableColumn.setPreferredWidth(columnWidth[i++]);
        }
        Collections.sort(archivers, new ArchiverComparator());
    }
    //===============================================================
    //===============================================================
    private void tableHeaderActionPerformed(java.awt.event.MouseEvent event) {
        //	Get specified column
        selectedColumn = table.getTableHeader().columnAtPoint(new Point(event.getX(), event.getY()));
        Collections.sort(archivers, new ArchiverComparator());
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
        javax.swing.JButton cancelBtn = new javax.swing.JButton();
        javax.swing.JTabbedPane jTabbedPane1 = new javax.swing.JTabbedPane();
        chartPanel = new javax.swing.JPanel();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JRadioButton notOkButton = new javax.swing.JRadioButton();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JRadioButton OkButton = new javax.swing.JRadioButton();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JRadioButton eventsButton = new javax.swing.JRadioButton();
        tablePanel = new javax.swing.JPanel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        titleLabel.setText("Attributes Distribution");
        topPanel.add(titleLabel);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        cancelBtn.setText("Dismiss");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });
        bottomPanel.add(cancelBtn);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        chartPanel.setLayout(new java.awt.BorderLayout());

        notOkButton.setSelected(true);
        notOkButton.setText("Attributes Not OK");
        notOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notOkButtonActionPerformed(evt);
            }
        });
        jPanel1.add(notOkButton);

        jLabel1.setText("          ");
        jPanel1.add(jLabel1);

        OkButton.setSelected(true);
        OkButton.setText("Attributes OK");
        OkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkButtonActionPerformed(evt);
            }
        });
        jPanel1.add(OkButton);

        jLabel2.setText("          ");
        jPanel1.add(jLabel2);

        eventsButton.setSelected(true);
        eventsButton.setText("Events Received");
        eventsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventsButtonActionPerformed(evt);
            }
        });
        jPanel1.add(eventsButton);

        chartPanel.add(jPanel1, java.awt.BorderLayout.NORTH);

        jTabbedPane1.addTab("Chart", chartPanel);

        tablePanel.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Table", tablePanel);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	//===============================================================
	//===============================================================
	@SuppressWarnings("UnusedParameters")
    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
		doClose();
	}//GEN-LAST:event_cancelBtnActionPerformed

	//===============================================================
	//===============================================================
    @SuppressWarnings("UnusedParameters")
	private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
		doClose();
	}//GEN-LAST:event_closeDialog

    //===============================================================
    //===============================================================
    private void notOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notOkButtonActionPerformed
        JRadioButton btn = (JRadioButton) evt.getSource();
        distributionChart.setVisibleCurve(btn.isSelected(),  failedDataView);
    }//GEN-LAST:event_notOkButtonActionPerformed

    //===============================================================
    //===============================================================
    private void OkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButtonActionPerformed
        JRadioButton btn = (JRadioButton) evt.getSource();
        distributionChart.setVisibleCurve(btn.isSelected(),  okDataView);
    }//GEN-LAST:event_OkButtonActionPerformed

    //===============================================================
    //===============================================================
    private void eventsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventsButtonActionPerformed
        JRadioButton btn = (JRadioButton) evt.getSource();
        distributionChart.setVisibleCurve(btn.isSelected(),  eventDataView);
    }//GEN-LAST:event_eventsButtonActionPerformed

    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
	//===============================================================
	/**
	 *	Closes the dialog
	 */
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPanel;
    private javax.swing.JPanel tablePanel;
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
			new DistributionDialog(null, null).setVisible(true);
		}
		catch(DevFailed e) {
            ErrorPane.showErrorMessage(new Frame(), null, e);
			System.exit(0);
		}
	}
    //===============================================================
    //===============================================================




    private JLDataView failedDataView;
    private JLDataView okDataView;
    private JLDataView eventDataView;
    //===============================================================
    /**
     * JLChart class to display distribution
     */
    //===============================================================
    private class DistributionChart extends JLChart implements IJLChartListener {
        private Archiver selectedArchiver = null;
        private int attributeCount;

        private final String[] labels = {
                "========================",
                "Update from archivers",
        };
        private static final int SEPARATOR = 0;
        private static final int UPDATE_DATA = 1;
        //===============================================================
        private DistributionChart() throws DevFailed {

            setJLChartListener(this);
            buildAxises();
            failedDataView = buildCurve("Attributes Not OK", Color.red);
            okDataView = buildCurve("Attributes OK", new Color(0x00aa00));
            eventDataView = buildCurve("Events Received", new Color(0x0000aa));
            getY1Axis().addDataView(failedDataView);
            getY1Axis().addDataView(okDataView);
            getY2Axis().addDataView(eventDataView);

            updateValues();
            setPreferredSize(new Dimension(800, 650));
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent event) {
                    chartMouseClicked(event);
                }
            });

            //  Add JMenuItem to popup menu
            this.addMenuItem(new JMenuItem(labels[SEPARATOR]));
            JMenuItem updateItem = new JMenuItem(labels[UPDATE_DATA]);
            updateItem.setSelected(true);
            updateItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    menuActionPerformed(evt);
                }
            });
            this.addMenuItem(updateItem);
        }
        //===============================================================
        private void setVisibleCurve(boolean b, JLDataView dataView) {
            JLAxis axis = dataView.getAxis();
            if (!b)
                axis.removeDataView(dataView);
            else {
                if (dataView==eventDataView)
                    getY2Axis().addDataView(dataView);
                else
                    getY1Axis().addDataView(dataView);
            }
            repaint();
        }
        //===============================================================
        private void updateValues() throws DevFailed {
            resetDataViews();
            archivers.clear();
            ArrayList<String> labels = subscriberMap.getLabelList();
            SplashUtils.getInstance().reset();
            attributeCount = 0;
            int x = 0;
            for (String label : labels) {
                SplashUtils.getInstance().increaseSplashProgressForLoop(labels.size(), "Reading " + label);
                Archiver archiver = new Archiver(subscriberMap.getSubscriber(label));

                okDataView.add(x, archiver.attributeOk.length);
                failedDataView.add(x + 0.2, archiver.attributeFailed.length);
                eventDataView.add(x+0.4, archiver.totalEvents);
                attributeCount += archiver.attributeCount();
                archivers.add(archiver);
                x++;
            }
        }
        //===============================================================
        private void menuActionPerformed(ActionEvent evt) {
            String cmd = evt.getActionCommand();
            if (cmd.equals(labels[UPDATE_DATA])) {
                try {
                    SplashUtils.getInstance().startSplash();
                    String  configuratorDeviceName = TangoUtils.getConfiguratorDeviceName();
                    subscriberMap = new SubscriberMap(new DeviceProxy(configuratorDeviceName));
                    distributionChart.updateValues();
                    SplashUtils.getInstance().stopSplash();
                }
                catch (DevFailed e) {
                    SplashUtils.getInstance().stopSplash();
                    ErrorPane.showErrorMessage(this, null, e);
                }
            }
            repaint();
        }
        //===============================================================
        private void resetDataViews() {
            okDataView.reset();
            failedDataView.reset();
            eventDataView.reset();
        }
        //===============================================================
        private void buildAxises() {
            //  Create X axis.
            JLAxis  xAxis = getXAxis();
            xAxis.setName("Archivers");
            xAxis.setAnnotation(JLAxis.VALUE_ANNO);
            xAxis.setGridVisible(true);
            xAxis.setSubGridVisible(true);

            JLAxis  y1Axis = getY1Axis();
            y1Axis.setName("Attributes");
            y1Axis.setAutoScale(true);
            y1Axis.setScale(JLAxis.LINEAR_SCALE);
            y1Axis.setGridVisible(true);
            y1Axis.setSubGridVisible(true);

            JLAxis  y2Axis = getY2Axis();
            y2Axis.setName("Events Number");
            y2Axis.setAutoScale(true);
            y2Axis.setScale(JLAxis.LINEAR_SCALE);
            y2Axis.setGridVisible(true);
            y2Axis.setSubGridVisible(true);
        }
        //===============================================================
        private JLDataView buildCurve(String name, Color color) {
            JLDataView  dataView = new JLDataView();
            dataView.setColor(Color.blue);
            dataView.setFillColor(color);
            dataView.setName(name);
            dataView.setFill(false);
            dataView.setLabelVisible(true);
            dataView.setViewType(JLDataView.TYPE_BAR);
            dataView.setBarWidth(6);
            dataView.setFillStyle(JLDataView.FILL_STYLE_SOLID);

            return dataView;
        }
        //===============================================================
        /**
         *  Called after implementation of clickOnChart method
         *  to add another feature (display attributes on error)
         */
        //===============================================================
        private void chartMouseClicked(MouseEvent event) {
            int mask = event.getModifiers();
            if (event.getClickCount()==2 && (mask & MouseEvent.BUTTON1_MASK)!=0) {
                if (selectedArchiver!=null) {
                    try {
                        String[] attributeList =
                                ArchiverUtils.getAttributeList(selectedArchiver.subscriber, "Nok");
                        if (attributeList.length>0) {
                            new FaultyAttributesDialog(
                                    thisDialog, selectedArchiver.subscriber).setVisible(true);
                        }
                    }
                    catch (DevFailed e) {
                        ErrorPane.showErrorMessage(this, e.getMessage(), e);
                    }
                }
            }
        }
        //===============================================================
        @Override
        public String[] clickOnChart(JLChartEvent event) {
            //JLDataView dataView = event.getDataView();
            int index = event.getDataViewIndex();
            Archiver archiver = archivers.get(index);
            selectedArchiver = archiver;

            if (archiver!=null) {
                //  Check mouse modifier
                MouseEvent mouseEvent = event.getMouseEvent();
                if (archiver.attributeFailed.length>0 && mouseEvent!=null &&
                        (mouseEvent.getModifiers() & MouseEvent.SHIFT_MASK)!=0)
                    return null;

                //  Display archiver info
                ArrayList<String>   lines = new ArrayList<String>();
                lines.add(archiver.title);
                lines.add(archiver.totalEvents + " Events Received");
                if ( archiver.attributeOk.length==0)
                    lines.add(" - NO  Attribute OK");
                else
                    lines.add(" - " + archiver.attributeOk.length + " attributes are OK");

                if ( archiver.attributeFailed.length==0)
                    lines.add(" - NO  Attribute Failed");
                else
                    lines.add(" - " + archiver.attributeFailed.length + " attributes Failed");

                int cnt = 0;
                for (String attribute : archiver.attributeFailed) {
                    lines.add("    - " + attribute);
                    if (cnt++>15) {
                        lines.add("       - - - - -");
                        lines.add("       Use Shift for Full List");
                        break;
                    }
                }

                String[]    array = new String[lines.size()];
                for (int i=0 ; i<lines.size() ; i++)
                    array[i] = lines.get(i);
                return array;
            }
            else
                return new String[0];
        }

        //===============================================================
    }
    //===============================================================
    //===============================================================



    //===============================================================
    //===============================================================
    private class Archiver {
        private Subscriber  subscriber;
        private String      title;
        private String[]    attributeOk;
        private String[]    attributeFailed;
        private int         totalEvents = 0;
        private long        resetTime=-1;
        private long        sinceReset=0;
        //===========================================================
        private Archiver(Subscriber subscriber) throws DevFailed {
            this.subscriber = subscriber;
            title = subscriber.getLabel() + "  (" +
                    TangoUtils.getOnlyDeviceName(subscriber.getName()) + ") :";
            update();
        }
        //===========================================================
        private void update() {
            try {
                DeviceAttribute[] attributes = subscriber.read_attribute(
                        new String[]{ "AttributeOkList",
                                "AttributeNOkList", "AttributeEventNumberList"});
                int i = 0;
                DeviceAttribute attribute = attributes[i++];
                if (attribute.hasFailed())
                    attributeOk = new String[0];
                else
                    attributeOk = attribute.extractStringArray();

                attribute = attributes[i++];
                if (attribute.hasFailed())
                    attributeFailed = new String[0];
                else
                    attributeFailed = attribute.extractStringArray();

                attribute = attributes[i];
                if (!attribute.hasFailed()) {
                    int[] nbEvents = attribute.extractLongArray();
                    totalEvents = 0;
                    for (int nb : nbEvents)
                        totalEvents += nb;
                }
                resetTime = subscriber.getStatisticsResetTime();
                if (resetTime>0) {
                    sinceReset = System.currentTimeMillis() - resetTime;
                }
            }
            catch (DevFailed e) {
                attributeOk = new String[0];
                attributeFailed = new String[0];
            }
        }
        //===========================================================
        private int attributeCount() {
            return attributeOk.length + attributeFailed.length;
        }
        //===========================================================
        public String toString() {
            return subscriber.getLabel();
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
            return archivers.size();
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
            setBackground(getBackground(column));
            Archiver archiver = archivers.get(row);
            switch (column) {
                case SUBSCRIBER_NAME:
                    setText(archiver.subscriber.getLabel());
                    break;
                case ATTRIBUTE_NUMBER:
                    setText(Integer.toString(archiver.attributeCount()));
                    break;
                case EVENT_NUMBER:
                    setText(Integer.toString(archiver.totalEvents));
                    break;
                case RESET_TIME:
                    long resetTime = archiver.resetTime;
                    if (resetTime>0)
                        setText(StatisticsDialog.formatResetTime(resetTime));
                    else
                        setText("Not available");
                    break;
                case RESET_DURATION:
                    resetTime = archiver.resetTime;
                    if (resetTime>0)
                        setText(Utils.strPeriod(archiver.sinceReset / 1000));
                    else
                        setText("Not available");
                    break;
            }
            return this;
        }
        //==========================================================
        private Color getBackground(int column) {
            switch (column) {
                case SUBSCRIBER_NAME:
                    return StatisticsDialog.firstColumnBackground;
                default:
                    return Color.white;
            }
        }
        //==========================================================
    }
    //=========================================================================
    //=========================================================================




    //=========================================================================
    /**
     * Comparator to sort attribute list
     */
    //=========================================================================
    private class ArchiverComparator implements Comparator<Archiver> {

        //======================================================
        public int compare(Archiver archiver1, Archiver archiver2) {
            switch (selectedColumn) {
                case ATTRIBUTE_NUMBER:
                    return valueSort(archiver2.attributeCount(), archiver1.attributeCount());
                case EVENT_NUMBER:
                    return valueSort(archiver2.totalEvents, archiver1.totalEvents);
                case RESET_TIME:
                    return valueSort(archiver1.resetTime, archiver2.resetTime);
                case RESET_DURATION:
                    return valueSort(archiver1.sinceReset, archiver2.sinceReset);
                default:
                    return alphabeticalSort(
                            archiver1.subscriber.getLabel(), archiver2.subscriber.getLabel());
            }
        }
        //======================================================
        private int alphabeticalSort(String s1, String s2) {
            if (s1==null)      return 1;
            else if (s2==null) return -1;
            else return s1.compareTo(s2);
        }
        //======================================================
        private int valueSort(double d1, double d2) {
            if (d1==d2)    return 0;
            else if (d1<0) return  1;   // Not initialized
            else if (d2<0) return -1;   // Not initialized
            else return ((d1 > d2)? 1 : -1);
        }
        //======================================================
    }
    //===============================================================
    //===============================================================

}
