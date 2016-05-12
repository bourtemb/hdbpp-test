//+======================================================================
// $Source:  $
//
// Project:   Tango
//
// Description:  Basic Dialog Class to display info
//
// $Author: pascal_verdier $
//
// Copyright (C) :      2004,2005,2006,2007,2008,2009,2009,2010,2011,2012,2013,2014,2015,2016
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

package org.tango.hdb_configurator.configurator.strategy;



/**
 * Created by verdier on 14/04/2016.
 * Define strategy object
 */
public class Strategy {
    private String name;
    private Boolean used = false ;
    private String description = null;
    //===========================================================
    //===========================================================
    public Strategy(String name, boolean used, String description) {
        this.name = name;
        this.used = used;
        this.description = description;
    }
    //===========================================================
    //===========================================================
    public String getName() {
        return name;
    }
    //===========================================================
    //===========================================================
    public boolean isUsed() {
        return used;
    }
    //===========================================================
    //===========================================================
    public void toggleUsed() {
        used = !used;
    }
    //===========================================================
    //===========================================================
    public String getHtmlDescription() {
        StringBuilder sb = new StringBuilder("<b><u>" + this.toString() + ":</u></b><ul>\n");
        if (description==null)
            return sb.toString() + "....";
        //  Convert '\n' in "<br>"
        int start = 0;
        int end;
        while ((end=description.indexOf('\n', start+1))>0) {
            sb.append(description.substring(start, end)).append("<br>");
            start = end;
        }
        sb.append(description.substring(start));
        return sb.toString();
    }
    //===========================================================
    //===========================================================
    public String getDescription() {
        return description;
    }
    //===========================================================
    //===========================================================
    public String toString() {
        return "Strategy "+ name + "   {" + ((used)? "used" : "not used") + ")";
    }
    //===========================================================
    //===========================================================
}
