package org.scavenge.imprint.database.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.minecraft.block.Block;

public class BlockIdUpdate extends DatabaseWriteQuery
{
  private static final String _queryStr = "INSERT INTO imprint.blocks ("
      + "block_id, "
      + "block_metadata) VALUES (?, ?) ON DUPLICATE KEY UPDATE block_id=block_id";
  private final int _blockId;
  private final int _blockMetadata;
  
  public BlockIdUpdate(int blockId, int metadata) throws SQLException
  {
    this.setPrecompiledQuery(_queryStr);
    this._blockId = blockId;
    this._blockMetadata = metadata;
  }

  @Override
  protected PreparedStatement getFinalQuery(PreparedStatement statement) throws SQLException
  {
    statement.setInt(1, this._blockId);
    statement.setInt(2, this._blockMetadata);
        
    return statement;
  }
}
