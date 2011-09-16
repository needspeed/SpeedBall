package need.speedball.commands;

import need.speedball.GameUtils;
import need.speedball.SpeedBall;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBlist extends SBcommand
{
	private enum ListCommand {BALLS, STADIUMS, GAMES, GOALS, DEVS, VERSION}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
		{
			ListCommand listCommand;
			Player player = (Player)sender;
			
	        try 
	        {
	        	listCommand = ListCommand.valueOf(args[0].toUpperCase());
	        } 
	        catch (IllegalArgumentException ie) 
	        {
	            sender.sendMessage("Unknown list command: " + args[0]);
	            return true;
	        }
	        if(!SpeedBall.permissionHandler.has(player, "speedball.list."+listCommand))
	        {
	        	sender.sendMessage(ChatColor.RED + "No Permissions");
	        	return false;
	        }
	        switch (listCommand) 
	        {
	        	case BALLS:  	listBalls(player);			break;
	        	case STADIUMS:  listStadiums(player); 		break;
	        	case GAMES:  	listGames(player);			break;
	        	case GOALS:   	listGoals(player);			break;
	        	case DEVS: 		listDevs(player);			break;
	        	case VERSION:   showVersion(player);		break;
	        }
	        
			return true;
		}
		
		public void showVersion(Player p)
		{
			p.sendMessage("Version: " + sb.pdfFile.getVersion());
		}
		
		public void listBalls(Player p)
		{
			for(String b:sb.Balls.keySet())
			{
				p.sendMessage(b +  ": " + GameUtils.toString(sb.Balls.get(b).getBlock().getLocation()));
			}
		}
		
		public void listStadiums(Player p)
		{
			p.sendMessage(sb.Stadiums.keySet().toString());
		}
		
		public void listGames(Player p)
		{
			p.sendMessage(sb.Games.keySet().toString());
		}
		
		public void listGoals(Player p)
		{
			p.sendMessage(sb.Goals.keySet().toString());
		}
		
		public void listDevs(Player p)
		{
			p.sendMessage("Devs: needspeed10");
		}
}