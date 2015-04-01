package org.scavenge.imprint;

public class ImprintConfig
{
  private static ImprintConfig instance = createDefault();
  public final String CMD_NAME;
  public final String DB_HOSTNAME;
  public final int DB_PORT;
  public final String DB_USER;
  public final String DB_PASSWORD;
    
  ImprintConfig(String cmdLookupName, String hostname, int port, String user, String password)
  {
    this.CMD_NAME = cmdLookupName;
    this.DB_PORT = port;
    this.DB_USER = user;
    this.DB_PASSWORD = password;
    this.DB_HOSTNAME = hostname;
  }
  
  public static ImprintConfig getInstance()
  {
    return instance;
  }
  
  public static void loadDefaultFile()
  {
    
  }
  
  private static ImprintConfig createDefault()
  {
    return new ImprintConfig("imprint", "scavenge.org", 3306, "imprint", "ralphtango");
  }
}
