package org.scavenge.imprint;

import net.minecraftforge.common.MinecraftForge;

public class ServerProxy extends CommonProxy
{
  public final Test test = new Test();
  
  @Override
  public void init()
  {
    MinecraftForge.EVENT_BUS.register(test);
  }
}
