package need.speedball.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBremove extends SBcommand
{
	private enum RemoveCommand {STADIUM, BALL, DISPLAY, GOAL}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
		{
			RemoveCommand removeCommand;
			Player player = (Player)sender;
			
	        try 
	        {
	        	removeCommand = RemoveCommand.valueOf(args[0].toUpperCase());
	        } 
	        catch (IllegalArgumentException ie) 
	        {
	            sender.sendMessage("Unknown play command: " + args[0]);
	            return true;
	        }
	        if(!sb.perms.hasPerms(player, "remove." +removeCommand.name()))
	        {
	        	sender.sendMessage(ChatColor.RED + "No Permissions");
	        	return false;
	        }
	        switch (removeCommand) 
	        {
	        	case STADIUM: deleteStadium(args[1]);	break;
	        	case BALL:	  deleteBall(args[1]);  	break;
	        	case DISPLAY: 							break;
	        	case GOAL:	  deleteGoal(args[1]);		break;
	        }
	        
			return true;
		}
		
		void deleteStadium(String s)
		{
			sb.Stadiums.remove(s);
		}
		
		void deleteBall(String s)
		{
			sb.Balls.remove(s);
		}
		
		void deleteGoal(String s)
		{
			sb.Goals.get(s).getStadium().removeGoal(sb.Goals.get(s));
			sb.Goals.remove(s);			
		}

}
