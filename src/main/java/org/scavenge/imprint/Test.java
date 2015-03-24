package org.scavenge.imprint;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class Test
{
  @SubscribeEvent
  public void test(BreakEvent event)
  {
    System.out.println(event.x + " " + event.y + " " + event.z + " " + event.getPlayer().getDisplayName());
  }
  
}
