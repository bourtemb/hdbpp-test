package org.tango.jhdbextract.data;

import org.tango.jhdbextract.HdbFailed;
import org.tango.jhdbextract.HdbSigInfo;

import java.util.ArrayList;

/**
 * HDB long data (64 bits integer)
 */
public class HdbLong extends HdbData {

  long value = 0;
  long wvalue = 0;

  public HdbLong(int type) {
    this.type = type;
  }

  public long getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public long getWriteValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return wvalue;

  }

  public void parseValue(ArrayList<String> value) throws HdbFailed {

    try {
      String str = value.get(0);
      if(str==null)
        this.value = 0;
      else
        this.value = Long.parseLong(str);
    } catch(NumberFormatException e) {
      throw new HdbFailed("Invalid number syntax for value");
    }

  }

  public void parseWriteValue(ArrayList<String> value) throws HdbFailed {

    if(value!=null) {
      try {
        String str = value.get(0);
        if(str==null)
          this.wvalue = 0;
        else
          this.wvalue = Long.parseLong(str);
      } catch(NumberFormatException e) {
        throw new HdbFailed("Invalid number syntax for write value");
      }
    }

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_SCALAR_INT64_RO)
      return timeToStr(dataTime)+": "+Long.toString(value)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": "+Long.toString(value)+";"+Long.toString(wvalue)+" "+
          qualitytoStr(qualityFactor);

  }

}
