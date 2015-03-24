package org.scavenge.imprint.command;

import java.util.List;

public class Option
{
  private int _id;
  private String _name;
  private boolean _isList = false;
  private int _listMax = -1;
  private boolean _required = false;
  private String _result;
  private String[] _listResult;
    
  public Option(String name, int id)
  {
    this._name = name;
    this._id = id;
  }
  
  public Option setList(boolean isList)
  {
    this._isList = isList;
    return this;
  }
  
  public Option setList(boolean isList, int listMax)
  {
    this._isList = isList;
    this._listMax = listMax;
    return this;
  }
  
  public Option setRequired(boolean required)
  {
    this._required = required;
    return this;
  }
  
  public String[] getListResult()
  {
    return this._listResult;
  }
  
  public String getName()
  {
    return this._name;
  }
  
  public void setResult(String result)
  {
    this._result = result;
  }
  
  public void setResult(String[] result)
  {
    this._listResult = result;
  }
  
  public int getListMax()
  {
    return this._listMax;
  }
  
  public boolean isList()
  {
    return this._isList;
  }
  
  public boolean isRequired()
  {
    return this._required;
  }
  
  public int getId()
  {
    return this._id;
  }
  
  public String getResult()
  {
    return this._result;
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (!(o instanceof Option))
      return false;
    return ((Option) o)._id == this._id;
  }
}
