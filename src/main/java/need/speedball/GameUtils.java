package need.speedball;

import java.util.ArrayList;
import java.util.List;

import need.speedball.objects.Ball;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GameUtils
{
	SpeedBall sb;
	
	public GameUtils(SpeedBall sb) 
	{
		this.sb = sb;
	}
	
	public Game getGame(Player p)
	{
		for(Game g:sb.RunningGames.values())
		{
			for(List<Player> lp:g.getPlayers().values())
			{
				if(lp.contains(p))return g;
			}
		}
		return null;
	}
	
	public Game getGame(Ball b)
	{
		for(Game g:sb.RunningGames.values())
		{
			if(g.getBall().equals(b))return g;
		}
		return null;
	}
	
	static public String toString(Location loc)
	{
		return "X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ();
	}
	
	public boolean isBall(Block b)
	{
		for(Ball ba:sb.Balls.values())
		{
			if(ba.getBlock().equals(b))return true;
		}
		return false;
	}
	
	static <T>ArrayList<T> getList(T... array)
	{
		ArrayList<T> list = new ArrayList<T>();
		for(T t : array)
		{
			list.add(t);
		}
		return list;
	}
	
	static int[] chestPlates = new int[]{299,307,311,315};
}
