package org.scavenge.imprint.database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.scavenge.imprint.ImprintConfig;
import org.scavenge.imprint.database.statement.DatabaseQuery;
import org.scavenge.imprint.database.util.StatementExecutor;
import org.scavenge.imprint.database.util.StatementExecutorManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.cfg.C3P0Config;

public class ImprintDatabase
{
  private static ImprintDatabase instance;
  
  private String _ip;
  private int _port;
  private String _user;
  private String _password;
  private StatementExecutorManager _executor;
  
  private ComboPooledDataSource _source;
  private String _connectJdbcUrl;
  
  public ImprintDatabase(String ip, int port, String username, String password)
  {
    this._ip = ip;
    this._port = port;
    this._user = username;
    this._password = password;
    this._connectJdbcUrl = "jdbc:mysql://" + this._ip + ":" + this._port + "/";
  }
  
  public ComboPooledDataSource getDataSource()
  {
    return this._source;
  }
  
  public void execute(DatabaseQuery query)
  {
    this.getExecutor().addQuery(query);
  }
  
  public void executeWaiting(DatabaseQuery query)
  {
    // TODO timeout
    boolean done = false;
    while (!done)
    {
      try
      {
        this.getExecutor().addWaitingQuery(query);
        done = true;
      } catch (SQLException e)
      {
        try
        {
          this.getExecutor().setConnection(this.getConnection());
        } catch (SQLException e1) {}      
      }
    }
  }
  
  public Connection getConnection() throws SQLException
  {
    Connection conn = null;
    conn = this._source.getConnection();

    return conn;
  }
  
  public StatementExecutor getExecutor()
  {
    return this._executor.getExecutor();
  }
  
  public StatementExecutorManager getExecutorManager()
  {
    return this._executor;
  }
  
  public static ImprintDatabase getInstance()
  {
    ImprintConfig cfg = ImprintConfig.getInstance();
    if (instance == null)
      instance = new ImprintDatabase(cfg.DB_HOSTNAME, cfg.DB_PORT, 
          cfg.DB_USER, cfg.DB_PASSWORD);
    
    return instance;
  }
  
  private void createTables(Connection conn)
  {
    try
    {
      conn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS imprint");
      conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS imprint.players (`id` MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT, `name` CHAR(64) NOT NULL, PRIMARY KEY(`id`), INDEX `name_i` (name), UNIQUE `pname_uniq` (name)) ENGINE = MYISAM");
      conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS imprint.blocks (`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, `block_id` SMALLINT UNSIGNED NOT NULL, `block_metadata` TINYINT NOT NULL, `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(`id`), UNIQUE `block_uniq` (block_id, block_metadata), INDEX `block_id_i` (block_id)) ENGINE = MYISAM");
//      conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS imprint.worlds (`id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, `name` CHAR(32) NOT NULL, PRIMARY KEY(`id`), INDEX world_name_i (name)) ENGINE = MYISAM");
      //conn.createStatement().executeQuery("CREATE TABLE IF NOT EXISTS imprint.history (`id` INT NOT NULL AUTO_INCREMENT,   PRIMARY KEY(`id`), )");
      conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS imprint.block_breaks (`id` INT NOT NULL AUTO_INCREMENT, `block_id` INT NOT NULL, `player_id` INT NOT NULL, xz POINT NOT NULL, y SMALLINT NOT NULL, world_id SMALLINT NOT NULL, `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(`id`), SPATIAL INDEX `xz_i` (`xz`)) ENGINE = MYISAM");
      conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS imprint.block_places (`id` INT NOT NULL AUTO_INCREMENT, `block_id` INT NOT NULL, `player_id` INT NOT NULL, xz POINT NOT NULL, y SMALLINT NOT NULL, world_id SMALLINT NOT NULL, `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(`id`), SPATIAL INDEX `xz_i` (`xz`)) ENGINE = MYISAM");
      conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS imprint.player_interacts (`id` INT NOT NULL AUTO_INCREMENT, `player_id` INT NOT NULL, xz POINT NOT NULL, y SMALLINT NOT NULL, world_id SMALLINT NOT NULL, `item_id` INT NOT NULL, `click_type` TINYINT NOT NULL, `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(`id`), SPATIAL INDEX `xz_i` (`xz`)) ENGINE = MYISAM");      
      //conn.createStatement().executeQuery("CREATE TABLE IF NOT EXISTS imprint.block_breaks (`id` INT NOT NULL AUTO_INCREMENT, `block_id` INT NOT NULL, PRIMARY KEY(`id`))");
    } catch (SQLException e)
    {
      System.err.println("Inconsistent database state.");
      e.printStackTrace();
    }
  }
  
  public void init()
  {
    Connection conn = null;
    try
    {
      this._source = new ComboPooledDataSource();
      this._source.setDriverClass("com.mysql.jdbc.Driver");
      this._source.setJdbcUrl(this._connectJdbcUrl);
      this._source.setUser(this._user);
      this._source.setPassword(this._password);
                  
      this._source.setMinPoolSize(8);
      this._source.setMaxPoolSize(32);
      // this._source.setTestConnectionOnCheckin(false);
      this._source.setMaxStatements(128);
      this._source.setMaxStatementsPerConnection(16);
      this._source.setInitialPoolSize(8);
      conn = this._source.getConnection();
      this.createTables(conn);
      this._executor = new StatementExecutorManager();
    } catch (PropertyVetoException e)
    {
      System.err.println("No MySQL bindings/jdbc!");
      e.printStackTrace();
    } catch (SQLException e)
    {
      System.err.println("Cannot connect to database with supplied parameters.");
      e.printStackTrace();
    } finally {
      if (conn != null)
        try
        {
          conn.close();
        } catch (SQLException e)
        {
          System.err.println("Unknown database error. Maybe server blew up?");
          e.printStackTrace();
        }
    }
  }
}
