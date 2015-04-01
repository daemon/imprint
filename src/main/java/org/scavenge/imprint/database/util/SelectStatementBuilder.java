package org.scavenge.imprint.database.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.scavenge.imprint.command.ParserUtils;

public class SelectStatementBuilder
{
  private final String _table;
  private final List<String> _retrieveColumnNames = new LinkedList<String>();
  private final List<String> _conditions = new LinkedList<String>();
  private final List<LogicalOperands> _whereLogicalOps = new LinkedList<LogicalOperands>();
  private boolean _retrieveAllColumns = false;
  
  public enum LogicalOperands
  {
    AND, OR
  }
  
  public SelectStatementBuilder(String table)
  {
    this._table = table;
  }
  
  public SelectStatementBuilder addRetrieveColumn(String name)
  {
    this._retrieveColumnNames.add(name);
    return this;
  }
  
  public SelectStatementBuilder addWhereCondition(LogicalOperands operand, String condition)
  {
    this._whereLogicalOps.add(operand);
    this._conditions.add(condition);
    return this;
  }
  
  public SelectStatementBuilder setRetrieveColumnAll()
  {
    this._retrieveAllColumns = true;
    return this;
  }
  
  public String getStatement()
  {
    String preStr = "SELECT " + (this._retrieveAllColumns ? "*" : 
      "(" + ParserUtils.join(this._retrieveColumnNames, ",") + ")") + " FROM " + this._table;
    preStr += " WHERE";
    
    Iterator<String> it = this._conditions.iterator();
    Iterator<LogicalOperands> it2 = this._whereLogicalOps.iterator();
    for (int i = 0; it.hasNext(); ++i)
    {
      preStr += " " + it.next();
      if (i != this._conditions.size() - 1)
        preStr += " " + it2.next();
    }
    
    return preStr;
  }
}
