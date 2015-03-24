package org.scavenge.imprint.database.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.scavenge.imprint.database.ImprintDatabase;

public class StatementExecutorManager implements Runnable
{
  public long timingC = 0;
  private Thread _thread;
  private Connection _conn;
  private StatementExecutor _executor;
  private boolean _running = true;
  
  public StatementExecutorManager()
  {
    try
    {
      _conn = ImprintDatabase.getInstance().getConnection();
    } catch (SQLException e)
    {
      return;
    }
    
    _executor = new StatementExecutor(this._conn);
    _thread = new Thread(this);
    _thread.start();
  }
  
  public StatementExecutor getExecutor()
  {
    return this._executor;
  }
  
  public void stop()
  {
    this._thread.interrupt();
    this._running = false;
    System.out.println(timingC);
  }

  @Override
  public void run()
  {
    long timingA = System.currentTimeMillis();
    while (this._running)
    {
      try
      {
        _executor.execute();        
      } catch (SQLException e)
      {
        try
        {
          _executor.setConnection(ImprintDatabase.getInstance().getConnection());
        } catch (SQLException e1)
        {
          try
          {
            Thread.sleep(2500);
          } catch (InterruptedException e2)
          {
            return;
          }
          e1.printStackTrace();
        }
      }
      long timingB = System.currentTimeMillis();
      timingC += (timingB - timingA);
    }
  }  
}
