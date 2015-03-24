package org.scavenge.imprint.database.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.scavenge.imprint.database.ImprintDatabase;

public abstract class DatabaseQuery
{
  private boolean initDefault = true;
  private PreparedStatement _statement;
  private Connection _conn;
  private String _precompQuery;
  private boolean _initConn = false;
  
  protected abstract PreparedStatement getFinalQuery(PreparedStatement prepared) throws SQLException;
  public abstract void execute() throws SQLException;
  public abstract void execute(PreparedStatement statement) throws SQLException;
  
  protected void initConnection() throws SQLException
  {
    if (this._conn != null)
      return;
    
    this._initConn = true;
    if (initDefault)
      this._conn = ImprintDatabase.getInstance().getConnection();
  }
  
  @Override
  public boolean equals(Object o)
  {
    return false;
  }
  
  @Override
  public int hashCode()
  {
    return 0;
  }

  protected Connection getCurrentConnection()
  {
    return this._conn;
  }
  
  public void setConnection(Connection connection) throws SQLException
  {
    this.initDefault = false;
    this._conn = connection;
  }
    
  // TODO set error flag after close or something
  public void close()
  {
    if (this._statement == null)
      return;
    if (this._conn == null)
      return;
    try
    {
      if (this.initDefault)
        this._conn.close();
      this._statement.close();
    } catch (SQLException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public String getPreparedStatementStr()
  {
    return this._precompQuery;
  }
  
  protected PreparedStatement makePrecompiledQuery() throws SQLException
  {
    if (!this._initConn || this._conn == null)
      initConnection();
    if (this._precompQuery == null || this._conn == null)
      return null;
    
    this._statement = _conn.prepareStatement(this._precompQuery);        
    return this._statement;
  }
  
  protected void setPrecompiledQuery(String str)
  {
    this._precompQuery = str;
  }
}
