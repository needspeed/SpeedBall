package need.speedball.objects;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import need.speedball.Game;
import need.speedball.SpeedBall;

public class ItemBall extends Ball
{
	private enum Fields{Name, Source, Game, Entity};
	private Derkick dk = new Derkick();
	
	public ItemBall(SpeedBall sb,Entity entity, Location source, String name)
	{
		super(sb, name, source);
		setEntity(entity,false);
	}
	
	public ItemBall(SpeedBall sb,Entity entity, Location source, String name, Game game)
	{
		super(sb, name, source, game);
		setEntity(entity,false);		
	}
	
	public ItemBall(SpeedBall sb, Map<String,Object> info, boolean compressed)
	{
		super(sb,info,compressed);
	}
	
	
	// Implemented Functions --------------------------------------------------------------------------------------
	
	@Override
	public void remove()
	{
		sb.removeBallHash(getEntity());
		removeGame();
		getEntity().remove();
		sb.removeBall(getName());
	}	
	
	@Override
	public Location getLocation()
	{
		return getEntity().getLocation();
	}

	@Override
	public void fix()
	{	
		Entity newEntity = getEntity().getWorld().dropItem(getEntity().getLocation(), ((Item)getEntity()).getItemStack());
		setEntity(newEntity,true);
	}
	
	@Override
	public void kick(Vector v)
	{
		if(getGame()==null||!getGame().isRunning())return;
		getEntity().setVelocity(v);		
		sb.getServer().getScheduler().cancelTask(dk.threadid);
		dk.threadid = sb.getServer().getScheduler().scheduleSyncRepeatingTask(sb,dk, 0, 1); 
	}

	@Override
	public void setBallObjectLocation(Location l)
	{
		getEntity().teleport(l);
	}

	@Override
	public void reset()
	{
		Vector v = new Vector();
		v.setX(0);v.setZ(0);v.setY(0);
		getEntity().setVelocity(v);
		getEntity().teleport(getSource());
	}
		
	public boolean isObject(Object object)
	{
		return getEntity().equals(object);
	}
	
	
	// Additional Functions --------------------------------------------------------------------------------------
	
	public class Derkick implements Runnable
	{
		public int threadid;
		
		@Override
		public void run()
		{						
			if(getEntity().getTicksLived() >= 1000)
			{	
				fix(); 
			}
			
			Goal tempgoal;
			if((tempgoal = isInGoal())!=null)
			{
				reachPoint(tempgoal);
				sb.getServer().getScheduler().cancelTask(threadid);
			}
			else if(!isInStadium())reset();
		}
	}	
	
	public void reachPoint(Goal g)
	{
		reset();
		getGame().reachedPoint(g.getName());
	}
	
	// Getter/Setter ---------------------------------------------------------------------------------------------
	
	public Entity getEntity()
	{
		return (Entity)data.get(Fields.Entity.name());
	}
	
	private void setEntity(Entity entity, boolean remove)
	{
		sb.removeBallHash(getEntity());
		if(remove&&getEntity()!=null)getEntity().remove();
		data.put(Fields.Entity.name(), entity);
		sb.addBallHash(getEntity(), getName());
	}
	

	
	// Persistance ------------------------------------------------------------------------------------------------
	
	@Override
	public Map<String, Object> saveBall()
	{
		Map<String, Object> outdata = new HashMap<String, Object>();
		
		outdata.put("Type", Balltype.Item.name());
		
		outdata.put("ItemType",((Item)getEntity()).getItemStack().getTypeId());
//		outdata.put("Entity", getEntity().getUniqueId().toString());
		
		return outdata;
	}
	
	@Override
	public Map<String, Object> restoreBall(Map<String, Object> info, Map<String, Object> addinfo)
	{
		Map<String, Object> restore = new HashMap<String, Object>();
		
//		UUID entityuid = UUID.fromString((String) info.get("Entity"));
		Location entityloc = ((Location)addinfo.get(Fields.Source.name()));
		World world = entityloc.getWorld();
		
		world.loadChunk(entityloc.getBlockX(), entityloc.getBlockZ());
//		Entity entity = sb.gu.getEntity(entityuid, sb.gu.getStadium(entityloc));
//		if(entity==null){ entity = entityloc.getWorld().dropItem(entityloc, new ItemStack((Integer)info.get("EntityType"))); }
		Entity entity = entityloc.getWorld().dropItem(entityloc, new ItemStack((Integer)info.get("ItemType")));
		
		restore.put(Fields.Entity.name(), entity);		
		
		return restore;
	}
}