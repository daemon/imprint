package org.scavenge.imprint.database.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.scavenge.imprint.database.statement.DatabaseQuery;

public class StatementExecutor
{
  private Map<String, PreparedStatement> _preparedStrToStatement;
  private Queue<DatabaseQuery> _queries;
  
  private Lock _queriesLock = new ReentrantLock();
  private Condition _queriesNotEmpty = _queriesLock.newCondition();
  
  private Connection _conn;
  private volatile boolean _executing = false;
  private final int _capacity = 512;
  private final int _queueCapacity = 100000;
  
  public StatementExecutor(Connection conn)
  {
    this._preparedStrToStatement = new ConcurrentHashMap<String, PreparedStatement>();
    this._queries = new LinkedBlockingQueue<DatabaseQuery>(_queueCapacity);
    this._conn = conn;
  }
  
  public void setConnection(Connection conn)
  {
    this._preparedStrToStatement.clear();
    this._conn = conn;
  }
  
  public void reset()
  {
    this._conn = null;
    this._preparedStrToStatement.clear();
  }
  
  public void addQuery(DatabaseQuery query)
  {
    this._queries.add(query);
    
    if (!this._executing)
    {
      this._queriesLock.lock();
      this._queriesNotEmpty.signal();
      this._queriesLock.unlock();
    }
  }
  
  public void addWaitingQuery(DatabaseQuery query) throws SQLException
  {
    query.setConnection(this._conn);
    
    // TODO: DRY
    PreparedStatement statement = null;
    String queryStr = query.getPreparedStatementStr();
    if (!this._preparedStrToStatement.containsKey(queryStr))
      this._preparedStrToStatement.put(queryStr, statement = this._conn.prepareStatement(queryStr));
    else
      statement = this._preparedStrToStatement.get(queryStr);
    
    if (this._preparedStrToStatement.size() > this._capacity)
      this._preparedStrToStatement.remove(this._preparedStrToStatement.keySet().iterator().next());
    
    query.execute(statement);
  }
  
  public void execute() throws SQLException
  {
    this._queriesLock.lock();
    this._executing = false;
    try {
      while (this._queries.isEmpty())
        try
        {
          this._queriesNotEmpty.await();
        } catch (InterruptedException e)
        {
          this._queriesLock.unlock();
          return;
        }
      
      this._executing = true;
      while(!this._queries.isEmpty())
      {
        DatabaseQuery query = this._queries.poll();
        query.setConnection(this._conn);
        
        PreparedStatement statement = null;
        String queryStr = query.getPreparedStatementStr();
        if (!this._preparedStrToStatement.containsKey(queryStr))
          this._preparedStrToStatement.put(queryStr, statement = this._conn.prepareStatement(queryStr));
        else
          statement = this._preparedStrToStatement.get(queryStr);
        
        if (this._preparedStrToStatement.size() > this._capacity)
          this._preparedStrToStatement.remove(this._preparedStrToStatement.keySet().iterator().next());
                
        query.execute(statement);                
        //statement.close();
      }
    } finally {
      if (this._executing)
        this._queriesLock.unlock();
      this._executing = false;
    }    
  }
}
