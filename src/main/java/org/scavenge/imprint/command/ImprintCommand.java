package org.scavenge.imprint.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scavenge.imprint.ImprintConfig;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public abstract class ImprintCommand implements ICommand
{
  private static List<String> commandNames = new LinkedList<String>(); 
  public abstract void onCommand(ICommandSender sender, List<Option> options);
  public abstract String getName();
  public abstract String getUsage();
  
  public static void register(int id, ImprintCommand cmd, String... prefixes)
  {
    CommandParser.getMasterParser().register(id, cmd, prefixes);
    commandNames.add(cmd.getName());
  }
  
  @Override
  public int compareTo(Object arg0)
  {
    return 0;
  }
  
  @Override
  public List getCommandAliases()
  {
    return new ArrayList(); // TODO change later
  }
  
  @Override
  public String getCommandName()
  {
    return ImprintConfig.getInstance().CMD_NAME;
  }
  
  @Override
  public String getCommandUsage(ICommandSender sender)
  {
    final String s = ParserUtils.join(commandNames, ", ");
    return "/" + getCommandName() + " " + s;
  }
  
  @Override
  public void processCommand(ICommandSender sender, String[] args)
  {
    try
    {
      CommandParser.getMasterParser().parse(sender, args);
    } catch (IllFormedCommandException e)
    {
      ImprintCommand command;      
      sender.addChatMessage(new ChatComponentText(e.getMessage()));
      
      if ((command = CommandParser.getMasterParser().getHandler(args[0])) != null)
        sender.addChatMessage(new ChatComponentText(command.getUsage()));        
    }
  }
  
  @Override
  public boolean canCommandSenderUseCommand(ICommandSender sender)
  {
    return true;
  }
  
  @Override
  public List addTabCompletionOptions(ICommandSender p_71516_1_,
      String[] p_71516_2_)
  {
    return null;
  }
  
  @Override
  public boolean isUsernameIndex(String[] args, int i)
  {
    return false;
  }
}
