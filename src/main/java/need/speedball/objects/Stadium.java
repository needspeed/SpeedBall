package need.speedball.objects;

import need.speedball.SpeedBall;

import org.bukkit.Location;

public class Stadium
{
	SpeedBall sb;
	Location corner1;
	Location corner2;
	public String name;
	Goal[] goals;
	
	public Stadium(SpeedBall sb,Location c1,Location c2,String name,Goal[] goa)
	{
		this.sb = sb;
		this.corner1 = c1;
		this.corner2 = c2;
		this.name = name;
		this.goals = goa;
	}
	
	public Goal[] getGoals()
	{
		return goals;
	}
	
	public Location[] getCorners()
	{
		return new Location[]{corner1,corner2};
	}
}
