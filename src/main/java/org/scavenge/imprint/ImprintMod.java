package org.scavenge.imprint;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(version = "0.0.1", modid = ImprintMod.MOD_ID, name = ImprintMod.NAME, acceptableRemoteVersions = "*")
public class ImprintMod
{
  public static final String MOD_ID = "imprint";
  public static final String NAME = "imprint";
  public static final String VERSION = "0.0.1"; 
  
  @Instance(value = ImprintMod.MOD_ID)
  public static ImprintMod instance;
  
  @SidedProxy(clientSide = "org.scavenge.imprint.CommonProxy", serverSide = "org.scavenge.imprint.ServerProxy")
  public static CommonProxy proxy;
  
  @EventHandler
  public void init(FMLInitializationEvent event)
  {
    proxy.init();
  }
  
  @EventHandler
  public void serverInit(FMLServerStartingEvent event)
  {
  }
}
