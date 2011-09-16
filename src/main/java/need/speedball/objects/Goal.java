package need.speedball.objects;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Goal
{
	public String name;
	Stadium stadium;
	Location corner1;
	Location corner2;
	
	public Goal(String name,Location c1,Location c2)
	{
		this.name = name;
		this.corner1 = c1;
		this.corner2 = c2;
	}
	
	public Location[] getCorners()
	{
		return new Location[]{corner1,corner2};
	}
	
	public Stadium getStadium()
	{
		return stadium;
	}
	
	public void setStadium(Stadium stadium)
	{
		this.stadium = stadium;
	}
	
	public boolean containsBlock(Location loc)
	{
		Vector v1 = Vector.getMinimum(corner1.toVector(), corner2.toVector());
		Vector v2 = Vector.getMaximum(corner1.toVector(), corner2.toVector());
		return loc.getX()>v1.getX()&&loc.getX()<v2.getX()&&loc.getZ()>v1.getZ()&&loc.getZ()<v2.getZ();
	}
}
