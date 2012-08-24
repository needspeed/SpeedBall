package need.speedball.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import need.speedball.Game;
import need.speedball.SpeedBall;

public abstract class SBobject extends SBsaveable
{
	private enum Fields{Name, Game};
	
	public SBobject(SpeedBall sb)
	{
		super(sb);
	}
	
	public SBobject(SpeedBall sb, Map<String,Object> info, boolean compressed)
	{
		super(sb,info,compressed);
	}

	// Fields ----------------------------------------------------------------------------------
	
	// Getter/Setter ----------------------------------------------------------------------------------
   	
    public Game getGame() {return sb.getGame(((String) data.get(Fields.Game.name())));}
    public boolean setGame(Game game) 		{return setGame(game.getName());}
    public boolean setGame(String game) 	{if(getGame()==null||getGame().equals(game)) data.put(Fields.Game.name(),game);else return false; return true;	} 
	
    // Abstract ----------------------------------------------------------------------------------
	
    protected abstract Map<String, Object> restoreObject(Map<String, Object> info,Map<String, Object> addinfo);
	protected abstract Map<String,Object> saveObject();
	public abstract void removeGame();
	
	//Persistance ---------------------------------------------------------------------------------
		
	@Override
	protected Map<String, Object> restore(Map<String, Object> info, Map<String,Object> addinfo)
	{
		Map<String, Object> restored = new HashMap<String, Object>();
		
		restored.put(Fields.Game.name(), sb.getGame((String)info.get("Game")));
		
		addinfo.putAll(restored);
		restored.putAll(restoreObject(info,new HashMap<String,Object>(addinfo)));		
		return restored;
	}
	
	@Override
	public  Map<String,Object> save()
	{
		Map<String, Object> dump = new HashMap<String, Object>();
		
		dump.put(Fields.Game.name(), (getGame()==null)?"null":getGame().getName());
		dump.putAll(saveObject());		
		
		return dump;
	}
	
	// Static ----------------------------------------------------------------------------------
	
	public static <T>List<SBobject> getList(List<T> objects)
	{
		List<SBobject> outObj = new ArrayList<SBobject>();
		for(Object o:objects)outObj.add((SBobject)o);
		return outObj;
	}
}