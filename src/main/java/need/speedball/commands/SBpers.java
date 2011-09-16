package need.speedball.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBpers extends SBcommand
{
	private enum PersCommand {SAVE, LOAD}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
	{
		PersCommand persCommand;
		
        try 
        {
        	persCommand = PersCommand.valueOf(args[0].toUpperCase());
        } 
        catch (IllegalArgumentException ie) 
        {
            sender.sendMessage("Unknown pers command: " + args[0]);
            return true;
        }
        if(!sb.perms.hasPerms((Player)sender, "pers." +persCommand.name()))
        {
        	sender.sendMessage(ChatColor.RED + "No Permissions");
        	return false;
        }
        switch (persCommand) 
        {
        	case SAVE:  sb.per.save();		break;
        	case LOAD:  sb.per.load(); 		break;
        }        
		return true;
	}

}
