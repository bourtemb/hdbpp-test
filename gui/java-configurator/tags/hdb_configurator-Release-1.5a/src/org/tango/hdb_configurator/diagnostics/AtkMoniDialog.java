//+======================================================================
// $Source: /segfs/tango/cvsroot/jclient/jblvac/src/jblvac/vacuum_panel/AtkMoniDialog.java,v $
//
// Project:   Tango
//
// Description:  Basic Dialog Class to display ATK trend
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
// $Revision: 1.1.1.1 $
//
//-======================================================================

package org.tango.hdb_configurator.diagnostics;


import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import org.tango.hdb_configurator.common.SplashUtils;

import javax.swing.*;
import java.util.List;


//===============================================================
/**
 * JDialog Class to display ATK trend
 *
 * @author Pascal Verdier
 */
//===============================================================


@SuppressWarnings("MagicConstant")
public class AtkMoniDialog extends JDialog {

    private JFrame parent;
    private AtkMoniTrend atkMoniTrend;
    //===============================================================
    /**
     * Creates new form AtkMoniDialog
     *
     * @param parent      the parent frame instance
     * @param attributeNames list of devices to be monitored
     * @param title dialog title
     */
    //===============================================================
    public AtkMoniDialog(JFrame parent, List<String> attributeNames, String title) {
        super(parent, false);
        this.parent = parent;
        SplashUtils.getInstance().startSplash();
        SplashUtils.getInstance().increaseSplashProgress(2, "Create " + title);
        initComponents();

        atkMoniTrend = new AtkMoniTrend(title, attributeNames);
        getContentPane().add(atkMoniTrend, java.awt.BorderLayout.CENTER);

        //titleLabel.setText(title);
        titleLabel.setVisible(false);
        ATKGraphicsUtils.centerDialog(this);
        cancelBtn.setVisible(false);
        pack();
        SplashUtils.getInstance().stopSplash();
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

        javax.swing.JPanel topPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
        cancelBtn = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        titleLabel.setText("Dialog Title");
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
    private void doClose() {

        if (parent == null)
            System.exit(0);
        else {
            //	End of dialog -> stop refresher
            atkMoniTrend.getModel().stopRefresher();
            atkMoniTrend.clearModel();
            setVisible(false);
            dispose();
        }
    }
    //===============================================================
    //===============================================================


    //===============================================================
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelBtn;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
    //===============================================================
}