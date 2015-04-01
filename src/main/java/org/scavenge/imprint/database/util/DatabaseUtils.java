package org.scavenge.imprint.database.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.scavenge.imprint.database.ImprintDatabase;
import org.scavenge.imprint.database.statement.BlockIdQuery;
import org.scavenge.imprint.database.statement.BlockIdUpdate;
import org.scavenge.imprint.database.statement.PlayerIdQuery;
import org.scavenge.imprint.database.statement.PlayerIdUpdate;

import net.minecraft.block.Block;

public class DatabaseUtils
{  
  public static int updateBlockId(int blockId, int blockMetadata) throws SQLException
  {
    ImprintDatabase db = ImprintDatabase.getInstance();
    db.executeBlocking(new BlockIdUpdate(blockId, blockMetadata));
    
    BlockIdQuery query;
    db.executeBlocking(query = new BlockIdQuery(blockId, blockMetadata));
    
    return query.getRowId();
  }
      
  public static int updatePlayerId(String playerName) throws SQLException
  {
    ImprintDatabase db = ImprintDatabase.getInstance();
    db.executeBlocking(new PlayerIdUpdate(playerName));
        
    PlayerIdQuery query = new PlayerIdQuery(playerName);
    db.executeBlocking(query);
    
    return query.getRowId();
  }
}
