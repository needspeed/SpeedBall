package need.speedball.objects;

import need.speedball.Region;
import need.speedball.SpeedBall;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class Ball
{
	SpeedBall sb;
	Block ballBlock;
	public String name;
	public Location source;
	public int id;
	public byte data;
	
	public Ball(SpeedBall ins,Block b,String name)
	{
		this.sb = ins;
		this.ballBlock = b;
		this.name = name;
		this.source = ballBlock.getLocation();
		this.id = ballBlock.getTypeId();
		this.data = ballBlock.getData();
	}
	
	public Block getBlock()
	{
		return ballBlock;
	}
	
	public void kick(Vector v)
	{
		Location newBlock = ballBlock.getLocation().toVector().add(v).toLocation(ballBlock.getWorld());
				
		if(isInGoal(newBlock)==null)
		{
			if(ballBlock.getWorld().getBlockAt(newBlock).getTypeId()!=0)reset();
			ballBlock.getWorld().getBlockAt(newBlock).setTypeId(id);
			ballBlock.getWorld().getBlockAt(newBlock).setData(data);
			ballBlock.setTypeId(0);
			ballBlock = ballBlock.getWorld().getBlockAt(newBlock);
		}
		else
		{
			reset();
			sb.gu.getGame(this).reachedPoint(isInGoal(newBlock));
		}
		if(!isInStadium(newBlock))reset();
	}
	
	public void reset()
	{
		ballBlock.setTypeId(0);
		ballBlock = ballBlock.getWorld().getBlockAt(source);
		ballBlock.setTypeId(id);
		ballBlock.setData(data);
		sb.getServer().getScheduler().scheduleSyncDelayedTask(sb, new Runnable(){

			@Override
			public void run()
			{
				ballBlock.setTypeId(id);	
				ballBlock.setData(data);
			}
			
		}, 100);
	}
	
	private boolean isInStadium(Location l)
	{		
		Stadium st = sb.gu.getGame(this).getStadium();
		Region region = new Region(sb,st.corner1,st.corner2);
		
		return region.isInRegion(l);
	}
	
	private Goal isInGoal(Location l)
	{
		Stadium st = sb.gu.getGame(this).getStadium();
		
		for(Goal g:st.getGoals())
		{
			Region rg = new Region(sb,g.corner1,g.corner2);
			if(rg.isInRegion(l)) return g;
		}		
		return null;
	}
	
}
