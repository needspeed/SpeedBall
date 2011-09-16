package need.speedball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import need.speedball.objects.Ball;
import need.speedball.objects.Goal;
import need.speedball.objects.Stadium;

public class Game
{
	SpeedBall sb;
	public String name;
	Stadium stadium;
	Ball ball;
	//Display display;
	Map<Goal, List<Player>> players = new HashMap<Goal, List<Player>>();
	Map<Goal, Integer> points = new HashMap<Goal,Integer>();
	
	public Game(SpeedBall sb,String name,Stadium stad,Ball ba)
	{
		this.sb = sb;
		this.name = name;
		this.stadium = stad;
		this.ball = ba;
		for(Goal g:stad.getGoals())	points.put(g, 0);
	}
	
	public void start()
	{
		sb.RunningGames.put(name, this);
	}
	
	public void stop()
	{
		sb.RunningGames.remove(name);
	}
	
	public void delete()
	{
		stop();
		sb.Games.remove(name);
	}
	
	public void addPlayers(Goal goal,List<String> pl)
	{
		List<Player> players = new ArrayList<Player>();
		for(String s:pl)players.add(sb.getServer().getPlayer(s));
		if(this.players.get(goal)==null)this.players.put(goal, new ArrayList<Player>());
		this.players.get(goal).addAll(players);
		givePlayersTricots();
	}
	
	public void remPlayers(List<String> pl)
	{
		List<Player> players = new ArrayList<Player>();
		for(String s:pl)players.add(sb.getServer().getPlayer(s));
		for(Goal g:this.players.keySet())
		{
			if(this.players.get(g).removeAll(players));
		}
	}
	
	public void remPlayer(Player p)
	{
		remPlayers(GameUtils.getList(p.getName()));
	}
	
	public void addPlayer(Goal g,Player p)
	{
		addPlayers(g,GameUtils.getList(p.getName()));
	}
	
	public void addPlayer(Goal g,String p)
	{
		addPlayers(g,GameUtils.getList(p));
	}
	
	public Ball getBall()
	{
		return ball;
	}
	
	public Stadium getStadium()
	{
		return stadium;
	}
	
	public Map<Goal,List<Player>> getPlayers()
	{
		return players;
	}
	
	public void reset()
	{
		for(Goal g:points.keySet())	points.put(g, 0);
	}
	
	public void reachedPoint(Goal g)
	{
		points.put(g, points.get(g)+1);
		sayAllPlayers("GOOOOAL! for: " + g.name);
	}
	
	public void sayAllPlayers(String s)
	{
		for(List<Player> l:players.values())
		{
			for(Player p:l)
			{
				p.sendMessage(s);
			}
		}		
	}
	
	public List<Player> getAllPlayers()
	{
		List<Player> player = new ArrayList<Player>();
		for(List<Player> l:players.values())
		{
			player.addAll(l);
		}
		return player;
	}
	
	public Map<String,Integer> getPoints()
	{
		Map<String,Integer> poi = new HashMap<String,Integer>();
		for(Goal g:points.keySet()) poi.put(g.name,points.get(g));
		return poi;
	}
	
	public void givePlayersTricots()
	{
		for(Goal g:players.keySet())
		{
			for(Player p:players.get(g))
			{
				p.getInventory().setChestplate(new ItemStack(GameUtils.chestPlates[stadium.getGoals().indexOf(g)]));
			}
		}
	}
}
