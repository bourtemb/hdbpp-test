package org.tango.jhdbextract.data;

import org.tango.jhdbextract.HdbFailed;
import org.tango.jhdbextract.HdbSigInfo;

import java.util.ArrayList;

/**
 * HDB String data
 */
public class HdbString extends HdbData {

  String value = null;
  String wvalue = null;

  public HdbString(int type) {
    this.type = type;
  }

  public String getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public String getWriteValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return wvalue;

  }

  public void parseValue(ArrayList<String> value) throws HdbFailed {

    String str = value.get(0);
    if(str==null)
      this.value = "NULL";
    else
      this.value = str;

  }

  public void parseWriteValue(ArrayList<String> value) throws HdbFailed {

    if(value!=null) {
      String str = value.get(0);
      if(str==null)
        this.wvalue = "NULL";
      else
        this.wvalue = str;
    }

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_SCALAR_STRING_RO)
      return timeToStr(dataTime)+": "+value+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": "+value+";"+wvalue+" "+
          qualitytoStr(qualityFactor);

  }

}
