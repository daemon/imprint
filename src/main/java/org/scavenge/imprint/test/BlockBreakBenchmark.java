package org.scavenge.imprint.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.scavenge.imprint.database.BlockIdCache;
import org.scavenge.imprint.database.ImprintDatabase;
import org.scavenge.imprint.database.PlayerIdCache;
import org.scavenge.imprint.database.statement.DatabaseWriteQuery;
import org.scavenge.imprint.database.util.DatabaseUtils;

public class BlockBreakBenchmark
{
  public static void main(String[] args) throws SQLException, InterruptedException
  {
    if (args.length == 1)
      return;
    
    String nTimesStr = "100000";//args[0];
    
    int nTimes = Integer.parseInt(nTimesStr);
    ImprintDatabase.getInstance().init();
    BreakEvent[] events = new BreakEvent[nTimes];
    String[] names = new String[50];
    String[] basePrefix = {"cool", "boring", "lazy"};
    String[] middlePrefix = {"cat", "zebra", "narwhal"};
        
    for (int i = 0; i < names.length; ++i)
    {
      names[i] = basePrefix[(int) (Math.random() * 3)] + middlePrefix[(int) (Math.random() * 3)] + (int) (Math.random() * 4);
    }
            
    for (int i = 0; i < nTimes; ++i)
    {
      int dim = (int) (Math.random() * 3);
      int blockId = (int) (Math.random() * 200);
      int metadata = (int) (Math.random() * 4);
      String name = names[(int) (Math.random() * 50)];
      int x = (int) ((Math.random() * 30000) - 15000);
      int z = (int) ((Math.random() * 30000) - 15000);
      int y = (int) (Math.random() * 128);
      
      events[i] = new BreakEvent(dim, blockId, metadata, name, x, y, z);
    }
    
    long timingA = System.currentTimeMillis();
    
    for (int i = 0; i < nTimes; ++i)
    {
      ImprintDatabase.getInstance().execute(new BlockBreakUpdate(events[i]));
    }
    
    long timingB = System.currentTimeMillis();
    
    System.out.println("Block breaking benchmark lasted " + (timingB - timingA) + " ms");
    Thread.sleep((long) (nTimes * 1.5));
    ImprintDatabase.getInstance().getExecutorManager().stop();
  }
  
  public static class BreakEvent
  {
    public final int dimId, id, metadata, x, y, z;
    public final String name;
    
    public BreakEvent(int dimId, int id, int metadata, String name, int x, int y, int z)
    {
      this.dimId = dimId;
      this.id = id;
      this.metadata = metadata;
      this.name = name;
      this.x = x;
      this.y = y;
      this.z = z;
    }
  }
  
  public static class BlockBreakUpdate extends DatabaseWriteQuery
  {
    private BreakEvent _event;
    private static final String _preparedStatementStr = "INSERT INTO imprint.block_breaks "
        + "(`block_id`, "
        + "player_id, "
        + "`xz`, "
        + "`y`, "
        + "world_id) VALUES (?, ?, GeomFromText(?), ?, ?)";
    
    public BlockBreakUpdate(BreakEvent event) throws SQLException
    {
      this._event = event;
      this.setPrecompiledQuery(_preparedStatementStr);
    }
    
    protected PreparedStatement getFinalQuery(PreparedStatement statement) throws SQLException
    {      
      int id = this._event.id, metadata = this._event.metadata;
      
      int blockRowId = BlockIdCache.getInstance().lookup(id, metadata);
      
      if (blockRowId == BlockIdCache.NOT_FOUND)
      {
        blockRowId = DatabaseUtils.updateBlockId(id, metadata);
        BlockIdCache.getInstance().put(id, metadata, blockRowId);
      }
      
      int playerRowId = PlayerIdCache.getInstance().lookup(this._event.name);
      
      if (playerRowId == PlayerIdCache.NOT_FOUND)
      {
        playerRowId = DatabaseUtils.updatePlayerId(this._event.name);
        PlayerIdCache.getInstance().put(this._event.name, playerRowId);
      }
      
      statement.setInt(1, blockRowId);
      statement.setInt(2, playerRowId);
      statement.setString(3, "POINT(" + this._event.x + " " + this._event.z + ")");
      statement.setInt(4, this._event.y);
      statement.setInt(5, this._event.dimId);
      
      return statement;
    }
  }
}
