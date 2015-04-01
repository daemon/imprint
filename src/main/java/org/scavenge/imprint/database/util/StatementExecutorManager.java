package org.scavenge.imprint.database.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.scavenge.imprint.database.ImprintDatabase;

public class StatementExecutorManager implements Runnable
{
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
    System.out.println(this._executor.timingC);
  }

  @Override
  public void run()
  {   
    while (this._running)
    {
      try
      {
        _executor.execute();        
      } catch (SQLException e)
      {
        try
        {
          _executor.setConnection(this._conn = ImprintDatabase.getInstance().getConnection());
        } catch (SQLException e1)
        {
          try
          {
            try
            {
              if (this._conn != null)
                this._conn.close();
            } catch (SQLException e2)
            {
              e2.printStackTrace();
            }
            Thread.sleep(2500);
          } catch (InterruptedException e2)
          {
            return;
          }
          e1.printStackTrace();
        }
      } finally {
        if (this._conn != null)
          try
          {
            this._conn.close();
          } catch (SQLException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
      }
    }
  }  
}
