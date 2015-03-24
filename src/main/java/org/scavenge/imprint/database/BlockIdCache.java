package org.scavenge.imprint.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockIdCache
{
  public static int NOT_FOUND = -1;
  private static int _capacity = 2048;
  private static BlockIdCache _instance;
  
  private HashMap<List<Integer>, Integer> _map = new HashMap<List<Integer>, Integer>();
  
  public void put(final int blockId, final int metadata, int id)
  {
    List<Integer> mapping = new ArrayList<Integer>() {{ add(blockId); add(metadata); }};
    this._map.put(mapping, id);
  }
  
  public int lookup(final int blockId, final int metadata)
  {
    List<Integer> mapping = new ArrayList<Integer>() {{ add(blockId); add(metadata); }};
    Integer id = this._map.get(mapping);
    if (_map.size() > _capacity)
      _map.clear();
    
    return id == null ? NOT_FOUND : id; 
  }
  
  public static BlockIdCache getInstance()
  {
    if (_instance == null)
      _instance = new BlockIdCache();
    return _instance;
  }
}
