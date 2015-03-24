package org.scavenge.imprint.database.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.scavenge.imprint.database.PlayerIdCache;
import org.scavenge.imprint.database.util.DatabaseUtils;

import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerInteractUpdate extends DatabaseWriteQuery
{
  private PlayerInteractEvent _event;
  private String _queryStr = "INSERT INTO imprint.player_interacts (player_id, xz, y, world_id, item_id, click_type) VALUES (?, GeomFromText(?), ?, ?, ?, ?)";
  public PlayerInteractUpdate(PlayerInteractEvent event)
  {
    this._event = event;
    this.setPrecompiledQuery(this._queryStr);
  }
  
  @Override
  protected PreparedStatement getFinalQuery(PreparedStatement statement)
      throws SQLException
  {
    int playerId = PlayerIdCache.getInstance().lookup(this._event.entityPlayer.getDisplayName());
    if (playerId == PlayerIdCache.NOT_FOUND)
    {
      playerId = DatabaseUtils.updatePlayerId(this._event.entityPlayer.getDisplayName());
      PlayerIdCache.getInstance().put(this._event.entityPlayer.getDisplayName(), playerId);
    }
    
    statement.setInt(1, playerId);
    statement.setString(2, "Point(" + _event.x + " " + _event.z + ")");
    statement.setInt(3, _event.y);
    statement.setInt(4, this._event.world.provider.dimensionId);
    statement.setInt(5, Item.getIdFromItem(this._event.entityPlayer.getHeldItem().getItem()));
    statement.setInt(6, this._event.action.ordinal());
    
    return statement;
  }
}
