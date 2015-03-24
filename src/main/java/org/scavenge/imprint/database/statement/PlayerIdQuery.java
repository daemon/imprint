package org.scavenge.imprint.database.statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerIdQuery extends DatabaseReadQuery
{
  private String _name;
  private int _rowId;
  private static final String _queryStr = "SELECT id FROM imprint.players WHERE `name`=?";
  
  public PlayerIdQuery(String name) throws SQLException
  {
    this.setPrecompiledQuery(this._queryStr);
    this._name = name;
  }
  
  public int getRowId()
  {
    return this._rowId;
  }

  @Override
  protected PreparedStatement getFinalQuery(PreparedStatement statement) throws SQLException
  {
    statement.setString(1, this._name);        
    return statement;
  }

  @Override
  public void onResultSet(ResultSet set) throws SQLException
  {
    if (set.next())
      this._rowId = set.getInt(1);
  }

  @Override
  public boolean equals(Object o)
  {
    if (!(o instanceof PlayerIdQuery))
      return false;
    PlayerIdQuery query = (PlayerIdQuery) o;
    return query._name.equals(this._name);
  }

  @Override
  public int hashCode()
  {
    return (_queryStr.hashCode() << 4) ^ (_queryStr.hashCode() >> 28) ^ this._name.hashCode();
  }
}
