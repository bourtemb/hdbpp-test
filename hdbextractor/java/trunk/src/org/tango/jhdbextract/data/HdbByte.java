package org.tango.jhdbextract.data;

import org.tango.jhdbextract.HdbFailed;
import org.tango.jhdbextract.HdbSigInfo;

import java.util.ArrayList;

/**
 * HDB byte data (8bit integer)
 */
public class HdbByte extends HdbData {

  byte value = 0;
  byte wvalue = 0;

  public HdbByte(int type) {
    this.type = type;
  }

  public byte getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public byte getWriteValue() throws HdbFailed {

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
        this.value = Byte.parseByte(str);
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
          this.wvalue = Byte.parseByte(str);
      } catch(NumberFormatException e) {
        throw new HdbFailed("Invalid number syntax for write value");
      }
    }

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_SCALAR_INT8_RO)
      return timeToStr(dataTime)+": "+Byte.toString(value)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": "+Byte.toString(value)+";"+Byte.toString(wvalue)+" "+
          qualitytoStr(qualityFactor);

  }

}
