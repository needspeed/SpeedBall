package need.speedball.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import need.speedball.Game;
import need.speedball.objects.Ball;
import need.speedball.objects.Goal;
import need.speedball.objects.Stadium;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBgame extends SBcommand
{
	private enum GameCommand {START, CREATE, STOP, DELETE, ADDPLAYERS, AUTOADDPLAYERS, RANDOMADDPLAYERS ,REMPLAYERS, POINTS, RESET}
	
	public Game game = null;
	
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
        if(!sb.perms.hasPerms(player, gameCommand.name()))
        {
        	sender.sendMessage(ChatColor.RED + "No Permissions");
        	return false;
        }
        
        game = sb.Games.get(args[1]);
		if(game==null && gameCommand != GameCommand.CREATE )
		{
			player.sendMessage(game.name + " not found!");
			return true;
		}
			
        
        switch (gameCommand) 
        {
        	case START:  	start(player);																break;
        	case CREATE:  	create(player,args[1],args[2],args[3]);	   											break;
        	case STOP:	  	stop(player);	  											  				break;
        	case DELETE: 	delete(player);																break;
        	case ADDPLAYERS:	addPlayers(player,args[2],Arrays.copyOfRange(args, 3, args.length));  		break;
        	case AUTOADDPLAYERS: autoAddPlayers(player);												break;
        	case RANDOMADDPLAYERS: randomAddPlayers(player,Arrays.copyOfRange(args, 2, args.length));	break;
        	case REMPLAYERS: remPlayers(player,Arrays.copyOfRange(args, 2, args.length));  				break;
        	case POINTS:	points(player);																break;
        	case RESET: 	reset(player);																break;
        }
        
		return true;
	}
	
	private void create(Player p,String ga,String stad,String ba)
	{
		Stadium stadium = sb.Stadiums.get(stad);
		Ball ball = sb.Balls.get(ba);
		Game game = new Game(sb,ga,stadium,ball);
		sb.Games.put(ga, game);
		
		p.sendMessage(game.name + " created");
	}
	
	private void start(Player p)
	{
		game.start();		
		p.sendMessage(game.name + " started");
	}
	
	private void stop(Player p)
	{
		game.stop();		
		p.sendMessage(game.name + " stopped");
	}
	
	private void delete(Player p)
	{
		game.delete();		
		p.sendMessage(game.name + " deleted");
	}
	
	private void addPlayers(Player p,String Goal,String[] ps)
	{
		game.addPlayers(sb.Goals.get(Goal), Arrays.asList(ps));
		p.sendMessage(ps.length + " players added");				
	}

	private void autoAddPlayers(Player p)
	{
		List<String> players = new ArrayList<String>();
		Stadium st = game.getStadium();
		for(Player pl:st.getCorners()[0].getWorld().getPlayers())
		{
			if(st.containsBlock(pl.getLocation()))
			{
				players.add(pl.getName());
			}
		}
		randomAddPlayers(p,players.toArray(new String[players.size()]));
	}
	
	private void randomAddPlayers(Player p,String[] ps)
	{
		List<String> temp = Arrays.asList(ps);
		Collections.shuffle(temp);
		List<Goal> goals = game.getStadium().getGoals();
		for(int i=0;i<ps.length;i++)
		{
			game.addPlayer(goals.get(i%goals.size()),sb.getServer().getPlayer(ps[i]));			
		}
		p.sendMessage(ps.length + " players randomly added");		
	}	
	
	private void remPlayers(Player p,String[] ps)
	{
		game.remPlayers(Arrays.asList(ps));		
		p.sendMessage("Players removed");
	}
	
	private void points(Player p)
	{
		p.sendMessage(game.getPoints().toString());
	}
	
	private void reset(Player p)
	{
		game.reset();
		p.sendMessage(game.name + " resettet");
	}
}