package need.speedball;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Region
{
	SpeedBall sb;
	Location corner1;
	Location corner2;
	//List<Location> coords = new ArrayList<Location>();
	
	public Region(SpeedBall sb,Location c1, Location c2)
	{
		this.sb = sb;
		this.corner1 = Vector.getMinimum(c1.toVector(), c2.toVector()).toLocation(c1.getWorld());
		this.corner2 = Vector.getMaximum(c1.toVector(), c2.toVector()).toLocation(c2.getWorld());
		//this.coords = getCoordList(corner1,corner2);
	}
	
	static List<Location> getCoordList(Location l1,Location l2)
	{
		Vector v1 = Vector.getMinimum(l1.toVector(),l2.toVector());
		Vector v2 = Vector.getMaximum(l1.toVector(),l2.toVector());
		List<Location> coords = new ArrayList<Location>();
		
		for(int i=v1.getBlockX();i<=v2.getBlockX();i++)
		{
			for(int j=v1.getBlockZ();j<=v2.getBlockZ();j++)
			{
				Location lnew = new Location(l1.getWorld(), i, v1.getY(), j);
				coords.add(lnew);
			}
		}
		
		return coords;
	}
	
	public boolean isInRegion(Location loc)
	{
		return loc.getX()>corner1.getX()&&loc.getX()<corner2.getX()&&loc.getZ()>corner1.getZ()&&loc.getZ()<corner2.getZ();
	}
}
