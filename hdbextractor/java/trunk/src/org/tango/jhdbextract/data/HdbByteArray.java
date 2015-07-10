package org.tango.jhdbextract.data;

import org.tango.jhdbextract.HdbFailed;
import org.tango.jhdbextract.HdbSigInfo;

import java.util.ArrayList;

/**
 * HDB byte array data (8 bits integer)
 */
public class HdbByteArray extends HdbData {

  byte[] value = null;
  byte[] wvalue = null;

  public HdbByteArray(int type) {
    this.type = type;
  }

  public byte[] getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public byte[] getWriteValue() throws HdbFailed {

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

  private byte[] parseArray(ArrayList<String> value) throws HdbFailed {

    byte[] ret = new byte[value.size()];

    try {
      for(int i=0;i<value.size();i++) {
        String str = value.get(i);
        if(str==null) {
          ret[i] = 0;
        } else {
          ret[i] = Byte.parseByte(str);
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

    if(type== HdbSigInfo.TYPE_ARRAY_INT8_RO)
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+","+Integer.toString(wvalue.length)+" "+
          qualitytoStr(qualityFactor);

  }

}
