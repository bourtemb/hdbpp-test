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
import org.tango.hdbcpp.tools.PopupHtml;
import org.tango.hdbcpp.tools.SplashUtils;
import org.tango.hdbcpp.tools.TangoUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;


//===============================================================
/**
 *	JDialog Class to display info
 *
 *	@author  Pascal Verdier
 */
//===============================================================


@SuppressWarnings("MagicConstant")
public class DistributionDialog extends JDialog {

	private JFrame	parent;
    private SubscriberMap subscriberMap;
    private DistributionChart distributionChart;
	//===============================================================
	/**
	 *	Creates new form DistributionDialog
	 */
	//===============================================================
	public DistributionDialog(JFrame parent, SubscriberMap subscriberMap) throws DevFailed {
		super(parent, true);
		this.parent = parent;
        SplashUtils.getInstance().startSplash();
        initComponents();

        try {
            if (subscriberMap==null) {
                String  configuratorDeviceName = TangoUtils.getConfiguratorDeviceName();
                subscriberMap = new SubscriberMap(new DeviceProxy(configuratorDeviceName));
            }
            this.subscriberMap = subscriberMap;
            distributionChart = new DistributionChart();
            getContentPane().add(distributionChart, BorderLayout.CENTER);

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




    //===============================================================
    /**
     * JLChart class to display distribution
     */
    //===============================================================
    private class DistributionChart extends JLChart implements IJLChartListener {
        private Hashtable<Integer, Archiver> archivers = new Hashtable<Integer, Archiver>();
        private Archiver selectedArchiver = null;
        private ArchiverPopupMenu   archiverMenu = new ArchiverPopupMenu();
        private JLDataView failedDataView;
        private JLDataView okDataView;
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
            getY1Axis().addDataView(failedDataView);
            getY1Axis().addDataView(okDataView);

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
        private void updateValues() throws DevFailed {
            resetDataViews();
            int index = 0;
            archivers.clear();
            ArrayList<String> labels = subscriberMap.getLabelList();
            SplashUtils.getInstance().reset();
            attributeCount = 0;
            for (String label : labels) {
                SplashUtils.getInstance().increaseSplashProgressForLoop(labels.size(), "Reading " + label);
                Archiver archiver = new Archiver(subscriberMap.getSubscriber(label));

                double x = index;
                okDataView.add(x, archiver.attributeOk.length);
                failedDataView.add(x + 0.2, archiver.attributeFailed.length);
                attributeCount += archiver.attributeCount();

                archivers.put(index++, archiver);
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
        }
        //===============================================================
        private void buildAxises() {
            //  Create X axis.
            JLAxis  xAxis = getXAxis();
            xAxis.setName("Archivers");
            xAxis.setAnnotation(JLAxis.VALUE_ANNO);
            xAxis.setGridVisible(true);
            xAxis.setSubGridVisible(true);

            JLAxis  yAxis = getY1Axis();
            yAxis.setName("Attributes");
            yAxis.setAutoScale(true);
            yAxis.setScale(JLAxis.LINEAR_SCALE);
            yAxis.setGridVisible(true);
            yAxis.setSubGridVisible(true);
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
            //dataView.setBarWidth(12);
            dataView.setFillStyle(JLDataView.FILL_STYLE_SOLID);

            return dataView;
        }
        //===============================================================
        private void chartMouseClicked(MouseEvent event) {
            int mask = event.getModifiers();
            if ((mask & MouseEvent.BUTTON1_MASK) != 0) {
                if (selectedArchiver!=null) {
                    if (selectedArchiver.attributeFailed.length>0) {
                        archiverMenu.showMenu(event);
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
                ArrayList<String>   list = new ArrayList<String>();
                list.add(archiver.title);
                if ( archiver.attributeOk.length==0)
                    list.add(" - NO  Attribute OK");
                else
                    list.add(" - " + archiver.attributeOk.length + " attributes are OK");

                if ( archiver.attributeFailed.length==0)
                    list.add(" - NO  Attribute Failed");
                else
                    list.add(" - " + archiver.attributeFailed.length + " attributes Failed");

                int cnt = 0;
                for (String attribute : archiver.attributeFailed) {
                    list.add("    - " + attribute);
                    if (cnt++>15) {
                        list.add("       - - - - -");
                        break;
                    }
                }

                String[]    array = new String[list.size()];
                for (int i=0 ; i<list.size() ; i++)
                    array[i] = list.get(i);
                return array;
            }
            else
                return new String[0];
        }

        //===============================================================
        private class Archiver {
            private Subscriber  subscriber;
            private String      title;
            private String[]    attributeOk;
            private String[]    attributeFailed;
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
                            new String[]{ "AttributeOkList", "AttributeNOkList"});
                    if (attributes[0].hasFailed())
                        attributeOk = new String[0];
                    else
                        attributeOk = attributes[0].extractStringArray();
                    if (attributes[1].hasFailed())
                        attributeFailed = new String[0];
                    else
                        attributeFailed = attributes[1].extractStringArray();
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
            private String failedListToHtml() {
                StringBuilder   sb = new StringBuilder("<center><h1>"+ title + "</h1></center>\n");
                for (String attribute  : attributeFailed) {
                    sb.append("<li> ").append(attribute).append("</li>\n");
                }
                return sb.toString();
            }
            //===========================================================
            public String toString() {
                return subscriber.getLabel();
            }
            //===========================================================
        }
        //===============================================================
    }
    //===============================================================
    //===============================================================




    //======================================================
    /**
     * Popup menu class
     */
    //======================================================
    private static final int DISPLAY_FAILED  = 0;
    private static final int OFFSET = 2;    //	Label And separator

    private static String[] menuLabels = {
            "Attribute Failed List",
    };
    //=======================================================
    //=======================================================
    private class ArchiverPopupMenu extends JPopupMenu {
        private JLabel title;
        //======================================================
        private ArchiverPopupMenu() {
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
        private void showMenu(MouseEvent event) {
            title.setText(distributionChart.selectedArchiver.toString());
            show(distributionChart, event.getX(), event.getY());
        }
        //======================================================
        private void menuActionPerformed(ActionEvent evt) {
            //	Check component source
            Object obj = evt.getSource();
            int itemIndex = -1;
            for (int i=0 ; i<menuLabels.length ; i++)
                if (getComponent(OFFSET + i)==obj)
                    itemIndex = i;
            switch (itemIndex) {
                case DISPLAY_FAILED:
                    new PopupHtml(parent).show(
                            distributionChart.selectedArchiver.failedListToHtml());
                    break;
            }
        }
        //======================================================
    }
}
