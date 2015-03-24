package org.scavenge.imprint.database.statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlockIdQuery extends DatabaseReadQuery
{
  private int _blockId;
  private int _metadata;
  private int _rowId;
  private static final String _queryStr = "SELECT * FROM imprint.blocks WHERE `block_id`=? AND `block_metadata`=?";
    
  public BlockIdQuery(int blockId, int blockMetadata) throws SQLException
  {
    this.setPrecompiledQuery(this._queryStr);
    this._blockId = blockId;
    this._metadata = blockMetadata;
  }
  
  @Override
  public boolean equals(Object o)
  {
    BlockIdQuery query = null;
    if (o instanceof BlockIdQuery)
      query = (BlockIdQuery) o;
    else
      return false;
    
    return query._metadata == this._metadata && query._blockId == this._blockId;
  }
  
  @Override
  public int hashCode()
  {
    return (this._queryStr.hashCode() << 4) ^ (this._queryStr.hashCode() >> 28) ^ this._blockId; 
  }
  
  public int getRowId()
  {
    return this._rowId;
  }
  
  @Override
  protected PreparedStatement getFinalQuery(PreparedStatement statement) throws SQLException
  {
    statement.setInt(1, this._blockId);
    statement.setInt(2, this._metadata);

    return statement;
  }
  
  @Override
  public void onResultSet(ResultSet set) throws SQLException
  {
    if (set.next())
      this._rowId = set.getInt(1);
  }
}
