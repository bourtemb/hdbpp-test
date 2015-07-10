package org.tango.jhdbextract.data;

import org.tango.jhdbextract.HdbFailed;
import org.tango.jhdbextract.HdbSigInfo;

import java.util.ArrayList;

/**
 * HDB long array data (64 bits integer)
 */
public class HdbLongArray extends HdbData {

  long[] value = null;
  long[] wvalue = null;

  public HdbLongArray(int type) {
    this.type = type;
  }

  public long[] getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public long[] getWriteValue() throws HdbFailed {

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

  private long[] parseArray(ArrayList<String> value) throws HdbFailed {

    long[] ret = new long[value.size()];

    try {
      for(int i=0;i<value.size();i++) {
        String str = value.get(i);
        if(str==null) {
          ret[i] = 0;
        } else {
          ret[i] = Long.parseLong(str);
        }
      }
    } catch(NumberFormatException e) {
      throw new HdbFailed("Invalid number syntax");
    }

    return ret;

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_ARRAY_INT64_RO)
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+","+Integer.toString(wvalue.length)+" "+
          qualitytoStr(qualityFactor);

  }

}
