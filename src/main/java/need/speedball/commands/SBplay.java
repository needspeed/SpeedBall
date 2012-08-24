package need.speedball.commands;

import java.util.Arrays;

import need.speedball.Game;
import need.speedball.PlayerCom;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBplay extends SBcommand
{
private enum ECommand {START, STOP}
	
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
		
		String[] nargs = Arrays.copyOfRange(args, 1,args.length);
		
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
        switch (eCommand) 
        {
        	case START:  start(player,nargs);		break;
        	case STOP:   stop(player, nargs);		break;
        }
        
		return true;
	}
	
	private void start(Player p,String[] args)
	{
		if(args.length<2)
		{
			PlayerCom.error(p, "Missing Arguments");
			return;
		}
		if(args.length>2)PlayerCom.warn(p, "You used more arguments than you had to!");
		String ga = args[0];
		String goa = args[1];
		Game game = sb.getGame(ga);
		if(game==null)
		{
			PlayerCom.error(p, "Game " + ga + " does not exist!");
			return;
		}
		if(!game.addPlayer(goa,sb.getSBplayer(p)))
		{
			PlayerCom.error(p, "You were not able to join game " + ga + " in goal " + goa+"!");
			return;
		}
		PlayerCom.info(p, "You joined successfully game " + ga + " in goal " + goa +"!");
		p.teleport(game.getBall().getLocation());
	}
	
	private void stop(Player p, String[] args)
	{
		if(args.length>0)PlayerCom.warn(p, "This command does not need any arguments!");
		Game game = sb.getPlayerGame(p.getName());
		if(game==null)
		{
			PlayerCom.error(p, "You can't leave a game, when you are even in one in!");
			return;
		}
		if(!game.removePlayer(sb.getSBplayer(p)))
		{
			PlayerCom.error(p, "You could not leave the game " + game.getName() + "!");
			return;
		}
		PlayerCom.info(p, "You left successfully the game %s.",game.getName());
	}
}
