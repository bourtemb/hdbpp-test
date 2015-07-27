package org.tango.jhdbextract;

import org.tango.jhdbextract.data.HdbData;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Signal parameters structure
 */
public class HdbSigParam {

  final static SimpleDateFormat dfr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  public long   recvTime;
  public long   insertTime;
  public String label;
  public String unit;
  public String display_unit;
  public String standard_unit;
  public String format;
  public String archive_rel_change;
  public String archive_abs_change;
  public String archive_period;
  public String description;

  public String timeToStr(long time) {

    long ms = time/1000;
    Date d = new Date(ms);
    String dStr = dfr.format(d);
    String sStr = String.format("%06d",time%1000000);
    return dStr+"."+sStr;

  }

  public String toString() {

    return  "insert_time: " + timeToStr(insertTime) + "\n" +
        "recv_time: " + timeToStr(recvTime) + "\n" +
        "label: " + label + "\n" +
        "unit: " + unit + "\n" +
        "display_unit: " + display_unit + "\n" +
        "standard_unit: " + standard_unit + "\n" +
        "format: " + format + "\n" +
        "archive_rel_change: " + archive_rel_change + "\n" +
        "archive_abs_change: " + archive_abs_change + "\n" +
        "archive_period: " + archive_period + "\n" +
        "description: " + description;

  }

}
