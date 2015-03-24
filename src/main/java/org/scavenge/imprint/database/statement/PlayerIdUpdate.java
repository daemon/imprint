package org.scavenge.imprint.database.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.minecraft.block.Block;

public class PlayerIdUpdate extends DatabaseWriteQuery
{
  private static final String _queryStr = "INSERT INTO imprint.players ("
      + "name) VALUES (?) ON DUPLICATE KEY UPDATE name=name";
  
  private final String _name;
  
  public PlayerIdUpdate(String name) throws SQLException
  {
    this.setPrecompiledQuery(_queryStr);
    this._name = name;    
  }

  @Override
  protected PreparedStatement getFinalQuery(PreparedStatement statement) throws SQLException
  {
    statement.setString(1, this._name);            
    return statement;
  }
}