/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HDBViewer;

import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLChart;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 *
 * @author pons
 */
public class ChartPanel extends javax.swing.JPanel {
  
  JLChart chart;
  MainPanel parent;

  /**
   * Creates new form ChartPanel
   */
  ChartPanel(MainPanel parent) {

    this.parent = parent;
    
    initComponents();
    
    chart = new JLChart();
    chart.setPreferredSize(new Dimension(800, 600));
    chart.getY1Axis().setAutoScale(true);
    chart.getY2Axis().setAutoScale(true);
    chart.getXAxis().setAnnotation(JLAxis.TIME_ANNO);
    long now = System.currentTimeMillis();
    chart.getXAxis().setAutoScale(false);
    chart.getXAxis().setMinimum(now-3600*1000);
    chart.getXAxis().setMaximum(now);
    chart.getXAxis().setAutoScale(true);
    chart.setJLChartListener(parent);
    chartPanel.add(chart, BorderLayout.CENTER);
        
  }
  
  public boolean isShowingError() {
    return chartErrorCheck.isSelected();
  }
  
  public void setShowError(boolean show) {
    chartErrorCheck.setSelected(show);
    updateShowError();
  }
  
  private void updateClickableError() {
  
    boolean b = onlyErrorCheck.isSelected();
    for(AttributeInfo ai:parent.selection) {
      if(ai.chartData!=null)
        ai.chartData.setClickable(!b);
      if(ai.wchartData!=null)
        ai.wchartData.setClickable(!b);
    }
    
  }
  
  
    
  private void updateShowError() {
    
    boolean view = chartErrorCheck.isSelected();

    if(view) {
      for(AttributeInfo ai:parent.selection) {
        if(ai.chartData!=null) {
          JLAxis a1 = ai.chartData.getAxis();
          if(a1!=null) a1.addDataView(ai.errorData);
        } else if (ai.wchartData!=null) {
          JLAxis a2 = ai.wchartData.getAxis();
          if(a2!=null) a2.addDataView(ai.errorData);
        }
      }
    } else {
      for(AttributeInfo ai:parent.selection) {
        if(ai.errorData!=null)
          ai.errorData.removeFromAxis();
      }
    }
    chart.repaint();

  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    chartBtnPanel = new javax.swing.JPanel();
    showLegendCheckBox = new javax.swing.JCheckBox();
    showGridCheckBox = new javax.swing.JCheckBox();
    onlyErrorCheck = new javax.swing.JCheckBox();
    chartErrorCheck = new javax.swing.JCheckBox();
    chartPanel = new javax.swing.JPanel();

    setLayout(new java.awt.BorderLayout());

    chartBtnPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 1));

    showLegendCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    showLegendCheckBox.setSelected(true);
    showLegendCheckBox.setText("Show legend");
    showLegendCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showLegendCheckBoxActionPerformed(evt);
      }
    });
    chartBtnPanel.add(showLegendCheckBox);

    showGridCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    showGridCheckBox.setText("Show grid");
    showGridCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showGridCheckBoxActionPerformed(evt);
      }
    });
    chartBtnPanel.add(showGridCheckBox);

    onlyErrorCheck.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    onlyErrorCheck.setText("Clickable error only");
    onlyErrorCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        onlyErrorCheckActionPerformed(evt);
      }
    });
    chartBtnPanel.add(onlyErrorCheck);

    chartErrorCheck.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    chartErrorCheck.setText("View Errors");
    chartErrorCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        chartErrorCheckActionPerformed(evt);
      }
    });
    chartBtnPanel.add(chartErrorCheck);

    add(chartBtnPanel, java.awt.BorderLayout.SOUTH);

    chartPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    chartPanel.setLayout(new java.awt.BorderLayout());
    add(chartPanel, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void chartErrorCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chartErrorCheckActionPerformed

    updateShowError();

  }//GEN-LAST:event_chartErrorCheckActionPerformed

  private void showLegendCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showLegendCheckBoxActionPerformed
    chart.setLabelVisible(showLegendCheckBox.isSelected());
    chart.repaint();
  }//GEN-LAST:event_showLegendCheckBoxActionPerformed

  private void showGridCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showGridCheckBoxActionPerformed
    boolean v = showGridCheckBox.isSelected();
    chart.getY1Axis().setGridVisible(v);
    chart.getY1Axis().setSubGridVisible(v);
    chart.getXAxis().setGridVisible(v);
    chart.getXAxis().setSubGridVisible(v);
    chart.repaint();
  }//GEN-LAST:event_showGridCheckBoxActionPerformed

  private void onlyErrorCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onlyErrorCheckActionPerformed
     updateClickableError();
  }//GEN-LAST:event_onlyErrorCheckActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel chartBtnPanel;
  javax.swing.JCheckBox chartErrorCheck;
  private javax.swing.JPanel chartPanel;
  private javax.swing.JCheckBox onlyErrorCheck;
  private javax.swing.JCheckBox showGridCheckBox;
  private javax.swing.JCheckBox showLegendCheckBox;
  // End of variables declaration//GEN-END:variables
}