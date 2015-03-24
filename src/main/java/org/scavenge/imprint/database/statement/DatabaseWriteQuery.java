package org.scavenge.imprint.database.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DatabaseWriteQuery extends DatabaseQuery
{
  @Override
  public void execute() throws SQLException
  {
    this.execute(this.makePrecompiledQuery());    
  }
  
  @Override
  public void execute(PreparedStatement statement) throws SQLException
  {
    this.getFinalQuery(statement).execute();
  }

}
