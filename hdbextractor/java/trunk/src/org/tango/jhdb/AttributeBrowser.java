package org.tango.jhdb;

import java.util.ArrayList;

/**
 * Internal attribute browser
 * (For database that don't allow browsing)
 */

class AttributeBrowser {

  private String name;
  private ArrayList<AttributeBrowser> children;

  public AttributeBrowser(String name) {
    this.name = name;
    children = null;
  }

  public String getName() {
    return name;
  }

  public AttributeBrowser get(int idx) {
    return children.get(idx);
  }

  public AttributeBrowser getBrowser(String name) {

    boolean found = false;
    int i = 0;
    while (!found && i < size()) {
      found = get(i).getName().equalsIgnoreCase(name);
      if (!found) i++;
    }
    if (found)
      return get(i);
    else
      return null;

  }

  public int size() {
    if (children != null)
      return children.size();
    else
      return 0;
  }

  public void addChild(String name) {

    if (children == null)
      children = new ArrayList<AttributeBrowser>();

    // Remove "tango://"
    if (name.startsWith("tango://"))
      name = name.substring(8);

    int i = name.indexOf("/");

    if (i == -1) {

      // Leaf item
      addToList(name);

    } else {

      // Build tree
      String rName = name.substring(0, i);
      String lName = name.substring(i + 1, name.length());
      AttributeBrowser item = addToList(rName);
      item.addChild(lName);

    }

  }

  private AttributeBrowser addToList(String name) {

    boolean found = false;
    int i = 0;
    int comp = -1;

    while (!found && i < children.size()) {
      comp = children.get(i).name.compareToIgnoreCase(name);
      found = comp >= 0;
      if (!found) i++;
    }

    if (comp == 0) {
      return children.get(i);
    } else {
      AttributeBrowser ret = new AttributeBrowser(name);
      children.add(i, ret);
      return ret;
    }

  }

  static AttributeBrowser constructBrowser(HdbReader reader) throws HdbFailed {

    AttributeBrowser browser = new AttributeBrowser("root");
    String[] attList = reader.getAttributeList();
    for (int i = 0; i < attList.length; i++)
      browser.addChild(attList[i]);

    return browser;

  }

  String[] getHosts() throws HdbFailed {

    String[] ret = new String[size()];
    for (int i = 0; i < size(); i++)
      ret[i] = get(i).getName();
    return ret;

  }

  String[] getDomains(String host) throws HdbFailed {

    AttributeBrowser hBrowser = getBrowser(host);
    if (hBrowser == null)
      throw new HdbFailed("host '" + host + "' not defined");

    String[] ret = new String[hBrowser.size()];
    for (int i = 0; i < hBrowser.size(); i++)
      ret[i] = hBrowser.get(i).getName();
    return ret;

  }


  public String[] getFamilies(String host, String domain) throws HdbFailed {

    AttributeBrowser hBrowser = getBrowser(host);
    if (hBrowser == null)
      throw new HdbFailed("host '" + host + "' not defined");
    AttributeBrowser dBrowser = hBrowser.getBrowser(domain);
    if (dBrowser == null)
      throw new HdbFailed("domain '" + domain + "' not defined in '" + host + "'");

    String[] ret = new String[dBrowser.size()];
    for (int i = 0; i < dBrowser.size(); i++)
      ret[i] = dBrowser.get(i).getName();
    return ret;

  }

  public String[] getMembers(String host, String domain, String family) throws HdbFailed {

    AttributeBrowser hBrowser = getBrowser(host);
    if (hBrowser == null)
      throw new HdbFailed("host '" + host + "' not defined");
    AttributeBrowser dBrowser = hBrowser.getBrowser(domain);
    if (dBrowser == null)
      throw new HdbFailed("domain '" + domain + "' not defined in '" + host + "'");
    AttributeBrowser fBrowser = dBrowser.getBrowser(family);
    if (fBrowser == null)
      throw new HdbFailed("family '" + family + "' not defined in '" + host + "/" + domain + "'");

    String[] ret = new String[fBrowser.size()];
    for (int i = 0; i < fBrowser.size(); i++)
      ret[i] = fBrowser.get(i).getName();
    return ret;

  }

  public String[] getNames(String host, String domain, String family, String member) throws HdbFailed {

    AttributeBrowser hBrowser = getBrowser(host);
    if (hBrowser == null)
      throw new HdbFailed("host '" + host + "' not defined");
    AttributeBrowser dBrowser = hBrowser.getBrowser(domain);
    if (dBrowser == null)
      throw new HdbFailed("domain '" + domain + "' not defined in '" + host + "'");
    AttributeBrowser fBrowser = dBrowser.getBrowser(family);
    if (fBrowser == null)
      throw new HdbFailed("family '" + family + "' not defined in '" + host + "/" + domain + "'");
    AttributeBrowser mBrowser = fBrowser.getBrowser(member);
    if (mBrowser == null)
      throw new HdbFailed("member '" + member + "' not defined in '" + host + "/" + domain + "/" + family + "'");

    String[] ret = new String[mBrowser.size()];
    for (int i = 0; i < mBrowser.size(); i++)
      ret[i] = mBrowser.get(i).getName();
    return ret;

  }

}
