package need.speedball.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import need.speedball.Persistence;
import need.speedball.SpeedBall;

import org.bukkit.Location;
import org.bukkit.util.Vector;

@SuppressWarnings("unchecked")
public class Stadium extends SBobject
{
	public static enum Field {Name, Game, Corner1, Corner2, Goals};
	
	public Stadium(SpeedBall sb, Location corner1,Location corner2,String name,List<Goal> goals)
	{
		super(sb);
		setName(name);
		setCorners(new Location[]{corner1,corner2});
		setGoals(goals);
	}	
	
	public Stadium(SpeedBall sb, Map<String,Object> info, boolean compressed)
	{
		super(sb,info,compressed);
	}
	
	// Functions --------------------------------------------------------------------------------------
		
	
	public boolean containsBlock(Location loc)
	{
		Vector v1 = Vector.getMinimum(getCorners()[0].toVector(), getCorners()[1].toVector());
		Vector v2 = Vector.getMaximum(getCorners()[0].toVector(), getCorners()[1].toVector());
		return loc.getX()>=v1.getX()&&loc.getX()<=v2.getX()&&loc.getZ()>=v1.getZ()&&loc.getZ()<=v2.getZ();
	}	
	
	// Getter/Setter ----------------------------------------------------------------------------------
	
	@Override
	public void remove()
	{
		removeGoals();
		removeGame();
		sb.removeStadium(getName());
	}
	
	@Override
	public void removeGame()
	{
		if(getGame()==null)return;
		((need.speedball.Game)data.remove(Field.Game.name())).removeStadium();
	}
	
	public Location[] getCorners()
	{
		return new Location[]{(Location)data.get(Field.Corner1.name()),(Location)data.get(Field.Corner2.name())};
	}
	
	private void setCorners(Location[] corners)
	{
		data.put(Field.Corner1.name(), corners[0]);
		data.put(Field.Corner2.name(), corners[1]);
	}

	public List<Goal> getGoals()
	{
		return (List<Goal>) data.get(Field.Goals.name());
	}
	
	private void setGoals(List<Goal> goals)
	{
		data.put(Field.Goals.name(), goals);
	}
	
	public void removeGoals()
	{
		for(Goal g: getGoals())removeGoal(g);
	}
	
	public void removeGoal(Goal g)
	{
		if(!getGoals().contains(g))return;
		getGoals().remove(g);
		g.removeStadium();
	}
	
	
	// Persistance ------------------------------------------------------------------------------------	
	
	@Override
	protected Map<String, Object> restoreObject(Map<String,Object> info, Map<String,Object> addinfo)
	{
		Map<String,Object> restored = new HashMap<String,Object>();
			
		Location[] locs = Persistence.getLocation(sb, info.get("Corners"));			
		restored.put(Field.Corner1.name(), locs[0]);
		restored.put(Field.Corner2.name(), locs[1]);		
		restored.put(Field.Goals.name(), sb.getStadium((String)info.get("Stadium")));
		
		return restored;
	}

	@Override
	public Map<String,Object> saveObject()
	{
		Map<String,Object> out = new HashMap<String,Object>();
		
		out.put("Corners", Persistence.getCoords(getCorners()));
		out.put("Goals", Persistence.getNames(getList(getGoals())));
		
		return out;
	}
}
