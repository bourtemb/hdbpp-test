package org.tango.jhdbextract.Cassandra;

import org.tango.jhdbextract.DbSchema;
import org.tango.jhdbextract.HdbFailed;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
  String[] partitions;

  /**
   * Create a period object
   * @param start Start date (number of millisecond since epoch)
   * @param end Stop date (number of millisecond since epoch)
   */

   Period(long start, long end) {

     this.start = new Timestamp(start);
     this.end   = new Timestamp(end);

     Date   startDate = new Date(start);
     String p0 = periodFormat.format(startDate);
     Date   endDate = new Date(end);
     String p1 = periodFormat.format(endDate);

     if (p0.equals(p1)) {
       partitions = new String[] { p0 };
     } else {
       partitions = new String[] { p0, p1 };
     }

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
      d0 = DbSchema.hdbDateFormat.parse(startDate);
    } catch( ParseException e ) {
      throw new HdbFailed("Wrong start date format : " + e.getMessage());
    }

    try {
      d1 = DbSchema.hdbDateFormat.parse(stopDate);
    } catch( ParseException e ) {
      throw new HdbFailed("Wrong stop date format : " + e.getMessage());
    }


    long start = d0.getTime();
    long endTime = d1.getTime();
    long end = start + GRANULARITY;

    while (end<endTime) {
      periods.add(new Period(start, end));
      start = end + 1000;
      end = start + GRANULARITY;
    }
    periods.add(new Period(start, endTime));
    return periods;

  }


}
