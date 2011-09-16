package need.speedball.objects;

import java.util.Map;

import need.speedball.SpeedBall;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class Ball
{
	protected SpeedBall sb;
	private String name;
	private Location source;
	
	public Ball(SpeedBall ins,String name,Location source)
	{
		this.sb = ins;
		this.name = name;
		this.source = source;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Location getSource()
	{
		return source;
	}
	
	public abstract Location getLocation();
	
	public abstract boolean isObject(Object o);
	
	public abstract void kick(Vector v);
		
	public abstract void setBallObjectLocation(Location l);
	
	public abstract void reset();
	
	public abstract Map<String,Object> getSpecials();
	
	public boolean isInStadium(Location l)
	{						
		return sb.gu.getGame(this).getStadium().containsBlock(l);
	}
	
	public Goal isInGoal(Location l)
	{
		Stadium st = sb.gu.getGame(this).getStadium();
		
		for(Goal g:st.getGoals())
		{
			if(g.containsBlock(l)) return g;
		}		
		return null;
	}
	
}
