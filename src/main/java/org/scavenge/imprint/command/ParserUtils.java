package org.scavenge.imprint.command;

import java.util.List;

public class ParserUtils
{
  // switch to guava, which is included
  public static String join(List<String> list, String limiter)
  {
    String s = "";
    for (int i = 0; i < list.size(); ++i)
    {
      s += list.get(i);
      if (i < list.size() - 1)
        s += limiter;
    }
    
    return s;
  }
}
