package org.scavenge.imprint.command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;

public class CommandParser
{
  private static CommandParser _instance = null;
  private final Pattern _CMD_COLON;
  
  private boolean _isMaster;
  private final String _cmdName;
  private int _id;
  private final String _cmdPrefix;
  private final Map<String, Option> _namesToOptions = new HashMap<String, Option>();
  private final Map<String, Option> _namesToRequiredOptions = new HashMap<String, Option>();
  private final Map<String, CommandParser> _namesToParsers = new HashMap<String, CommandParser>(); 
  private static Map<Integer, ImprintCommand> _intsToHandlers = new HashMap<Integer, ImprintCommand>();
  
  // TODO: split master and slave...
  private CommandParser(String name, String prefix, boolean isMaster, int id)
  {
    this._CMD_COLON = Pattern.compile("^(.+?):(.+)$");
    this._cmdName = name;
    this._cmdPrefix = prefix;
    this._isMaster = isMaster;
    this._id = id;
  }
  
  public static CommandParser getMasterParser()
  {
    if (_instance == null)
      _instance = new CommandParser("imprint", "", true, 0);
    return _instance;
  }
  
  public static CommandParser createSubParser(String name, String prefix, int id)
  {
    return new CommandParser(name, prefix, false, id);
  }
  
  public static CommandParser createSubRootParser(String name, String prefix, int id)
  {
    return new CommandParser(name, prefix, true, id);
  }
  
  public CommandParser addSubParser(CommandParser parser)
  {
    this._namesToParsers.put(parser._cmdPrefix, parser);
    return this;
  }
  
  public static void register(int id, ImprintCommand handler, String... prefixes)
  {    
    CommandParser parser = CommandParser.getMasterParser();
    for (String s : prefixes)
      parser.addSubParser(parser = parser.createSubParser("imprint", s, id));
    _intsToHandlers.put(id, handler);
  }
  
  public void addOption(Option option)
  {
    if (option.isRequired())
      this._namesToRequiredOptions.put(option.getName(), option);
    else
      this._namesToOptions.put(option.getName(), option);
  }
  
  public ImprintCommand getHandler(String prefix)
  {
    CommandParser p;
    if ((p = this._namesToParsers.get(prefix)) == null)
      return null;
    
    return p._intsToHandlers.get(p._id);
  }
  
  private void _parse(String[] args, int argBegin, ICommandSender sender) throws IllFormedCommandException
  {
    if (argBegin >= args.length)
      throw new IllFormedCommandException("No parameters specified.");
    
    final LinkedList<Option> resultList = new LinkedList<Option>();
    final Set<String> requiredList = this._namesToRequiredOptions.keySet();
        
    for (int i = argBegin; i < args.length; ++i)
    {
      Matcher m = this._CMD_COLON.matcher(args[i]);
      if (!m.matches())        
        throw new IllFormedCommandException("Illformed parameter at " + i);
      
      final String paramName = m.group(1);
      final String paramValue = m.group(2);
      Option opt = this._namesToRequiredOptions.get(paramName);
      if (opt == null)
        opt = this._namesToOptions.get(paramName);
      else
        requiredList.remove(opt.getName());
      if (opt == null)
        throw new IllFormedCommandException("Unknown parameter " + paramName);
      
      if (opt.isList())
      {
        final String[] values = paramValue.split(",");
        if (values.length > opt.getListMax())
          throw new IllFormedCommandException("Too many values for parameter " + paramName);
        
        opt.setResult(values);
      } else
        opt.setResult(paramValue);
      resultList.add(opt);
    }
    
    if (requiredList.size() != 0)
      throw new IllFormedCommandException("Required parameters missing.");
    
    ImprintCommand cmd = _intsToHandlers.get(this._id);
    if (cmd == null)
      throw new RuntimeException("Register command handler first!");
    cmd.onCommand(sender, resultList);
    return;
  }
  
  public void parse(ICommandSender sender, String[] args) throws IllFormedCommandException
  {
    this._parseHelper(sender, args, 0);
  }
  
  private void _parseHelper(ICommandSender sender, String[] args, int i) throws IllFormedCommandException
  {
    if (!this._isMaster)
    {
      _parse(args, i, sender); 
      return;
    }
    if (args.length == i)
      throw new IllFormedCommandException("Requires sub-command specifier(s).");
    
    CommandParser parser = this._namesToParsers.get(args[0]);
    if (parser == null)
      throw new IllFormedCommandException("Invalid sub-command specifier(s).");
    
    parser._parseHelper(sender, args, i + 1);
  }
}
