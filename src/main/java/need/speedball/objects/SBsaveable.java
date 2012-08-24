package need.speedball.objects;

import java.util.HashMap;
import java.util.Map;

import need.speedball.Game;
import need.speedball.SpeedBall;
import need.speedball.objects.Ball.Balltype;

public abstract class SBsaveable
{
	private enum Fields{Name};
	
	public SBsaveable(SpeedBall sb)
	{
		setSBInstance(sb); 
		data = new HashMap<String,Object>();
	}
	
	public SBsaveable(SpeedBall sb, Map<String,Object> info, boolean compressed)
	{
		this(sb);
		if(compressed)setData(restoreData(info));
		else setData(info);
	}

	// Fields ----------------------------------------------------------------------------------
	
	protected SpeedBall sb;
	protected Map<String,Object> data;	
	
	// Getter/Setter ----------------------------------------------------------------------------------
   	
	public void setSBInstance(SpeedBall sb) {this.sb = sb; }    
	
	protected Map<String,Object> getData() { return data; }
	protected void setData(Map<String,Object> info)	{data.clear();data.putAll(restoreData(info)); }
	
    public String getName() {return (String) data.get(Fields.Name.name());}
    protected void setName(String name) 	{data.put(Fields.Name.name(),name);} 
	
    // Abstract ----------------------------------------------------------------------------------
	
	protected abstract Map<String, Object> restore(Map<String, Object> info, Map<String,Object> addinfo);
	protected abstract Map<String,Object> save();
	public abstract void remove();
	
	
	//Persistance ---------------------------------------------------------------------------------
		
	protected  Map<String, Object> restoreData(Map<String, Object> info)
	{
		Map<String, Object> restored = new HashMap<String, Object>();
		
		restored.put(Fields.Name.name(), (String)info.get("Name"));
		restored.putAll(restore(info,new HashMap<String,Object>(restored)));
		
		return restored;
	}
	
	public  Map<String,Object> saveData()
	{
		Map<String, Object> dump = new HashMap<String, Object>();
		
		dump.put(Fields.Name.name(), getName());
		dump.putAll(save());		
		
		return dump;
	}
	
	
	//Static ---------------------------------------------------------------------------------------
	
	public static enum DataType
	{
		Goals(true), Stadiums(true), Scoreboards(true), Balls(true), Games(true), Players(false);
		
		private boolean save;
		DataType(boolean save)
		{
			this.save = save;
		}
		
		public boolean isSaveable()
		{
			return save;
		}		
	}
	
	public static SBsaveable getSaveable(SpeedBall sb, Map<String,Object> info, DataType type)
	{
		SBsaveable saveable=null;
		switch(type)
		{
			case Stadiums: 		saveable = new Stadium(sb, info, true);
			case Goals:    	 	saveable = new Goal(sb, info, true);
			case Scoreboards:  	saveable = new Scoreboard(sb, info, true);
			case Balls:			
				Balltype balltype = Balltype.valueOf((String)info.get("Type")); 
				switch(balltype) 
				{
					case Block: saveable = new BlockBall(sb,info,true); 
					case Item:  saveable = new ItemBall(sb,info,true);
					case Physics: break;
					case Entity: break;
				}
			case Games:			saveable = new Game(sb,info,true);
			case Players: 		saveable = sb.getSBplayer((String)info.get("Name"));
		}
		
		return saveable;
	}

}
