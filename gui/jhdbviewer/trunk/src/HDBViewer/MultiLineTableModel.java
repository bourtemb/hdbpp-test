/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HDBViewer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * Table model for HDB
 */

class CellItem {
  int column;
  String value;
}

class RowItem {  
  
  long   time;
  ArrayList<CellItem> value;
  
  String getValueAt(int column) {
    
    boolean found=false;
    int i=0;
    while(!found && i<value.size()) {
      found = value.get(i).column == column;
      if(!found) i++;
    }
    if(found) {
      return value.get(i).value;
    } else {
      return "";
    }
    
  }
  
}

public class MultiLineTableModel extends AbstractTableModel {
  
  private ArrayList<RowItem> data;
  private String[] colNames;
  private boolean doMicroSec = false;
  
  public MultiLineTableModel() {
    data = new ArrayList<RowItem>();
    colNames = new String[0];
  }
  
  public void setDoMicroSec(boolean doMicro) {
    doMicroSec = doMicro;
    fireTableDataChanged();
  }
  
  public boolean isDoingMicroSec() {
    return doMicroSec;
  }
  
  public void setColumnNames(String[] names) {
    colNames = names;
    fireTableStructureChanged();
  }
  
  public void reset() {
    data = new ArrayList<RowItem>();
    colNames = new String[0];    
  }
  
  private RowItem binarySearch(long time) {
    
    int low = 0;
    int high = data.size()-1;

    while (low <= high) {
      int mid = (low + high) >>> 1;
      long midVal = data.get(mid).time;

       if (midVal < time)
         low = mid + 1;
       else if (midVal > time)
         high = mid - 1;
       else
         return data.get(mid); // item found
    }
    
    // r is the insertion position
    int r = -(low + 1);
    if(r<0) r=  -(r+1);
    
    RowItem newItem = new RowItem();
    newItem.time = time;
    newItem.value = new ArrayList<CellItem>();
    data.add(r,newItem);
    return newItem;   
            
  }
  
  public void add(String value,long time,int colIdx) {
    
    RowItem n = binarySearch(time);
    CellItem c = new CellItem();
    c.column = colIdx;
    c.value = value;
    n.value.add(c);
                
  }

  public int getRowCount() {
    return data.size();
  }

  public int getColumnCount() {
    return colNames.length;
  }

  public String getColumnName(int columnIndex) {
    return colNames[columnIndex];    
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    
    RowItem i = data.get(rowIndex);
    if(columnIndex==0) {
      // Timestamp
      return Utils.formatTime(i.time,(doMicroSec?Utils.FORMAT_US:Utils.FORMAT_SEC));
    } else {
      return i.getValueAt(columnIndex);
    }
    
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
  }
  
  private void appendRow(StringBuffer f,int r) {

    int cCount = getColumnCount();

    String[] ds = Utils.formatDateAndTime(data.get(r).time);
    f.append(ds[0]).append("\t");
    f.append(ds[1]).append("\t");
    for(int c=1;c<cCount;c++) {
      String v = (String)getValueAt(r,c);
      f.append(v).append("\t");
    }
    f.append("\n");

  }
  
  public String buildTabbedString(int[] rows) {
    
    StringBuffer f = new StringBuffer();
    
    int cCount = getColumnCount();
    
    f.append("HDB Date\tHDB Time\t");
    
    for(int i=1;i<cCount;i++) {
      f.append(colNames[i]).append("\t");      
    }
    f.append("\n");
    
    if(rows==null) {
      // Whole table
      for(int r=0;r<getRowCount();r++)
        appendRow(f,r);      
    } else {
      // Selected rows
      for(int r=0;r<rows.length;r++)
        appendRow(f,rows[r]);
    }
    
    return f.toString();
    
  }
  
  public void saveFile(String fileName) throws IOException {
    
    FileWriter f = new FileWriter(fileName);
    f.write("# File generated from hdbviewer application\n");
    f.write("#\n");
    f.write(buildTabbedString(null));       
    f.close();
    
  }
  
}
