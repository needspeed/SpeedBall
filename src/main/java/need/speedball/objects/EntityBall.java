package need.speedball.objects;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import need.speedball.SpeedBall;

public class EntityBall extends Ball
{

	private Entity entity;
	private Derkick dk = new Derkick();
	
	public EntityBall(SpeedBall ins,Entity e, String name)
	{
		super(ins, name,e.getLocation());
		this.entity = e;
	}
	
	@Override
	public Location getLocation()
	{
		return entity.getLocation();
	}

	@Override
	public boolean isObject(Object o)
	{
		return entity.equals(o);
	}

	public class Derkick implements Runnable
	{
		public int threadid;
		
		@Override
		public void run()
		{
			if(isInGoal(entity.getLocation())!=null)
			{
				reachPoint(entity.getLocation());
				sb.getServer().getScheduler().cancelTask(threadid);
			}
		}
	}
	
	@Override
	public void kick(Vector v)
	{		
		entity.setVelocity(v);		
		sb.getServer().getScheduler().cancelTask(dk.threadid);
		dk.threadid = sb.getServer().getScheduler().scheduleSyncRepeatingTask(sb,dk, 0, 1); 
		if(!isInStadium(entity.getLocation()))reset();
	}

	@Override
	public void setBallObjectLocation(Location l)
	{
		entity.teleport(l);
	}

	@Override
	public void reset()
	{
		Vector v = new Vector();
		v.setX(0);v.setZ(0);v.setY(0);
		entity.setVelocity(v);
		entity.teleport(getSource());
	}
	
	public void reachPoint(Location l)
	{
		reset();
		if(isInGoal(l)!=null)sb.gu.getGame(this).reachedPoint(isInGoal(l));
	}
	
	@Override
	public Map<String, Object> getSpecials()
	{
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("Type", "Entity");
		map.put("Entity", entity.getUniqueId().toString());
		map.put("World", entity.getWorld().getName());
		
		return map;
	}	
}