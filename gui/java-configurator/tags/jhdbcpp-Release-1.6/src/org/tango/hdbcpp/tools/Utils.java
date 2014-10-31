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

package org.tango.hdbcpp.tools;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoDs.Except;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Utils {
    private static Utils instance = null;
    private static final String DefaultImagePath = "/org/tango/hdbcpp/img/";
    //======================================================================
    //======================================================================
    private Utils() {
    }
    //======================================================================
    //======================================================================
    public static Utils getInstance() {
        if (instance==null)
            instance = new Utils();
        return instance;
    }
    //===============================================================
    //===============================================================
    public ImageIcon getIcon(String filename) throws DevFailed {
        java.net.URL url =
                getClass().getResource(DefaultImagePath + filename);
        if (url == null) {
            Except.throw_exception("FILE_NOT_FOUND",
                    "Icon file  " + filename + "  not found");
        }

        return new ImageIcon(url);
    }

    //===============================================================
    //===============================================================
    public ImageIcon getIcon(String filename, double ratio) throws DevFailed {
        ImageIcon icon = getIcon(filename);
        return getIcon(icon, ratio);
    }

    //===============================================================
    //===============================================================
    public ImageIcon getIcon(ImageIcon icon, double ratio) {
        if (icon != null) {
            int width = icon.getIconWidth();
            int height = icon.getIconHeight();

            width = (int) (ratio * width);
            height = (int) (ratio * height);

            icon = new ImageIcon(
                    icon.getImage().getScaledInstance(
                            width, height, Image.SCALE_SMOOTH));
        }
        return icon;
    }
    //======================================================================
    //======================================================================




    //===============================================================
    //===============================================================
    public Component startExternalApplication(JFrame parent, String className) throws DevFailed {
        try {
            //	Retrieve class name
            Class	_class = Class.forName(className);

            //	And build object
            Constructor[] constructors = _class.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                Class[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length==1 && parameterTypes[0]==JFrame.class){
                    Component component = (Component) constructor.newInstance(parent);
                    component.setVisible(true);
                    return component;

                }
            }
            throw new Exception("Cannot find constructor for " + className);
        }
        catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                InvocationTargetException   ite = (InvocationTargetException) e;
                Throwable   throwable = ite.getTargetException();
                System.out.println(throwable);
                if (throwable instanceof DevFailed)
                    throw (DevFailed) throwable;
            }
            Except.throw_exception(e.toString(), e.toString());
        }
        return null;
    }
    //===============================================================
    /**
     * Open a file and return lines read.
     *
     * @param filename file to be read.
     * @return the file content read as lines.
     * @throws DevFailed in case of failure during read file.
     */
    //===============================================================
    public static ArrayList<String> readFileLines(String filename) throws DevFailed {
        ArrayList<String>   lines = new ArrayList<String>();
        String code = readFile(filename);
        StringTokenizer stringTokenizer = new StringTokenizer(code, "\n");
        while (stringTokenizer.hasMoreTokens())
            lines.add(stringTokenizer.nextToken());
        return lines;
    }
    //===============================================================
    /**
     * Open a file and return text read.
     *
     * @param filename file to be read.
     * @return the file content read.
     * @throws DevFailed in case of failure during read file.
     */
    //===============================================================
    public static String readFile(String filename) throws DevFailed {
        String str = "";
        try {
            FileInputStream fid = new FileInputStream(filename);
            int nb = fid.available();
            byte[] inStr = new byte[nb];
            nb = fid.read(inStr);
            fid.close();

            if (nb > 0)
                str = new String(inStr);
        } catch (Exception e) {
            Except.throw_exception(e.getMessage(), e.toString());
        }
        return str;
    }
    //======================================================================
    //======================================================================
    public static String[] matchFilter(String[] attributes, String pattern) {
        ArrayList<String>   list = new ArrayList<String>();
        for (String attribute : attributes) {
            if (matches(attribute.substring("tango://".length()), pattern)) {
                list.add(attribute);
            }
        }
        String[]    array = new String[list.size()];
        for (int i=0 ; i<list.size() ; i++)
            array[i] = list.get(i);
        return array;
    }
    //======================================================================
    //======================================================================
    public static boolean matches(String attributeName, String pattern) {
        StringTokenizer stk = new StringTokenizer(attributeName, "/");
        ArrayList<String>   attributeTokens = new ArrayList<String>();
        while (stk.hasMoreTokens())  attributeTokens.add(stk.nextToken());

        stk = new StringTokenizer(pattern, "/");
        ArrayList<String>   patternTokens = new ArrayList<String>();
        while (stk.hasMoreTokens())  patternTokens.add(stk.nextToken());

        int index = 0;
        for (String attributeToken : attributeTokens) {
            if (index>patternTokens.size()-1)
                return true;
            String  patternToken = patternTokens.get(index++);
            if (!matchField(attributeToken, patternToken))
                return false;
        }

        return true;
    }
    //======================================================================
    //======================================================================
    private static boolean matchField(String field, String pattern) {
        if (pattern.equals("*"))
            return true;
        if (pattern.contains("*")) {
            if (pattern.startsWith("*")) {
                //  Start with *
                pattern = pattern.substring(1);
            }
            int pos = pattern.indexOf('*');
            if (pos>0) {
                String patternStart = pattern.substring(0, pos);
                if (field.contains(patternStart)) {
                    pos++;  //  after *
                    String  patternEnd = pattern.substring(pos);
                    pos = pattern.indexOf('*', pos);
                    if (pos>0) {
                        patternEnd = pattern.substring(pos);
                        return field.endsWith(patternEnd);
                    }
                    else {
                        //  No * any more return endsWith
                        return field.endsWith(patternEnd);
                    }
                }
                else  {
                    //  No pattern return equals
                    return false;
                }
            }
            else  {
                //  No * any more return endsWith
                return field.endsWith(pattern);
            }
        }
        else {
            //  No * return equals
            return pattern.equals(field);
        }
    }
    //===============================================================
    //===============================================================
    public static String strPeriod(double period) {
        if (period < 60.0) {
            return String.format("%.2f sec.", period);
        } else {
            int intPeriod = (int) period;
            if (intPeriod < 3600) {
                int mn  = intPeriod / 60;
                int sec = intPeriod - 60 * mn;
                return "" + mn + " mn " + ((sec < 10) ? "0" : "") + sec + " sec.";
            } else if (intPeriod < 24 * 3600) {
                int h = intPeriod / 3600;
                intPeriod -= h * 3600;
                int mn = intPeriod / 60;
                int sec = intPeriod - 60 * mn;
                return "" + h + " h " + ((mn < 10) ? "0" : "") + mn + " mn " +
                        ((sec < 10) ? "0" : "") + sec + " sec.";
            } else {
                int days = intPeriod / (24 * 3600);
                return "" + days + " day" + ((days > 1) ? "s " : " ") +
                        strPeriod((double) intPeriod - (days * 24 * 3600));
            }
        }
    }
    //======================================================================
    //======================================================================

    //======================================================================
    //======================================================================
    public static void main(String[] args) {
        boolean b = matches("a/b/v-rga/d", "a/b/*rga/d");
        System.out.println(b);
    }
}
