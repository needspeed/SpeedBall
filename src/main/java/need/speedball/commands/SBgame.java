package need.speedball.commands;

import java.util.Arrays;

import need.speedball.Game;
import need.speedball.SpeedBall;
import need.speedball.objects.Ball;
import need.speedball.objects.Stadium;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBgame extends SBcommand
{
	private enum GameCommand {START, CREATE, STOP, DELETE, ADDPLAYER, REMPLAYER, POINTS, RESET}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
	{
		GameCommand gameCommand;
		Player player = (Player)sender;
		
        try 
        {
        	gameCommand = GameCommand.valueOf(args[0].toUpperCase());
        } 
        catch (IllegalArgumentException ie) 
        {
            sender.sendMessage("Unknown game command: " + args[0]);
            return true;
        }
        if(!SpeedBall.permissionHandler.has(player, "speedball.game."+gameCommand))
        {
        	sender.sendMessage(ChatColor.RED + "No Permissions");
        	return false;
        }
        
        switch (gameCommand) 
        {
        	case START:  	start(player,args[1]);													break;
        	case CREATE:  	create(player,args[1],args[2],args[3]);	   								break;
        	case STOP:	  	stop(player,args[1]);	  											  	break;
        	case DELETE: 	delete(player,args[1]);													break;
        	case ADDPLAYER:	addPlayers(player,args[1],args[2],Arrays.copyOfRange(args, 3, args.length));  	break;
        	case REMPLAYER: remPlayers(player,args[1],Arrays.copyOfRange(args, 2, args.length));  	break;
        	case POINTS:	points(player,args[1]);													break;
        	case RESET: 	reset(player,args[1]);													break;
        }
        
		return true;
	}
	
	private void create(Player p,String ga,String stad,String ba)
	{
		Stadium stadium = sb.Stadiums.get(stad);
		Ball ball = sb.Balls.get(ba);
		Game game = new Game(sb,ga,stadium,ball);
		sb.Games.put(ga, game);
		
		p.sendMessage("Game created");
	}
	
	private void start(Player p,String ga)
	{
		sb.Games.get(ga).start();
		
		p.sendMessage("Game started");
	}
	
	private void stop(Player p,String ga)
	{
		sb.Games.get(ga).stop();
		
		p.sendMessage("Game stopped");
	}
	
	private void delete(Player p,String ga)
	{
		sb.Games.get(ga).delete();
		
		p.sendMessage("Game deleted");
	}
	
	private void addPlayers(Player p,String ga,String Goal,String[] ps)
	{
		sb.Games.get(ga).addPlayers(sb.Goals.get(Goal), Arrays.asList(ps));
		
		p.sendMessage("Players added");
	}
	
	private void remPlayers(Player p,String ga,String[] ps)
	{
		sb.Games.get(ga).remPlayers(Arrays.asList(ps));
		
		p.sendMessage("Players removed");
	}
	
	private void points(Player p,String ga)
	{
		p.sendMessage(sb.Games.get(ga).getPoints().toString());
	}
	
	private void reset(Player p,String ga)
	{
		sb.Games.get(ga).reset();
	}
}