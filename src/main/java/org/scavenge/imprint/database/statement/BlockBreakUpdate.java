package org.scavenge.imprint.database.statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.scavenge.imprint.database.BlockIdCache;
import org.scavenge.imprint.database.PlayerIdCache;
import org.scavenge.imprint.database.util.DatabaseUtils;

import net.minecraft.block.Block;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class BlockBreakUpdate extends DatabaseWriteQuery
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
    super();
    this._event = event;
    this.setPrecompiledQuery(_preparedStatementStr);
  }
  
  protected PreparedStatement getFinalQuery(PreparedStatement statement) throws SQLException
  {
    int id = Block.getIdFromBlock(this._event.block), metadata = this._event.blockMetadata;           
    int blockRowId = BlockIdCache.getInstance().lookup(id, metadata);
    
    if (blockRowId == BlockIdCache.NOT_FOUND)
    {
      blockRowId = DatabaseUtils.updateBlockId(id, metadata);
      BlockIdCache.getInstance().put(id, metadata, blockRowId);
    }
    
    int playerRowId = PlayerIdCache.getInstance().lookup(this._event.getPlayer().getDisplayName());
    
    if (playerRowId == PlayerIdCache.NOT_FOUND)
    {
      playerRowId = DatabaseUtils.updatePlayerId(this._event.getPlayer().getDisplayName());
      PlayerIdCache.getInstance().put(this._event.getPlayer().getDisplayName(), playerRowId);
    }    
    
    statement.setInt(1, blockRowId);
    statement.setInt(2, playerRowId);
    statement.setString(3, "POINT(" + this._event.x + " " + this._event.z + ")");
    statement.setInt(4, this._event.y);
    statement.setInt(5, this._event.world.provider.dimensionId);
    
    return statement;
  }
}
