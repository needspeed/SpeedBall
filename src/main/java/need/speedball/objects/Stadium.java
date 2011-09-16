package need.speedball.objects;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Stadium
{
	Location corner1;
	Location corner2;
	public String name;
	List<Goal> goals;
	
	public Stadium(Location c1,Location c2,String name,List<Goal> goa)
	{
		this.corner1 = c1;
		this.corner2 = c2;
		this.name = name;
		this.goals = goa;
	}
	
	public List<Goal> getGoals()
	{
		return goals;
	}
	
	public Location[] getCorners()
	{
		return new Location[]{corner1,corner2};
	}
	
	public void removeGoal(Goal g)
	{
		goals.remove(g);
	}
	
	public boolean containsBlock(Location loc)
	{
		Vector v1 = Vector.getMinimum(corner1.toVector(), corner2.toVector());
		Vector v2 = Vector.getMaximum(corner1.toVector(), corner2.toVector());
		return loc.getX()>v1.getX()&&loc.getX()<v2.getX()&&loc.getZ()>v1.getZ()&&loc.getZ()<v2.getZ();
	}
}
