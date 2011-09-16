package need.speedball.objects;

import java.util.HashMap;
import java.util.Map;

import need.speedball.SpeedBall;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BlockBall extends Ball
{
	Block ballBlock;
	private int id;
	private byte data;
	
	public BlockBall(SpeedBall ins,Block b,String name)
	{
		super(ins,name,b.getLocation());
		this.ballBlock = b;
		this.id = ballBlock.getTypeId();
		this.data = ballBlock.getData();
	}
	
	public int getId()
	{
		return id;
	}
	
	public byte getData()
	{
		return data;
	}
	
	@Override
	public void kick(Vector v)
	{
		Location newBlock = ballBlock.getLocation().toVector().add(v).toLocation(ballBlock.getWorld());
	
		if(ballBlock.getLocation().equals(newBlock))return;
		
		if(isInGoal(newBlock)==null)
		{
			if(ballBlock.getWorld().getBlockAt(newBlock).getTypeId()!=0)
			{
				reset();		
			}
			else setBallObjectLocation(newBlock);
		}
		else
		{
			reset();			
		}
		if(!isInStadium(newBlock))
		{
			reset();
			sb.gu.getGame(this).reachedPoint(isInGoal(getLocation()));
		}
	}
	
	@Override
	public void setBallObjectLocation(Location l)
	{
		ballBlock.getWorld().getBlockAt(l).setTypeId(id);
		ballBlock.getWorld().getBlockAt(l).setData(data);
		ballBlock.setTypeId(0);
		ballBlock = ballBlock.getWorld().getBlockAt(l);
	}
	
	@Override
	public void reset()
	{
		
		ballBlock.setTypeId(0);
		ballBlock = ballBlock.getWorld().getBlockAt(getSource());
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
	
	@Override
	public boolean isObject(Object o)
	{
		return o.equals(ballBlock);
	}

	@Override
	public Location getLocation()
	{
		return ballBlock.getLocation();
	}

	@Override
	public Map<String, Object> getSpecials()
	{
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("Type", "Block");
		map.put("Id", id);
		map.put("Data", data);
		
		return map;
	}
}
