package org.scavenge.imprint.database;

import java.util.HashMap;

public class PlayerIdCache
{
  public static int NOT_FOUND = -1;
  private static int _capacity = 512;
  private static PlayerIdCache _instance;
  private HashMap<String, Integer> _map = new HashMap<String, Integer>();
  
  public void put(String playerName, int id)
  {
    this._map.put(playerName, id);
  }
  
  public int lookup(String playerName)
  {
    Integer id = this._map.get(playerName);
    if (_map.size() > _capacity)
      _map.clear();
    
    return id == null ? NOT_FOUND : id; 
  }
  
  public static PlayerIdCache getInstance()
  {
    if (_instance == null)
      _instance = new PlayerIdCache();
    return _instance;
  }
}
