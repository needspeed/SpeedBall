package need.speedball;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import need.speedball.objects.Ball;
import need.speedball.objects.Stadium;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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
	
	public Stadium getStadium (Location l)
	{
		for(Stadium s:sb.Stadiums.values())
		{
			if(getAllBetween(s).contains(l))return s;
		}
		return null;
	}
	
	public void addToPlayerCuboids(Player p,Location l,int slot)
	{
		if(sb.playerCuboids.containsKey(p)&&sb.playerCuboids.get(p)[slot]!=null)
		{
			sb.playerCuboids.put(p, new Location[]{l,sb.playerCuboids.get(p)[slot]});
		}
		else sb.playerCuboids.put(p, new Location[]{l,null});		
		
		p.sendMessage("Block " + slot + " is now at: " + GameUtils.toString(l));
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
	
	public boolean isBall(Object b)
	{
		for(Ball ba:sb.Balls.values())
		{
			if(ba.isObject(b))return true;
		}
		return false;
	}
	
	public Vector getVelocity(Entity e1,Entity e2,int faktor)
	{
		return e1.getLocation().getDirection().normalize().multiply(faktor).multiply(1/ e1.getLocation().toVector().distance(e2.getLocation().toVector()));
	}
	
	public Entity getEntity(UUID uniqueId,World w) 
	{
		for(Entity e:w.getEntities())
		{
			if(e.getUniqueId().toString().equals(uniqueId.toString()))return e;
		}
		return null;
	}
	
	public Entity getEntity(UUID uniqueId,Chunk c) 
	{
		c.getWorld().loadChunk(c);
		for(Entity e:c.getEntities())
		{
			if(e.getUniqueId().toString().equals(uniqueId.toString()))return e;
		}
		return null;
	}
	
	public Entity getEntity(UUID uniqueId,Stadium stad) 
	{
		List<Chunk> chunks = new ArrayList<Chunk>();
		for(Location l:getAllBetween(stad))
		{
			chunks.add(l.getWorld().getChunkAt(l));
		}
		for(Chunk c:chunks)
		{
			if(getEntity(uniqueId,c)!=null)return getEntity(uniqueId,c);
		}
		return null;
	}
	
	public List<Location> getAllBetween(Location l1,Location l2)
	{
		List<Location> out = new ArrayList<Location>();
		
		Vector min = Vector.getMinimum(l1.toVector(), l2.toVector());
		Vector max = Vector.getMaximum(l1.toVector(), l2.toVector());
		
		for(int i=min.getBlockX();i<max.getBlockX();i++)
		{
			for(int j=min.getBlockZ();j<max.getBlockZ();j++)
			{
				out.add(new Location(l1.getWorld(), i, l1.getBlockY(), j));
			}
		}
		
		return out;		
	}
	
	public List<Location> getAllBetween(Stadium s)
	{
		return getAllBetween(s.getCorners()[0],s.getCorners()[1]);
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
