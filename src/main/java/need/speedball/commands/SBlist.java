package need.speedball.commands;

import need.speedball.Game;
import need.speedball.PlayerCom;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBlist extends SBcommand
{
	private enum ECommand {BALLS, STADIUMS, GAMES,PLAYERS, GOALS, DEVS, VERSION}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
		{
			ECommand eCommand;
			Player player = (Player)sender;
			if(args.length<1)
			{
				PlayerCom.error(player, "Specify a subcommand");
				return true;
			}			
			
	        try 
	        {
	        	eCommand = ECommand.valueOf(args[0].toUpperCase());
	        } 
	        catch (IllegalArgumentException ie) 
	        {
	            sender.sendMessage("Unknown list command: " + args[0]);
	            return true;
	        }
	        if(!checkPerms(sender,this.getClass().getSimpleName(),eCommand.name()))return true;
	        switch (eCommand) 
	        {
	        	case BALLS:  	listBalls(player);			break;
	        	case STADIUMS:  listStadiums(player); 		break;
	        	case GAMES:  	listGames(player);			break;
	        	case PLAYERS:   listPlayers(player,args);	break;
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
			for(String b:sb.getBalls().keySet())
			{
				p.sendMessage(b +  ": " + PlayerCom.toString(sb.getBall(b).getLocation()));
			}
		}
		
		public void listStadiums(Player p)
		{
			p.sendMessage(sb.getStadiums().keySet().toString());
		}
		
		public void listGames(Player p)
		{
			p.sendMessage(sb.getGames().keySet().toString());
		}
		
		public void listPlayers(Player p,String[] args)
		{
			if(args.length<2)
			{
				PlayerCom.error(p, "Missing Arguments");
				return;
			}
			String ga = args[1];
			p.sendMessage(((Game)sb.getGames().get(ga)).getAllPlayers().toString());
		}
		
		public void listGoals(Player p)
		{
			p.sendMessage(sb.getGoals().keySet().toString());
		}
		
		public void listDevs(Player p)
		{
			p.sendMessage("Devs: needspeed10");
		}
}