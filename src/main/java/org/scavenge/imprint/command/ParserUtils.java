package org.scavenge.imprint.command;

import java.util.Iterator;
import java.util.List;

public class ParserUtils
{
  // switch to guava, which is included
  public static String join(String[] list, String limiter)
  {
    String s = "";    
    for (int i = 0; i < list.length; ++i)
    {
      s += list[i];
      if (i < list.length - 1)
        s += limiter;
    }
    
    return s;
  }
  
  public static String join(List<String> list, String limiter)
  {
    String s = "";
    Iterator<String> it = list.iterator();
    int i = 0;
    
    while (it.hasNext())
    {
      s += it.next();
      if (i < list.size() - 1)
        s += limiter;
      ++i;
    }
    
    return s;
  }
}
