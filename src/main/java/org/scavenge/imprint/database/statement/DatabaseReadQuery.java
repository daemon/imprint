package org.scavenge.imprint.database.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DatabaseReadQuery extends DatabaseQuery
{
  private ResultSet _resultSet;
  private int _resultSize;
    
  public ResultSet getResultSet() 
  {
    return this._resultSet;
  }
  
  public abstract void onResultSet(ResultSet set) throws SQLException;
    
  @Override
  public void execute() throws SQLException
  {
    this.execute(this.makePrecompiledQuery());    
  }
  
  public int getResultSize()
  {
    return this._resultSize;
  }
  
  @Override
  public void execute(PreparedStatement statement) throws SQLException
  {
    this._resultSet = this.getFinalQuery(statement).executeQuery();
    this._resultSize = this._resultSet.getFetchSize();
    this.onResultSet(this._resultSet);
  }
}
