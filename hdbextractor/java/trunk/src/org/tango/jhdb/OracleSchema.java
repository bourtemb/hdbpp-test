package org.tango.jhdb;

import org.tango.jhdb.data.*;
import tacoHdb.common.HdbBrowser;
import tacoHdb.common.HdbConnection;
import tacoHdb.common.HdbConst;
import tacoHdb.common.HdbException;
import tacoHdb.config.HdbSignal;
import tacoHdb.extract.sig.*;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Interface for Oracle HDB
 */

public class OracleSchema extends HdbReader {

  HdbConnection connection;
  HdbBrowser browser;

  public OracleSchema() throws HdbFailed {

    // Connect to oracle HDB
    connection = HdbConnection.getInstance();
    try {
      connection.connect();
    } catch( HdbException e ) {
      throw new HdbFailed(buildMessage(e));
    }
    browser = new HdbBrowser();

  }

  public String getInfo() throws HdbFailed {

    try {

      String v = String.format("%.1f",Hdb.LIB_RELEASE);
      String version =  "Oracle HDB API v" + v + "\n";
      String host = "Host:" + connection.getDb().getHost() + "\n";
      String service = "Service:" + connection.getDb().getServiceName();
      return version + host + service;

    } catch (HdbException e) {
      throw new HdbFailed(buildMessage(e));
    }

  }

  public String[] getHosts() throws HdbFailed {

   try {

     Vector<String> domains = browser.getSignalDomains();
     ArrayList<String> retHost = new ArrayList<String>();
     for(int i=0;i<domains.size();i++) {
       String nethost = browser.getNethostByDomain(domains.get(i));
       if(!retHost.contains(nethost))
         retHost.add(nethost);
     }

     String[] ret = new String[retHost.size()];
     for(int i=0;i<ret.length;i++)
       ret[i] = retHost.get(i);
     return ret;

    } catch (HdbException e) {
      throw new HdbFailed(buildMessage(e));
    }

  }

  public String[] getDomains(String host) throws HdbFailed {

    try {

      Vector<String> domains = browser.getSignalDomains();
      ArrayList<String> retDomains = new ArrayList<String>();
      for(int i=0;i<domains.size();i++) {
        String nethost = browser.getNethostByDomain(domains.get(i));
        if(nethost.equalsIgnoreCase(host))
          retDomains.add(domains.get(i));
      }

      String[] ret = new String[retDomains.size()];
      for(int i=0;i<ret.length;i++) {
        ret[i] = retDomains.get(i);
      }
      return ret;

    } catch (HdbException e) {
      throw new HdbFailed(buildMessage(e));
    }

  }

  public String[] getFamilies(String host,String domain) throws HdbFailed {

    try {

      Vector<String> families = browser.getSignalFamilies(domain);
      String[] ret = new String[families.size()];
      for(int i=0;i<ret.length;i++) {
        ret[i] = families.get(i);
      }
      return ret;

    } catch (HdbException e) {
      throw new HdbFailed(buildMessage(e));
    }

  }

  public String[] getMembers(String host,String domain,String family) throws HdbFailed {

    try {

      Vector<String> members = browser.getSignalMembers(domain,family);
      String[] ret = new String[members.size()];
      for(int i=0;i<ret.length;i++) {
        ret[i] = members.get(i);
      }
      return ret;

    } catch (HdbException e) {
      throw new HdbFailed(buildMessage(e));
    }

  }

  public String[] getNames(String host,String domain,String family,String member) throws HdbFailed {

    try {

      Vector<String> names = browser.getSignalNames(domain, family, member);
      String[] ret = new String[names.size()];
      for(int i=0;i<ret.length;i++) {
        ret[i] = names.get(i);
      }
      return ret;

    } catch (HdbException e) {
      throw new HdbFailed(buildMessage(e));
    }

  }

  public String[] getAttributeList() throws HdbFailed {

    ArrayList<String> allNames = new ArrayList<String>();

    HdbBrowser b = new HdbBrowser();

    try {

      Vector<String> domains = b.getSignalDomains();
      for(int i=0;i<domains.size();i++) {
        String nethost = b.getNethostByDomain(domains.get(i));
        Vector<String> families = b.getSignalFamilies(domains.get(i));
        for(int j=0;j<families.size();j++) {
          Vector<String> members = b.getSignalMembers(domains.get(i), families.get(j));
          for(int k=0;k<members.size();k++) {
            Vector<String> names = b.getSignalNames(domains.get(i), families.get(j), members.get(k));
            for(int l=0;l<names.size();l++) {
              allNames.add("tango://" + nethost + "/" + domains.get(i) + "/" + families.get(j)
                  + "/" + members.get(k) + "/" + names.get(l));
            }
          }
        }
      }

    } catch (HdbException e) {
      throw new HdbFailed(buildMessage(e));
    }

    String[] ret = new String[allNames.size()];
    for(int i=0;i<ret.length;i++)
      ret[i] = allNames.get(i);

    return ret;

  }

  HdbDataSet getDataFromDB(HdbSigInfo sigInfo,
                           String startDate,
                           String stopDate) throws HdbFailed {

    SigExtractQuery sigExt;

    // Construct query
    try {
      sigExt = new SigExtractQuery(Long.parseLong(sigInfo.sigId));
      sigExt.setStartDate(startDate);
      sigExt.setEndDate(stopDate);
    } catch (HdbException ex) {
      throw new HdbFailed(buildMessage(ex));
    }

    ArrayList<HdbData> retList = new ArrayList<HdbData>();

    try {

      // Fetch data
      SigHistoryData data = sigExt.getSigHistoryData();

      // Convert to HDB++ format
      int recNb = (int)data.getRecNumber();
      if (recNb > 0) {

        // Convert dates
        long[] dates = data.extractLongDates();
        long[] usdates = new long[recNb];
        for (int j = 0; j < recNb; j++)
          usdates[j] = dates[j] * 1000000;

        // Convert data
        int type = sigInfo.type;
        switch (type) {
          case HdbSigInfo.TYPE_SCALAR_SHORT_RO:
            short[] ss = ((ShortScalarHistoryData) data).extractData();
            for (int i = 0; i < recNb; i++) {
              HdbShort d = new HdbShort(type, ss[i]);
              d.setDataTime(usdates[i]);
              retList.add(d);
            }
            break;
          case HdbSigInfo.TYPE_SCALAR_LONG64_RO:
            long[] sl = ((LongScalarHistoryData) data).extractData();
            for (int i = 0; i < recNb; i++) {
              HdbLong64 d = new HdbLong64(type, sl[i]);
              d.setDataTime(usdates[i]);
              retList.add(d);
            }
            break;
          case HdbSigInfo.TYPE_SCALAR_FLOAT_RO:
            float[] sf = ((FloatScalarHistoryData) data).extractData();
            for (int i = 0; i < recNb; i++) {
              HdbFloat d = new HdbFloat(type, sf[i]);
              d.setDataTime(usdates[i]);
              retList.add(d);
            }
            break;
          case HdbSigInfo.TYPE_SCALAR_DOUBLE_RO:
            double[] sd = ((DoubleScalarHistoryData) data).extractData();
            for (int i = 0; i < recNb; i++) {
              HdbDouble d = new HdbDouble(type, sd[i]);
              d.setDataTime(usdates[i]);
              retList.add(d);
            }
            break;
          case HdbSigInfo.TYPE_SCALAR_STRING_RO:
            String[] sst = ((StringScalarHistoryData) data).extractData();
            for (int i = 0; i < recNb; i++) {
              HdbString d = new HdbString(type, sst[i]);
              d.setDataTime(usdates[i]);
              retList.add(d);
            }
            break;
          case HdbSigInfo.TYPE_ARRAY_CHAR_RO:
            byte[][] ac = ((CharArrayHistoryData)data).extractData();
            for (int i = 0; i < recNb; i++) {
              HdbByteArray d = new HdbByteArray(type, ac[i]);
              d.setDataTime(usdates[i]);
              retList.add(d);
            }
            break;
          case HdbSigInfo.TYPE_ARRAY_SHORT_RO:
            short[][] as = ((ShortArrayHistoryData)data).extractData();
            for (int i = 0; i < recNb; i++) {
              HdbShortArray d = new HdbShortArray(type, as[i]);
              d.setDataTime(usdates[i]);
              retList.add(d);
            }
            break;
          case HdbSigInfo.TYPE_ARRAY_LONG64_RO:
            long[][] al = ((LongArrayHistoryData)data).extractData();
            for (int i = 0; i < recNb; i++) {
              HdbLong64Array d = new HdbLong64Array(type, al[i]);
              d.setDataTime(usdates[i]);
              retList.add(d);
            }
            break;
          case HdbSigInfo.TYPE_ARRAY_FLOAT_RO:
            float[][] af = ((FloatArrayHistoryData)data).extractData();
            for (int i = 0; i < recNb; i++) {
              HdbFloatArray d = new HdbFloatArray(type, af[i]);
              d.setDataTime(usdates[i]);
              retList.add(d);
            }
            break;
          case HdbSigInfo.TYPE_ARRAY_DOUBLE_RO:
            double[][] ad = ((DoubleArrayHistoryData)data).extractData();
            for (int i = 0; i < recNb; i++) {
              HdbDoubleArray d = new HdbDoubleArray(type, ad[i]);
              d.setDataTime(usdates[i]);
              retList.add(d);
            }
            break;
        }

      }

    } catch (HdbException ex) {
      throw new HdbFailed(buildMessage(ex));
    }

    return new HdbDataSet(retList);

  }

  public HdbSigInfo getSigInfo(String attName) throws HdbFailed {

    HdbSigInfo ret = new HdbSigInfo();
    ret.name = attName;

    if(!attName.startsWith("tango://"))
      throw new HdbFailed("Fully qualified attribute name expected (eg:tango://nethost/domain/family/member/name)");

    attName = attName.substring(6).toLowerCase();

    HdbSignal s = new HdbSignal();
    s.setFullName(attName);
    try {

      int kind = s.getDefinition();
      ret.sigId = Long.toString(s.getId());

      switch(s.getDataType()) {

        case HdbConst.D_BOOLEAN_TYPE:
          throw new HdbFailed("D_BOOLEAN_TYPE not supported");
        case HdbConst.D_USHORT_TYPE:
          throw new HdbFailed("D_USHORT_TYPE not supported");
        case HdbConst.D_SHORT_TYPE:
          ret.type = HdbSigInfo.TYPE_SCALAR_SHORT_RO;
          break;
        case HdbConst.D_ULONG_TYPE:
          throw new HdbFailed("D_ULONG_TYPE not supported");
        case HdbConst.D_LONG_TYPE:
          ret.type = HdbSigInfo.TYPE_SCALAR_LONG64_RO;
          break;
        case HdbConst.D_FLOAT_TYPE:
          ret.type = HdbSigInfo.TYPE_SCALAR_FLOAT_RO;
          break;
        case HdbConst.D_DOUBLE_TYPE:
          ret.type = HdbSigInfo.TYPE_SCALAR_DOUBLE_RO;
          break;
        case HdbConst.D_STRING_TYPE:
          ret.type = HdbSigInfo.TYPE_SCALAR_STRING_RO;
          break;
        case HdbConst.D_FLOAT_READPOINT:
          throw new HdbFailed("D_FLOAT_READPOINT not supported");
        case HdbConst.D_STATE_FLOAT_READPOINT:
          throw new HdbFailed("D_STATE_FLOAT_READPOINT not supported");
        case HdbConst.D_LONG_READPOINT:
          throw new HdbFailed("D_LONG_READPOINT not supported");
        case HdbConst.D_DOUBLE_READPOINT:
          throw new HdbFailed("D_DOUBLE_READPOINT not supported");
        case HdbConst.D_VAR_CHARARR:
          ret.type = HdbSigInfo.TYPE_ARRAY_CHAR_RO;
          break;
        case HdbConst.D_VAR_STRINGARR:
          throw new HdbFailed("D_VAR_STRINGARR not supported");
        case HdbConst.D_VAR_USHORTARR:
          throw new HdbFailed("D_VAR_USHORTARR not supported");
        case HdbConst.D_VAR_SHORTARR:
          ret.type = HdbSigInfo.TYPE_ARRAY_SHORT_RO;
          break;
        case HdbConst.D_VAR_ULONGARR:
          throw new HdbFailed("D_VAR_ULONGARR not supported");
        case HdbConst.D_VAR_LONGARR:
          ret.type = HdbSigInfo.TYPE_ARRAY_LONG64_RO;
          break;
        case HdbConst.D_VAR_FLOATARR:
          ret.type = HdbSigInfo.TYPE_ARRAY_FLOAT_RO;
          break;
        case HdbConst.D_VAR_DOUBLEARR:
          ret.type = HdbSigInfo.TYPE_ARRAY_DOUBLE_RO;
          break;
        case HdbConst.D_VAR_FRPARR:
          throw new HdbFailed("D_VAR_FRPARR not supported");
        case HdbConst.D_VAR_SFRPARR:
          throw new HdbFailed("D_VAR_SFRPARR not supported");
        case HdbConst.D_VAR_LRPARR:
          throw new HdbFailed("D_VAR_LRPARR not supported");
        case HdbConst.D_OPAQUE_TYPE:
          throw new HdbFailed("D_OPAQUE_TYPE not supported");
        default:
          throw new HdbFailed("Unknown dataType code " + s.getDataType());

      }

      return ret;

    } catch (Exception e) {
      throw new HdbFailed("tacoHDB.HdbSignal.getDefinition() failed.\n" + attName + "\n" + e.getMessage());
    }

  }

  public ArrayList<HdbSigParam> getParams(String attName, String startDate, String stopDate) throws HdbFailed {
    throw new HdbFailed("getParams() not supported on Oracle HDB");
  }

  public HdbDataSet findErrors(String attName, String startDate, String stopDate) throws HdbFailed {
    return null;
  }

  private String buildMessage(HdbException ex) {
    String msg = "Class:" + ex.getClassName() + "\n" + "Method:" + ex.getMethod() + "\n" + ex.getMessage() + "\n" + ex.getSystemMessage();
    return msg;
  }

}

