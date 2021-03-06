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

package org.tango.hdb_configurator.diagnostics;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoDs.Except;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import org.tango.hdb_configurator.common.SplashUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;


//===============================================================

/**
 *	JDialog Class to display info
 *
 *	@author  Pascal Verdier
 */
//===============================================================


@SuppressWarnings("MagicConstant")
public class AttributesTableDialog extends JDialog {

    private ArrayList<String> filteredAttributes = new ArrayList<String>();
    private DataTableModel model;
    private String[] attributeList;
    private String columnName = "Attribute Names";

    private static final Color firstColumnBackground = new Color(0xe0e0e0);
	//===============================================================
	/**
	 *	Creates new form FaultyAttributesDialog for several subscribers
	 */
	//===============================================================
    public AttributesTableDialog(JFrame parent, String title, String [] attributeList) throws DevFailed {
        super(parent, false);
        SplashUtils.getInstance().startSplash();
        try {
            initComponents();
            this.attributeList = attributeList;
            Collections.addAll(filteredAttributes, attributeList);
            columnName = title + " attribute" + ((attributeList.length>1)?"s":"");
            setTitle(title);
            buildTableComponent();

            pack();
            ATKGraphicsUtils.centerDialog(this);
        }
        catch (DevFailed e) {
            SplashUtils.getInstance().stopSplash();
            throw e;
        }
        SplashUtils.getInstance().stopSplash();
    }
    //===============================================================
    //===============================================================
    private void buildTableComponent() throws DevFailed {
        try {
            model = new DataTableModel();

            // Create the table
            JTable table = new JTable(model);
            table.setRowSelectionAllowed(true);
            table.setColumnSelectionAllowed(true);
            table.setDragEnabled(false);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 14));
            table.setDefaultRenderer(String.class, new LabelCellRenderer());

            //	Put it in scrolled pane
            JScrollPane scrollPane = new JScrollPane(table);
            getContentPane().add(scrollPane, BorderLayout.CENTER);

            //  Compute size to display
            pack();
            int height = table.getHeight();
            if (height>800) height = 800;
            scrollPane.setPreferredSize(new Dimension(table.getWidth(), height+20));
        }
        catch (Exception e) {
            e.printStackTrace();
            Except.throw_exception("INIT_ERROR", e.toString());
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
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
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

        jLabel2.setText("            ");
        topPanel.add(jLabel2);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel1.setText("Filter:   ");
        topPanel.add(jLabel1);

        filterTextField.setColumns(25);
        filterTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterTextFieldActionPerformed(evt);
            }
        });
        topPanel.add(filterTextField);

        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });
        topPanel.add(applyButton);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

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
    private void dismissItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_dismissItemActionPerformed
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
    private void applyButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        applyFilter();
    }//GEN-LAST:event_applyButtonActionPerformed

    //===============================================================
    //===============================================================
    @SuppressWarnings("UnusedParameters")
    private void filterTextFieldActionPerformed(ActionEvent evt) {//GEN-FIRST:event_filterTextFieldActionPerformed
        applyFilter();
    }//GEN-LAST:event_filterTextFieldActionPerformed

    //===============================================================
	//===============================================================
    private void applyFilter() {
        String  filter = filterTextField.getText();
        filteredAttributes = new ArrayList<String>();
        for (String attribute : attributeList) {
            if (attribute.contains(filter)) {
                filteredAttributes.add(attribute);
            }
        }
        model.fireTableDataChanged();
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
    // End of variables declaration//GEN-END:variables
	//===============================================================



    //==============================================================
    /**
     * The Table model
     */
    //==============================================================
    public class DataTableModel extends AbstractTableModel {
        //==========================================================
        public int getColumnCount() {
            return 1;
        }

        //==========================================================
        public int getRowCount() {
            return filteredAttributes.size();
        }

        //==========================================================
        public String getColumnName(int columnIndex) {
            String title = columnName;

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
            setBackground(firstColumnBackground);
            String  attribute = filteredAttributes.get(row);
            setText(attribute);
            setToolTipText(null);
            setIcon(null);
            return this;
        }
    }
    //=========================================================================
    //=========================================================================
}
