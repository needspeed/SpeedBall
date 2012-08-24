package need.speedball.commands;

import need.speedball.PlayerCom;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBremove extends SBcommand
{
	private enum ECommand {STADIUM, BALL, SCOREBOARD, GOAL}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
		{
			ECommand eCommand;
			Player player = (Player)sender;
			if(args.length<2)
			{
				PlayerCom.error(player, "Specify a subcommand and an object");
				return true;
			}
			else if(args.length>2) PlayerCom.warn(player, "You added an argument too much!");
			
	        try 
	        {
	        	eCommand = ECommand.valueOf(args[0].toUpperCase());
	        } 
	        catch (IllegalArgumentException ie) 
	        {
	            sender.sendMessage("Unknown play command: " + args[0]);
	            return true;
	        }
	        if(!checkPerms(sender,this.getClass().getSimpleName(),eCommand.name()))return true;
	        
	        boolean flag=false;
	        
	        switch (eCommand) 
	        {
	        	case STADIUM: flag=sb.removeScoreboard(args[1]);		break;
	        	case BALL:	  flag=sb.removeStadium(args[1]);  			break;
	        	case SCOREBOARD: flag=sb.removeBall(args[1]);			break;
	        	case GOAL:	  flag=sb.removeGoal(args[1]);				break;
	        }
	        
	        if(flag) PlayerCom.info(player, eCommand.name() + " " + args[1] + " successfully deleted");
	        else PlayerCom.error(player, eCommand.name() + " " + args[1] + " is not existent or could not be deleted!");
	        
			return true;
		}
}
