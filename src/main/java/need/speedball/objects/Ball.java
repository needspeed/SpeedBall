package need.speedball.objects;

import java.util.HashMap;
import java.util.Map;

import need.speedball.Game;
import need.speedball.Persistence;
import need.speedball.SpeedBall;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public abstract class Ball extends SBobject
{
	private enum Fields{Name, Source, Game};
	public enum Balltype{Item, Block, Entity, Physics};
	public Ball(SpeedBall sb, String name, Location source)
	{
		super(sb);
		setName(name);
		setSource(source);
	}
	
	public Ball(SpeedBall sb, String name, Location source, Game game)
	{
		this(sb, name, source);
		setGame(game);
	}
	
	public Ball(SpeedBall sb, Map<String,Object> info, boolean compressed)
	{
		super(sb,info,compressed);
	}
	
	// Getter/Setter ----------------------------------------------------------------------------------
	
	@Override
	public void removeGame()
	{
		if(getGame()==null)return;
		((Game)data.remove(Fields.Game.name())).removeBall();
	}
	
	public Location getSource()		{return (Location)data.get(Fields.Source.name()); }
	protected void setSource(Location source)		{ data.put(Fields.Source.name(), source); }	
		
	// Abstract ----------------------------------------------------------------------------------
	
	public abstract void fix();	
	public abstract Location getLocation();	
	public abstract void kick(Vector v);		
	public abstract void setBallObjectLocation(Location l);	
	public abstract void reset();	
	public abstract boolean isObject(Object object);
	protected abstract Map<String, Object> saveBall();
	protected abstract Map<String, Object> restoreBall(Map<String, Object> info, Map<String, Object> addinfo);
	
	// Functions ----------------------------------------------------------------------------------

	
	public boolean isInStadium()
	{						
		return isInStadium(getLocation());
	}
	
	public boolean isInStadium(Location l)
	{						
		return getGame().getStadium().containsBlock(l);
	}
	
	public Goal isInGoal()
	{
		return isInGoal(getLocation());
	}
	
	public Goal isInGoal(Location l)
	{
		for(Goal g:getGame().getStadium().getGoals())
		{
			if(g.containsBlock(getLocation())) return g;
		}		
		return null;
	}
	
	//Persistance ---------------------------------------------------------------------------------

	@Override
	public Map<String, Object> saveObject()
	{
		Map<String, Object> dump = new HashMap<String, Object>();

		dump.put(Fields.Source.name(), Persistence.getCoords(getSource()));
		dump.putAll(saveBall());		
		
		return dump;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> restoreObject(Map<String, Object> info, Map<String, Object> addinfo )
	{
		Map<String, Object> restored = new HashMap<String, Object>();
		
		restored.put(Fields.Source.name(), Persistence.getLocation(sb, (Map<String, Object>)info.get("Source")));
		
		addinfo.putAll(restored);
		restored.putAll(restoreBall(info,new HashMap<String,Object>(addinfo)));		
		return restored;
	}
	
	
	// Static -----------------------------------------------------------------------------------------
	
	public static Vector getVelocity(Entity e1,Entity e2,int faktor)
	{
		return e1.getLocation().getDirection().normalize().multiply(faktor).multiply(1/ e1.getLocation().toVector().distance(e2.getLocation().toVector()));
	}
	
}
