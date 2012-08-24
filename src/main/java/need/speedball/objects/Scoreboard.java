package need.speedball.objects;

import java.util.HashMap;
import java.util.Map;

import need.speedball.Game;
import need.speedball.Persistence;
import need.speedball.SpeedBall;

import org.bukkit.Location;
import org.bukkit.block.Sign;

@SuppressWarnings("unchecked")
public class Scoreboard extends SBobject
{
	public static enum Field {Name, Game, Sign};
	
	public Scoreboard(SpeedBall sb, String name,Sign sign)
	{
		super(sb);
		setSign(sign);
		setName(name);
		setGame("");
	}
	
	public Scoreboard(SpeedBall sb, String name,Sign sign, Game game)
	{
		this(sb, name, sign);
		setGame(game);
	}	
	
	public Scoreboard(SpeedBall sb, Map<String,Object> info, boolean compressed)
	{
		super(sb,info,compressed);
	}
		
	
	// Functions --------------------------------------------------------------------------------------
		
	public void writeScore(Map<String,Integer> points)
	{
		getSign().setLine(0, getGame().getName());
		String scoreline="", teamline="", second="", winner="", winline="";
		for(String team:points.keySet())
		{
			scoreline +=points.get(team)+": ";
			teamline += team+": ";
			if(winner=="" || points.get(team)>=points.get(winner))
			{
				second = winner;
				winner = team;
			}
		}
		
		if(second == "" || points.get(winner)>points.get(second))winline = "Winner: " + winner;
		else winline = "Draw";
		
		scoreline = scoreline.substring(0, scoreline.length()-2);	
		teamline = teamline.substring(0,teamline.length()-2);
		getSign().setLine(1, scoreline);
		getSign().setLine(2, teamline);
		getSign().setLine(3, winline);
		getSign().update();
	}
	
	//Getter/Setter ------------------------------------------------------------------------------------
	
	@Override
	public void remove()
	{
		removeGame();
		sb.removeScoreboard(getName());
	}	
	
	@Override
	public void removeGame()
	{
		if(getGame()==null)return;
		((Game)data.remove(Field.Game.name())).removeScoreboard(this);
	}	
	
	public Sign getSign()
	{
		return (Sign)data.get(Field.Sign.name());
	}
	
	private void setSign(Sign sign)
	{
		data.put(Field.Sign.name(), sign);
	}
	
	public Location getSignLoc()
	{
		return getSign().getLocation();
	}	
	
	
	// Persistance ------------------------------------------------------------------------------------

	@Override
	protected Map<String, Object> restoreObject(Map<String, Object> info, Map<String, Object> addinfo)
	{
		Map<String,Object> restored = new HashMap<String,Object>();
		
		restored.put(Field.Sign.name(), Persistence.getLocation(sb,(Map<String,Object>)info.get("Sign")));
		
		return restored;
	}

	@Override
	public Map<String, Object> saveObject()
	{
		Map<String,Object> out = new HashMap<String,Object>();
		
		out.put("Sign", Persistence.getCoords(getSignLoc()));
		
		return out;
	}
	
}
