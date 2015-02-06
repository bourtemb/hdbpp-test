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


package org.tango.hdbcpp.atktable;

import fr.esrf.Tango.AttrDataFormat;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.AttributeInfo;
import fr.esrf.TangoApi.DeviceProxy;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.Enumeration;

//===============================================================
/**
 * Class Description:
 * TableConfig Class
 * Define Panel config.
 * It is a Vector of attributes
 *
 * @author Pascal Verdier
 */
//===============================================================
class TableConfig  {
    String title = "ATK Table Panel";
    String[] columnNames = new String[0];
    String[] rowNames = new String[0];
    int period = 1000;
    int height = -1;
    int width = -1;
    private int[] columnWidth;
    private int rowTitleWidth = 200;

    private ArrayList<Attribute>    attributes = new ArrayList<Attribute>();
    //==========================================================
    //==========================================================
    public TableConfig() {

    }
    //==========================================================
    //==========================================================
    public TableConfig(String[] rowNames, String[] columnNames,
                       String[][] attributeNames, int[] columnWidth) {
        this.rowNames = rowNames;
        this.columnNames = columnNames;
        this.columnWidth = columnWidth;

        for (int row=0 ; row<rowNames.length ; row++)
            for (int col=0 ; col<columnNames.length ; col++)
                add(attributeNames[row][col], row, col);
    }

    //==========================================================
    //==========================================================
    public TableConfig(String name) throws DevFailed {
        DeviceProxy dev = new DeviceProxy(name);
        columnNames = new String[]{ name };

        AttributeInfo[] info = dev.get_attribute_info();

        ArrayList<AttributeInfo> attributeInfoList = new ArrayList<AttributeInfo>();
        for (AttributeInfo anInfo : info)
            if (anInfo.data_format==AttrDataFormat.SCALAR)
                attributeInfoList.add(anInfo);

        rowNames = new String[attributeInfoList.size()];
        for (int i = 0 ; i<attributeInfoList.size() ; i++) {
            AttributeInfo attributeInfo = attributeInfoList.get(i);
            rowNames[i] = attributeInfo.label;
            add(name + "/" + attributeInfo.name, i, 0);
        }
    }

    //==========================================================
    //==========================================================
    void checkSize(JTable table) {
        //	Manage column width
        if (columnWidth!=null) {
            final Enumeration columns = table.getColumnModel().getColumns();
            TableColumn tableColumn;
            width = 0;

            if (columns.hasMoreElements()) {
                tableColumn = (TableColumn) columns.nextElement();
                tableColumn.setPreferredWidth(rowTitleWidth);
                width += rowTitleWidth;
            }

            for (int i=0 ; columns.hasMoreElements() && i<columnWidth.length ; i++) {
                tableColumn = (TableColumn) columns.nextElement();
                if (columnWidth[i]==0)
                    columnWidth[i] = 80;
                tableColumn.setPreferredWidth(columnWidth[i]);
                width += columnWidth[i];
            }
        } else if (width<0)
            width = (nbColumns() + 1) * 180;

        int h = table.getRowHeight();
        if (height<0)
            height = nbRows() * h + 10;
    }

    //==========================================================
    //==========================================================
    void setColWidth(int title_width, int[] col_width) {
        rowTitleWidth = title_width;
        columnWidth = col_width;
    }

    //==========================================================
    //==========================================================
    int nbRows() {
        return rowNames.length;
    }

    //==========================================================
    //==========================================================
    int nbColumns() {
        return columnNames.length;
    }

    //===========================================================
    //===========================================================
    public Attribute attributeAt(int row, int col) {
        for (int i = 0 ; i<attributes.size() ; i++) {
            Attribute att = attributeAt(i);
            if (att.row==row && att.col==col)
                return att;
        }
        return null;
    }

    //===========================================================
    //===========================================================
    public Attribute attributeAt(int idx) {
        return attributes.get(idx);
    }

    //===========================================================
    //===========================================================
    public Attribute attributeNamed(String name) {
        Attribute att;
        for (int i = 0 ; i<attributes.size() ; i++) {
            att = attributeAt(i);
            if (att.name!=null)
                if (att.name.equals(name))
                    return att;
        }
        return null;
    }

    //==========================================================
    //==========================================================
    void setAttributeNameAt(String name, int row, int col) {
        Attribute att = attributeAt(row, col);
        if (att!=null)
            attributes.remove(att);
        add(name, row, col);
    }

    //===========================================================
    //===========================================================
    public boolean add(String name, int row, int col) {
        return attributes.add(new Attribute(name, row, col));
    }

    //===========================================================
    //===========================================================
    public void setError(String attname, boolean b) {
        Attribute att;
        if (attname!=null) {
            att = attributeNamed(attname);
            if (att!=null)
                att.on_error = b;
        }
    }

    //===========================================================
    //===========================================================
    public boolean onError() {
        Attribute att;
        for (int i = 0 ; i<attributes.size() ; i++) {
            att = attributeAt(i);
            if (att.name!=null && att.on_error)
                return true;
        }
        return false;
    }

    //===========================================================
    //===========================================================
    public boolean allConnected() {
        for (int i = 0 ; i<attributes.size() ; i++)
            if (!attributeAt(i).connected)
                return false;
        return true;
    }

    //==========================================================
    //==========================================================
    public void resetConnection() {
        for (int i = 0 ; i<attributes.size() ; i++)
            attributeAt(i).connected = false;
    }

    //===========================================================
    //===========================================================
    public int size() {
        return attributes.size();
    }
    //===========================================================
    //===========================================================
    public void remove(Attribute attribute) {
        attributes.remove(attribute);
    }
    //===========================================================
    //===========================================================






    //===========================================================
    //===========================================================
    public class Attribute {
        String name;
        int row;
        int col;
        boolean connected = false;
        boolean on_error = true;

        //=======================================================
        public Attribute(String name, int row, int col) {
            this.name = name;
            this.row = row;
            this.col = col;
        }

        //=======================================================
        public String toString() {
            return name;
        }
    }
    //===========================================================


}
