package need.speedball.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import need.speedball.Game;
import need.speedball.PlayerCom;
import need.speedball.objects.Ball;
import need.speedball.objects.Goal;
import need.speedball.objects.Scoreboard;
import need.speedball.objects.Stadium;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBgame extends SBcommand
{
	private enum ECommand {START, CREATE, STOP, DELETE, ADDSCOREBOARD, ADDPLAYERS, AUTOADDPLAYERS, RANDOMADDPLAYERS ,REMPLAYERS, POINTS, RESET, FIX}
	
	public Game game = null;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
	{
		ECommand eCommand;
		Player player = (Player)sender;
		if(args.length<2)
		{
			PlayerCom.error(player, "Specify a subcommand and a game");
			return true;
		}
        try 
        {
        	eCommand = ECommand.valueOf(args[0].toUpperCase());
        } 
        catch (IllegalArgumentException ie) 
        {
            sender.sendMessage("Unknown game command: " + args[0]);
            return true;
        }
        if(!checkPerms(sender,this.getClass().getSimpleName(),eCommand.name()))return true;
        
        game = sb.getGame(args[1]);
		if(game==null && eCommand != ECommand.CREATE )
		{
			player.sendMessage("Game "+ args[1] + " not found!");
			return true;
		}
			
        
        switch (eCommand) 
        {
        	case START:  	start(player,args);																break;
        	case CREATE:  	create(player,args);	   													break;
        	case STOP:	  	stop(player,args);	  											  				break;
        	case DELETE: 	delete(player,args);																break;
        	case ADDSCOREBOARD: addScoreboard(player,args);												break;
        	case ADDPLAYERS:	addPlayers(player,args);  												break;
        	case AUTOADDPLAYERS: autoAddPlayers(player);												break;
        	case RANDOMADDPLAYERS: randomAddPlayers(player,args);										break;
        	case REMPLAYERS: remPlayers(player,args);  													break;
        	case POINTS:	points(player,args);																break;
        	case RESET: 	reset(player,args);																break;
        	case FIX: 	fix(player,args);																	break;
        }
        
		return true;
	}
	
	private void addScoreboard(Player player, String[] args)
	{
		if(args.length<3)
		{
			PlayerCom.error(player, "Missing Arguments");
			return;
		}
		if(args.length>3)PlayerCom.warn(player, "You used more arguments than you had to!");
		
		String name = args[2];
		Scoreboard scoreboard;
		if((scoreboard = sb.getScoreboard(name)) == null)
		{
			PlayerCom.error(player, "Scoreboard: %s does not exist. Set it up first.",name);
			return;
		}
		game.addScoreboard(scoreboard);		
	}

	private void fix(Player player, String[] args)
	{
		if(args.length>0)PlayerCom.warn(player, "This command does not need any arguments!");
		game.getBall().fix();
		PlayerCom.info(player, "Ball fixed at " + PlayerCom.toString(game.getBall().getSource()));
	}

	private void create(Player p,String[] args)
	{
		if(args.length<4)
		{
			PlayerCom.error(p, "Missing Arguments");
			return;
		}
		else if(args.length>5) PlayerCom.warn(p, "You used more arguments than you had to!");
		
		String ga = args[1];
		String stad = args[2];
		String ba = args[3];
		boolean balance = (args.length==5 && args[4].equalsIgnoreCase("balance"));
		Stadium stadium = sb.getStadium(stad);
		Ball ball = sb.getBall(ba);
		
		if(stadium == null) 
		{
			PlayerCom.error(p, "Stadium: %s not found",stad);
			return;
		}
		if(ball == null)
		{
			PlayerCom.error(p, "Ball: %s not found",ba);
			return;
		}
		if(sb.getGame(ga)!=null)
		{
			PlayerCom.error(p, "Game: %s already exists!", ga);
			return;
		}
		
		Game game = new Game(sb,ga,stadium,ball,balance);
		sb.addGame(game);
		
		PlayerCom.info(p, "Game: %s successfully created",game.getName());
		sb.continueTutorial(p);
	}
	
	private void start(Player p,String[] args)
	{
		if(args.length>0)PlayerCom.warn(p, "This command does not need any arguments!");
		game.startMatch();	
		PlayerCom.info(p,game.getName() + " started");
		
		sb.continueTutorial(p);
	}
	
	private void stop(Player p,String[] args)
	{
		if(args.length>0)PlayerCom.warn(p, "This command does not need any arguments!");
		game.freeze();	
		PlayerCom.info(p,game.getName() + " stopped");
	}
	
	private void delete(Player p,String[] args)
	{
		if(args.length>0)PlayerCom.warn(p, "This command does not need any arguments!");
		game.remove();		
		PlayerCom.info(p,game.getName() + " deleted");
	}
	
	private void addPlayers(Player p,String[] args)
	{
		if(args.length<4)
		{
			PlayerCom.error(p, "Missing Arguments");
			return;
		}
		
		String goal = args[2];
		String[] ps = Arrays.copyOfRange(args, 3, args.length);
		
		if(game.getPoints(goal)==null)
		{
			PlayerCom.error(p, "Goal: %s not found",goal);
			return;
		}
		if(ps.length==0)
		{
			PlayerCom.warn(p, "You did not specify any players!");
			return;
		}
		
		game.addPlayers(goal, Arrays.asList(ps));
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
		List<String> outlist = new ArrayList<String>();
		outlist.add("");outlist.add("");outlist.addAll(players);
		String[] out = outlist.toArray(new String[outlist.size()]);
		randomAddPlayers(p,out);
	}
	
	private void randomAddPlayers(Player p,String[] args)
	{
		if(args.length<3)
		{
			PlayerCom.error(p, "Missing Arguments");
			return;
		}
		String[] ps = Arrays.copyOfRange(args, 2, args.length);
		List<String> temp = Arrays.asList(ps);
		Collections.shuffle(temp);
		List<Goal> goals = game.getStadium().getGoals();
		for(int i=0;i<ps.length;i++)
		{
			game.addPlayer(goals.get(i%goals.size()).getName(),sb.getSBplayer(ps[i]));			
		}
		p.sendMessage(ps.length + " players randomly added");	
		sb.continueTutorial(p);
	}	
	
	private void remPlayers(Player p,String[] args)
	{
		if(args.length<3)
		{
			PlayerCom.error(p, "Missing Arguments");
			return;
		}
		String[] ps = Arrays.copyOfRange(args, 2, args.length);
		game.removePlayers(Arrays.asList(ps));		
		p.sendMessage("Players removed");
	}
	
	private void points(Player p)
	{
		p.sendMessage(game.getPointsSide().toString());
	}
	
	private void reset(Player p)
	{
		game.reset();
		p.sendMessage(game.getName() + " resettet");
	}
}