package org.tango.jhdb;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Casasandra partitioning period management
 */

public class Period {

  // Casasandra granularity partitioning (in millisecond)
  final static long GRANULARITY = 60*60*24*1000;

  final static SimpleDateFormat periodFormat = new SimpleDateFormat("yyyy-MM-dd");

  Timestamp start;
  Timestamp end;
  String    partitionDate;
  boolean   isFull;

  private static Date tmpDate = new Date();


  /**
   * Create a period object
   * Assume that start date and end date are in the same partition
   * @param start Start date (number of millisecond since epoch)
   * @param end Stop date (number of millisecond since epoch)
   */

   Period(long start, long end, boolean isFull) {

     this.start = new Timestamp(start);
     this.end   = new Timestamp(end);
     this.isFull = isFull;
     tmpDate.setTime(start);
     this.partitionDate = periodFormat.format(tmpDate);

  }

  public String toString() {

    String startDate = Hdb.hdbDateFormat.format(start);
    String stopDate = Hdb.hdbDateFormat.format(end);
    return "Full:" + Boolean.toString(isFull) + " Start:" + startDate +
           " Stop:"+stopDate+" Partition:"+partitionDate;

  }

  private static Date getEndOfPeriod(Date date) {

    // This code should be adapted every time GRANULARITY is changed
    // Here gets end of period for a day

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime();

  }

  private static Date getStartOfPeriod(Date date) {

    // This code should be adapted every time GRANULARITY is changed
    // Here gets end of period for a day

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();

  }

  /**
   * Return list of period for given start and stop date.
   * @param startDate Start Date (ex: 10/07/2014 10:00:00)
   * @param stopDate Stop Date (ex: 10/07/2014 10:00:00)
   */
  static ArrayList<Period> getPeriods(String startDate,String stopDate) throws HdbFailed {

    ArrayList<Period> periods = new ArrayList<Period>();
    Date d0;
    Date d1;

    try {
      d0 = Hdb.hdbDateFormat.parse(startDate);
    } catch( ParseException e ) {
      throw new HdbFailed("Wrong start date format : " + e.getMessage());
    }

    try {
      d1 = Hdb.hdbDateFormat.parse(stopDate);
    } catch( ParseException e ) {
      throw new HdbFailed("Wrong stop date format : " + e.getMessage());
    }


    long start = d0.getTime();
    long stop = getEndOfPeriod(d0).getTime();
    long endTime = d1.getTime();

    // First period
    if(endTime>stop) {
      periods.add(new Period(start, stop,false));
    } else {
      periods.add(new Period(start, endTime,false));
    }

    while( endTime>stop ) {

      tmpDate.setTime(stop+1000);
      start = getStartOfPeriod(tmpDate).getTime();
      stop = getEndOfPeriod(tmpDate).getTime();

      if(endTime>stop) {
        // Full period
        periods.add(new Period(start, stop,true));
        /*
        long middle = start+12*60*60*1000;
        periods.add(new Period(start, middle,false));
        periods.add(new Period(middle, stop,false));
        */
      } else {
        // Last period
        periods.add(new Period(start, endTime,false));
      }

    }

    return periods;

  }


}
