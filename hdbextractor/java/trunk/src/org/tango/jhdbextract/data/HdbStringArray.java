package org.tango.jhdbextract.data;

import org.tango.jhdbextract.HdbFailed;
import org.tango.jhdbextract.HdbSigInfo;

import java.util.ArrayList;

/**
 * HDB string array data
 */
public class HdbStringArray extends HdbData {

  String[] value = null;
  String[] wvalue = null;

  public HdbStringArray(int type) {
    this.type = type;
  }

  public String[] getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public String[] getWriteValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return wvalue;

  }

  public void parseValue(ArrayList<String> value) throws HdbFailed {

    this.value = parseArray(value);

  }

  public void parseWriteValue(ArrayList<String> value) throws HdbFailed {

    if(value!=null)
      this.wvalue = parseArray(value);

  }

  private String[] parseArray(ArrayList<String> value) throws HdbFailed {

    String[] ret = new String[value.size()];

    for(int i=0;i<value.size();i++) {
      String str = value.get(i);
      if(str==null) {
        ret[i] = "NULL";
      } else {
        ret[i] = str;
      }
    }

    return ret;

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_ARRAY_STRING_RO)
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+","+Integer.toString(wvalue.length)+" "+
          qualitytoStr(qualityFactor);

  }


}
