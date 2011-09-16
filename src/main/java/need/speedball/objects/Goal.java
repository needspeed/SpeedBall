package need.speedball.objects;

import org.bukkit.Location;

public class Goal
{
	public String name;
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
	
}
