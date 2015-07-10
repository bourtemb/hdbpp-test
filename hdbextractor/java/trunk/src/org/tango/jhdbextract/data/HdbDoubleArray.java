package org.tango.jhdbextract.data;

import org.tango.jhdbextract.HdbFailed;
import org.tango.jhdbextract.HdbSigInfo;

import java.util.ArrayList;

/**
 * HDB double array data
 */
public class HdbDoubleArray extends HdbData {

  double[] value = null;
  double[] wvalue = null;

  public HdbDoubleArray(int type) {
    this.type = type;
  }

  public double[] getValue() throws HdbFailed {

    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    return value;

  }

  public double[] getWriteValue() throws HdbFailed {

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

  private double[] parseArray(ArrayList<String> value) throws HdbFailed {

    double[] ret = new double[value.size()];

    try {
      for(int i=0;i<value.size();i++) {
        String str = value.get(i);
        if(str==null) {
          ret[i] = Double.NaN;
        } else {
          ret[i] = Double.parseDouble(str);
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

    if(type== HdbSigInfo.TYPE_ARRAY_DOUBLE_RO)
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+","+Integer.toString(wvalue.length)+" "+
          qualitytoStr(qualityFactor);

  }

}
