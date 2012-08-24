package need.speedball.objects;

import java.util.HashMap;
import java.util.Map;

import need.speedball.Game;
import need.speedball.SpeedBall;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BlockBall extends Ball
{
	private enum Fields{Name, Source, Game, Block, Id, Meta};
	
	public BlockBall(SpeedBall ins,Block block,Location source,String name, int id, byte meta)
	{
		super(ins,name,source);
		setBlock(block,false);
		setId(id);
		setMeta(meta);
	}
	
	public BlockBall(SpeedBall ins,Block block,Location source,String name, int id, byte meta, Game game)
	{
		super(ins,name,source, game);
		setBlock(block,false);
		setId(id);
		setMeta(meta);
	}	
	
	public BlockBall(SpeedBall sb, Map<String,Object> info, boolean compressed)
	{
		super(sb,info,compressed);
	}
	
	// Implemented Functions --------------------------------------------------------------------------------------
	
	@Override
	public void kick(Vector v)
	{
		if(getGame()==null||!getGame().isRunning())return;
		Location newBlock = getLocation().toVector().add(v).toLocation(getBlock().getWorld());
	
		if(getLocation().equals(newBlock))return;
		
		if(isInGoal(newBlock)==null)
		{
			if(getBlock().getWorld().getBlockAt(newBlock).getTypeId()!=0)
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
			getGame().reachedPoint(isInGoal().getName());
		}
	}
	
	@Override
	public void setBallObjectLocation(Location l)
	{
		getBlock().getWorld().getBlockAt(l).setTypeId(getId());
		getBlock().getWorld().getBlockAt(l).setData(getMeta());
		setBlock(getBlock().getWorld().getBlockAt(l),true);
	}
	
	@Override
	public void reset()
	{		
		setBlock(getBlock().getWorld().getBlockAt(getSource()),true);
		fix();
	}
	
	@Override
	public void fix()
	{
		getBlock().setTypeId(getId());
		getBlock().setData(getMeta());
	}

	@Override
	public Location getLocation()
	{
		return getBlock().getLocation();
	}	
	
	@Override
	public boolean isObject(Object object)
	{
		return getBlock().equals(object);
	}
	
	
	// Getter/Setter ---------------------------------------------------------------------------------------------
	
	@Override
	public void remove()
	{
		sb.removeBallHash(getBlock());
		removeGame();
		getBlock().setTypeId(0);
		sb.removeBall(getName());
	}	
	
	public int getId()
	{
		return (Integer)data.get(Fields.Id.name());
	}
	
	private void setId(int id)
	{
		data.put(Fields.Id.name(), id);
	}
	
	public byte getMeta()
	{
		return (Byte)data.get(Fields.Meta.name());
	}
	
	private void setMeta(byte meta)
	{
		data.put(Fields.Meta.name(), meta);
	}
	
	public Block getBlock()
	{
		return (Block)data.get(Fields.Block.name());
	}
	
	private void setBlock(Block block, boolean remove)
	{
		sb.removeBallHash(getBlock());
		if(remove&&getBlock()!=null)getBlock().setTypeId(0);
		data.put(Fields.Block.name(), block);
		sb.addBallHash(getBlock(), getName());
	}
	
	// Persistance ------------------------------------------------------------------------------------------------
	
	@Override
	public Map<String, Object> saveBall()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("Type", Balltype.Block.name());
		map.put("Id", Integer.valueOf(getId()));
	    map.put("Data", Byte.valueOf(getMeta()));
	
	    return map;
	}
	
	@Override
	public Map<String, Object> restoreBall(Map<String, Object> info, Map<String, Object> addinfo)
	{
		Map<String, Object> restored = new HashMap<String, Object>();
		
		restored.put(Fields.Id.name(), Integer.valueOf(getId()));
		restored.put(Fields.Meta.name(), Byte.valueOf(getMeta()));
		restored.put(Fields.Block.name(), ((Location)addinfo.get(Fields.Source.name())).getBlock());
	
	    return restored;
	}
}
