package org.scavenge.imprint.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.scavenge.imprint.database.ImprintDatabase;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DatabaseBenchmark
{
  public static void benchmarkPlayerInteractInsert(Connection conn, int n) throws SQLException
  {
    PreparedStatement statement = conn.prepareStatement("INSERT INTO imprint.player_interacts "
        + "(`player_id`, "
        + "`xz`, "
        + "`y`, "
        + "`world_id`, "
        + "`item_id`, "
        + "`click_type`) VALUES (?, GeomFromText(?), ?, ?, ?, ?)");
    
    int[] itemIds = new int[n];
    int[] worldIds = new int[n];
    int[] playerIds = new int[n];
    int[] clickTypes = new int[n];
    String[] points = new String[n];
    int[] yCoords = new int[n];
    
    for (int i = 0; i < n; ++i)
    {
      int itemId = (int) (Math.random() * 10000);
      int worldId = (int) (Math.random() * 5);
      int clickType = (int) (Math.random() * 4);
      int playerId = (int) (Math.random() * 10000);
      int x = (int) (Math.random() * 6000) - 3000;
      int z = (int) (Math.random() * 6000) - 3000;
      int y = (int) (Math.random() * 150);
      
      itemIds[i] = itemId;
      playerIds[i] = playerId;
      worldIds[i] = worldId;
      clickTypes[i] = clickType;
      
      points[i] = "POINT(" + x + " " + z + ")";
      yCoords[i] = y;
    }
    
    long timingA = System.currentTimeMillis();
    for (int i = 0; i < n; ++i)
    {
      statement.setInt(1, playerIds[i]);
      statement.setString(2, points[i]);
      statement.setInt(3, yCoords[i]);
      statement.setInt(4, worldIds[i]);
      statement.setInt(5, itemIds[i]);
      statement.setInt(6, clickTypes[i]);
      
      statement.executeUpdate();
    }
    
    long timingB = System.currentTimeMillis();
    System.out.println(n + " inserts lasted " + (timingB - timingA) + " ms");
  }
  
  public static void benchmarkBlockBreakInsert(Connection conn, int n) throws SQLException
  {
    PreparedStatement statement = conn.prepareStatement("INSERT INTO imprint.block_breaks "
        + "(`block_id`, "
        + "`player_id`, "
        + "`type_id`, "
        + "`xz`, "
        + "`y`) VALUES (?, ?, ?, GeomFromText(?), ?)");
    
    int[] blockIds = new int[n];
    int[] playerIds = new int[n];
    int[] typeIds = new int[n];
    String[] points = new String[n];
    int[] yCoords = new int[n];
    
    for (int i = 0; i < n; ++i)
    {
      int blockId = (int) (Math.random() * 10000);
      int playerId = (int) (Math.random() * 10000);
      int typeId = (int) (Math.random() * 10);
      int x = (int) (Math.random() * 6000) - 3000;
      int z = (int) (Math.random() * 6000) - 3000;
      int y = (int) (Math.random() * 150);
      
      blockIds[i] = blockId;
      playerIds[i] = playerId;
      typeIds[i] = typeId;
      points[i] = "POINT(" + x + " " + z + ")";
      yCoords[i] = y;
    }
    
    long timingA = System.currentTimeMillis();
    for (int i = 0; i < n; ++i)
    {
      statement.setInt(1, blockIds[i]);
      statement.setInt(2, playerIds[i]);
      statement.setInt(3, typeIds[i]);
      statement.setString(4, points[i]);      
      statement.setInt(5, yCoords[i]);
      
      statement.executeUpdate();
    }
    
    long timingB = System.currentTimeMillis();
    System.out.println(n + " inserts lasted " + (timingB - timingA) + " ms");
  }
  
  public static void main(String[] args)
  {
    /*if (args.length == 0)
      return;*/
    args[0] = "1000";
    
    int nTimes = Integer.parseInt(args[0]);
    
    ImprintDatabase.getInstance().init();
    ComboPooledDataSource src = ImprintDatabase.getInstance().getDataSource();
    Connection conn = null;
    try
    {
      conn = src.getConnection();
      benchmarkPlayerInteractInsert(conn, nTimes);
    } catch (SQLException e)
    {
      System.err.println("Acquiring connection failed.");
      e.printStackTrace();
    } finally {
      try
      {
        if (conn != null)
          conn.close();
      } catch (SQLException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
