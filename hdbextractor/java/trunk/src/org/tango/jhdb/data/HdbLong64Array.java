package org.tango.jhdb.data;

import org.tango.jhdb.HdbFailed;
import org.tango.jhdb.HdbSigInfo;

import java.util.ArrayList;

/**
 * HDB long array data (64 bits integer)
 */
public class HdbLong64Array extends HdbData {

  long[] value = null;
  long[] wvalue = null;

  public HdbLong64Array(int type) {
    this.type = type;
  }

  public HdbLong64Array(int type,long[] value) {
    this.type = type;
    this.value = value;
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

  public void parseValue(ArrayList<Object> value) throws HdbFailed {

    this.value = parseLong64Array(value);

  }

  public void parseWriteValue(ArrayList<Object> value) throws HdbFailed {

    if(value!=null)
      this.wvalue = parseLong64Array(value);

  }

  private long[] parseLong64Array(ArrayList<Object> value) throws HdbFailed {

    long[] ret = new long[value.size()];
    if(value.size()==0)
      return ret;

    if( value.get(0) instanceof String ) {

      // Value given as string
      try {
        for(int i=0;i<value.size();i++) {
          String str = (String)value.get(i);
          if(str==null) {
            ret[i] = 0;
          } else {
            ret[i] = Long.parseLong(str);
          }
        }
      } catch(NumberFormatException e) {
        throw new HdbFailed("parseLong64Array: Invalid number syntax");
      }

    } else {

      for(int i=0;i<value.size();i++) {
        Long l = (Long)value.get(i);
        ret[i] = l.longValue();
      }

    }

    return ret;

  }

  public String toString() {

    if(hasFailed())
      return timeToStr(dataTime)+": "+errorMessage;

    if(type== HdbSigInfo.TYPE_ARRAY_LONG64_RO)
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+" "+qualitytoStr(qualityFactor);
    else
      return timeToStr(dataTime)+": dim="+Integer.toString(value.length)+","+Integer.toString(wvalue.length)+" "+
          qualitytoStr(qualityFactor);

  }

  // Convenience function
  public void applyConversionFactor(double f) {
    for(int i=0;i<dataSize();i++)
      value[i] = (long)(value[i] * f);
  }
  int dataSize() {
    if(value==null)
      return 0;
    else
      return value.length;
  }
  int dataSizeW() {
    if(HdbSigInfo.isRWType(type))
      if(wvalue==null)
        return 0;
      else
        return wvalue.length;
    else
      return 0;
  }

  void copyData(HdbData src) {
    value = ((HdbLong64Array)src).value.clone();
    if(((HdbLong64Array)src).wvalue!=null)
      wvalue = ((HdbLong64Array)src).wvalue.clone();
  }

  public String getValueAsString() {
    if(hasFailed())
      return errorMessage;
    return arrayValue(value);
  }

  public String getWriteValueAsString() {
    if(hasFailed())
      return errorMessage;
    if(hasWriteValue())
      return arrayValue(wvalue);
    else
      return "";
  }

  private String arrayValue(long[] b) {
    StringBuffer ret = new StringBuffer();
    ret.append("Long64["+b.length+"]\n");
    for(int i=0;i<b.length;i++) {
      ret.append(Long.toString(b[i]));
      if(i<b.length-1)
        ret.append("\n");
    }
    return ret.toString();
  }

  public double getValueAsDouble() throws HdbFailed {
    throw new HdbFailed("This datum is not scalar");
  }

  public double getWriteValueAsDouble() throws HdbFailed {
    throw new HdbFailed("This datum is not scalar");
  }

  public double[] getValueAsDoubleArray() throws HdbFailed {
    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    double[] ret = new double[value.length];
    for(int i=0;i<value.length;i++)
      ret[i] = (double)value[i];
    return ret;
  }

  public double[] getWriteValueAsDoubleArray() throws HdbFailed {
    if(hasFailed())
      throw new HdbFailed(this.errorMessage);
    if(!hasWriteValue())
      throw new HdbFailed("This datum has no write value");
    double[] ret = new double[wvalue.length];
    for(int i=0;i<wvalue.length;i++)
      ret[i] = (double)wvalue[i];
    return ret;
  }

}
