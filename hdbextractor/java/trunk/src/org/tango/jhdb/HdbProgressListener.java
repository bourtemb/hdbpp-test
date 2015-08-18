package org.tango.jhdb;

/**
 * Allow to listen on progress of the DB request
 */
public interface HdbProgressListener {

  /**
   * Call when to indicate the progression of a query
   * @param source Source hdb reader
   * @param progress Progress value (0..1)
   * @param pos Attribute in progress (1..nb)
   * @param nb Total number of attribute
   */
  public void progress(HdbReader source,double progress,int pos,int nb);

}
