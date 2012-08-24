package need.speedball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import need.speedball.PlayerCom.MessageType;
import need.speedball.objects.Ball;
import need.speedball.objects.Goal;
import need.speedball.objects.SBplayer;
import need.speedball.objects.SBsaveable;
import need.speedball.objects.Scoreboard;
import need.speedball.objects.Stadium;
import need.speedball.objects.SBobject;

public class Game extends SBsaveable
{
	
	private enum Fields 
	{
		Name, Mode, Stadium, Ball, Scoreboards, Points, Players, Autobalance
	}		
	
	public enum GameState
	{
		OK, NoBall, NoStadium, NoSides, NoPlayers
	}
	
	public enum GameMode
	{
		Match, Fun, Freeze
	}
	
	private IntegrityCheck check = new IntegrityCheck();
	
	public Game(SpeedBall sb,String name,Stadium stadium,Ball ball, boolean autobalance)
	{
		super(sb);				
		
		setName(name);
		setStadium(stadium);
		setBall(ball);
		setScoreboards(new ArrayList<Scoreboard>());
		setPoints(new HashMap<String,Integer>());
		setPlayers(new HashMap<String, List<Player>>());

		freeze();
		for(Goal g:getStadium().getGoals())	getPoints().put(g.getName(), 0);
		activateFunmode();
	}
	
	public Game(SpeedBall sb,String name,Stadium stad,Ball ba, boolean autobalance,List<String> scoreboards, Map<String, Integer> points, Map<String, List<String>> players)
	{
		this(sb,name,stad,ba,autobalance);
		for(String s:scoreboards)addScoreboard(sb.getScoreboard(s));
		setPoints(points);
		setPlayers(players);
	}
	

	public Game(SpeedBall sb, Map<String, Object> info, boolean compressed)
	{
		super(sb,info,compressed);
	}
	
	
	//Functions -------------------------------------------------------------------------------------------
	
	public void remove()
	{
		freeze();
		removePlayersTricots();
		sb.removeGame(getName());
	}	
	
	public void startMatch()
	{
		givePlayersTricots();
		data.put(Fields.Mode.name(), GameMode.Match);
		startCheck();
		startAfkCheck();
	}
	
	public void activateFunmode()
	{
		data.put(Fields.Mode.name(), GameMode.Fun);
		startCheck();
		startAfkCheck();
	}

	public void freeze()
	{
		stopCheck();		
		stopAfkCheck();
		data.put(Fields.Mode.name(), GameMode.Freeze);
		sayAllPlayers(MessageType.Info, getName()+" freezed!",true);
	}
	
	public void reset()
	{
		for(String g:getPoints().keySet())	setPoints(g,0);
	}	
	
	public void givePlayersTricots()
	{
		for(String g:getPlayers().keySet())
		{
			for(SBplayer p:getPlayers(g))
			{
				p.giveTricot(35,(byte)getStadium().getGoals().indexOf(g));
			}
		}
	}
	
	public void removeTricot(SBplayer p)
	{
		p.removeTricot();
	}
	
	public void removePlayersTricots()
	{
		for(SBplayer p:getAllPlayers())removeTricot(p);
	}
	
	public void sayAllPlayers(MessageType type,String message, boolean log)
	{
		if(log)PlayerCom.log(type,message);
		for(SBplayer p:getAllPlayers())
		{
			p.send(type, message);
		}	
	}

	public void reachedPoint(String goal)
	{
		if(!isinMatchMode())return;
		incrementPoints(goal);
		refreshScoreboards();
		int goalnum = getPoints().keySet().size();
		if(goalnum>2)
		{
			sayAllPlayers(MessageType.Info,ChatColor.WHITE + "GOOOOAL! " + ChatColor.RED + " against: " + goal,false);
			incrementPoints(goal);
		}
		else if(goalnum==2)
		{
			String opponent= getOpponent(goal);
			sayAllPlayers(MessageType.Info,ChatColor.WHITE + "GOOOOAL! " + ChatColor.GREEN + "for: " + opponent  + ChatColor.RED + " against: " + goal,false);
			incrementPoints(opponent);
		}
		else incrementPoints(goal);
			
	}
	
	public void refreshScoreboards()
	{
		for(Scoreboard sc: getScoreboards()) sc.writeScore(getPoints());
	}

	public boolean isbalanced()
	{
		int playernum = getAllPlayers().size();
		int sides = getPlayers().keySet().size();
		
		int allowed = playernum / sides;
		int restplaces = playernum % sides;
		
		int overnum=0;
		
		boolean flag=true;
		for(List<SBplayer> players:getPlayers().values())
		{
			if(players.size()==allowed)continue;
			else if(players.size()==allowed+1)overnum++;
			else if(players.size()>allowed){flag=false;break;}
		}			
		if(overnum>restplaces)flag = false;
		return flag||!isAutobalanceON();
	}
	
	public void autobalance()
	{
		if(!isAutobalanceON())return;
		int playernum = getAllPlayers().size();
		List<SBplayer> pool = new ArrayList<SBplayer>();
		int sides = getPlayers().keySet().size();
		int restplaces = playernum % sides;
		for(String s:getPlayers().keySet())
		{
			while(getPlayers(s).size()>playernum/sides && !(getPlayers(s).size()-playernum/sides==1 && restplaces>0))
			{
				SBplayer p = getPlayers(s).get(getPlayers(s).size()-1);
				removePlayer(p);
				pool.add(p);
			}
		}
		for(String s:getPlayers().keySet())
		{
			while(getPlayers(s).size()<playernum/sides)
			{
				addPlayer(s,pool.remove(pool.size()-1));
			}
		}
		/*for(String s:getPlayers().keySet())
		{
			if(pool.size()==0)break;
			addPlayer(s,pool.remove(pool.size()-1));
		}*/
	}	
	
	public void startAfkCheck()
	{
		sb.getAfkCheck().addPlayers(getAllPlayers());
	}
	
	public void stopAfkCheck()
	{
		sb.getAfkCheck().removePlayers(getAllPlayers());
	}	
	
	public List<GameState> getGameState()
	{
		List<GameState> out = new ArrayList<GameState>();
		
		if(getBall()==null)out.add(GameState.NoBall);
		if(getStadium()==null)out.add(GameState.NoStadium);
		if(getPoints().size()==0)out.add(GameState.NoSides);
		if(getAllPlayers().size()==0)out.add(GameState.NoPlayers);
		if(out.size()==0)out.add(GameState.OK);
		
		return out;		
	}
	
	public void fixGame(List<GameState> errors)
	{
		if(errors.contains(GameState.NoBall))sayAllPlayers(MessageType.Error, getName() +": No Ball found!",true);
		if(errors.contains(GameState.NoStadium))sayAllPlayers(MessageType.Error, getName() +": No Stadium found!",true);
		if(errors.contains(GameState.NoSides))sayAllPlayers(MessageType.Error, getName() +": No Goals found!",true);
		if(errors.contains(GameState.NoPlayers))freeze();
		freeze();
	}
	
	private void startCheck()
	{
		check.threadid = sb.getServer().getScheduler().scheduleSyncRepeatingTask(sb,check, 0, 1200); 
	}
	
	private void stopCheck()
	{
		check.stop();
	}
	
	public class IntegrityCheck implements Runnable
	{
		public int threadid;
		
		@Override
		public void run()
		{
			List<GameState> states = getGameState();
			if(!states.contains(GameState.OK))
			{
				fixGame(states);
			}
		}
		
		public void stop()
		{
			sb.getServer().getScheduler().cancelTask(threadid);
		}
	}	
	
	//Getter/Setter ------------------------------------------------------------------------------------
	
	//Object
	private SBobject getObject(Fields type)
	{
		return (SBobject)data.get(type.name());
	}
	
	private void setObject(Fields type, SBobject object)
	{
		removeObject(type);
		data.put(type.name(), object);
	}
	
	private void removeObject(Fields type)
	{
		if(getObject(type)==null)return;
		((SBobject)data.remove(type.name())).removeGame();
	}
	
	//GameMode
	public GameMode getMode()
	{
		return (GameMode)data.get(Fields.Mode.name());
	}
	
	public boolean isFrozen()
	{
		return getMode().equals(GameMode.Freeze);
	}
	
	public boolean isinFunMode()
	{
		return getMode().equals(GameMode.Fun);
	}
	
	public boolean isinMatchMode()
	{
		return getMode().equals(GameMode.Match);
	}
	
	public boolean isRunning()
	{
		return isinMatchMode()||isinFunMode();
	}	
	
	//Autobalance
	public boolean isAutobalanceON()
	{
		return (Boolean)data.get(Fields.Autobalance.name());
	}
	
	public void setAutobalance(boolean autobalance)
	{
		data.put(Fields.Autobalance.name(), autobalance);
	}
	
	//Stadium
	public Stadium getStadium()
	{
		return (Stadium)getObject(Fields.Stadium);
	}
	
	public void setStadium(Stadium stadium)
	{
		setObject(Fields.Stadium,stadium);
	}
	
	public void removeStadium()
	{
		removeObject(Fields.Stadium);
		removePoints();
	}
		
	//Ball
	public Ball getBall()
	{
		return (Ball)getObject(Fields.Ball);
	}
	
	public void setBall(Ball ball)
	{
		setObject(Fields.Ball,ball);
	}
	
	public void removeBall()
	{
		removeObject(Fields.Ball);
	}
	
	//Scoreboard
	@SuppressWarnings("unchecked")
	public List<Scoreboard> getScoreboards()
	{
		return (List<Scoreboard>)data.get(Fields.Scoreboards.name());
	}
	
	public List<String> getScoreboardNames()
	{
		List<String> out = new ArrayList<String>();
		for(Scoreboard sc:getScoreboards())out.add(sc.getName());
		return out;
	}
	
	public <T> void setScoreboards(List<T> scoreboards)
	{
		removeScoreboards();
		for(T sc:scoreboards)addScoreboard(sc);
	}
	
	public <T> boolean addScoreboard(T scoreboard)
	{
		Scoreboard sc;
		if(scoreboard instanceof String)sc = sb.getScoreboard((String)scoreboard);
		else if(scoreboard instanceof Scoreboard) sc = (Scoreboard)scoreboard;
		else return false;
		
		if(!sc.setGame(this))return false;
		getScoreboards().add(sc);
		refreshScoreboards();
		return true;
	}
	
	public void removeScoreboards()
	{
		if(getScoreboards().size()==0)return;
		for(Scoreboard sc:getScoreboards())removeScoreboard(sc);
	}
	
	public boolean removeScoreboard(Scoreboard scoreboard)
	{
		if(!getScoreboards().contains(scoreboard))return false;
		getScoreboards().remove(scoreboard);
		scoreboard.removeGame();
		return true;
	}
	
	//Players
	@SuppressWarnings("unchecked")
	private Map<String, List<SBplayer>> getPlayers()
	{
		return (Map<String, List<SBplayer>>) data.get(Fields.Players.name());
	}
	
	private Map<String, List<String>> getPlayerNames()
	{
		Map<String,List<String>> out = new HashMap<String, List<String>>();
		Map<String,List<SBplayer>> in = getPlayers();
		for(String s:in.keySet())
		{
			List<SBplayer> players = in.get(s);
			List<String> names = new ArrayList<String>();
			for(SBplayer p:players)names.add(p.getName());
			out.put(s, names);
		}
		return out;		
	}
	
	public List<SBplayer> getPlayers(String goal)
	{
		return getPlayers().get(goal);
	}
	
	public <T> void setPlayers(Map<String,List<T>> players)
	{
		removePlayers();
		for(String goal:players.keySet())addPlayers(goal,players.get(goal));
	}
	
	public boolean addPlayer(String goal, SBplayer player)
	{
		if(!getPlayers().containsKey(goal)) return false;
		getPlayers().get(goal).add(player);
		player.setGame(this);
		return true;
	}

	public <T> boolean addPlayers(String goal, List<T> players)
	{
		if(!getPlayers().containsKey(goal)) return false;
		if(players.size()==0)return true;
		int flag=0;
		for(T player:players)
		{
			flag++;
			if(players.get(0) instanceof String) addPlayer(goal, sb.getSBplayer((String)player));
			else if(players.get(0) instanceof SBplayer) addPlayer(goal, (SBplayer)player);
			else flag--;
		}
		return flag>0;
	}
	
	public boolean removePlayer(SBplayer player)
	{
		boolean flag=false;
		for(String g:getPlayers().keySet())
		{
			if(getPlayers(g).remove(player))
			{
				flag = true;
				removeTricot(player);
				sb.removePlayer(player.getName());
			}
		}
		return flag;
	}
	
	public <T> boolean removePlayers(List<T> players)
	{		
		if(players.size()==0)return true;
		int flag=0;
		for(T player:players)
		{
			flag++;
			if(player instanceof String) removePlayer(sb.getSBplayer((String)player));
			else if(player instanceof SBplayer) removePlayer((SBplayer)player);
			else flag--;
		}
		return flag>0;
	}
	
	public void removePlayers()
	{
		if(getPlayers().size()==0)return;
		for(SBplayer player:getAllPlayers())sb.removePlayer(player.getName());
		setPlayers(new HashMap<String, List<Player>>());
	}
	
	public List<SBplayer> getAllPlayers()
	{
		List<SBplayer> players = new ArrayList<SBplayer>();
		for(List<SBplayer> l:getPlayers().values())
		{
			players.addAll(l);
		}
		return players;
	}
	
	public String getSide(SBplayer player)
	{
		for(String goal:getPlayers().keySet())if(getPlayers(goal).contains(player)) return goal;
		return null;
	}
	
	
	//Points
	@SuppressWarnings("unchecked")
	private HashMap<String, Integer> getPoints()
	{
		return (HashMap<String, Integer>) data.get(Fields.Points.name());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Integer> getPointsSide()
	{
		return (HashMap<String, Integer>)getPoints().clone();
	}
	
	
	public Integer getPoints(String goal)
	{
		return getPoints().get(goal);
	}
	
	public void setPoints(Map<String, Integer> points)
	{
		removePoints();
		getPoints().putAll(points);
	}
	
	public void setPoints(String goal, int points)
	{
		if(!getPoints().containsKey(goal))return;
		getPoints().put(goal, points);
	}
	
	public void removePoint(String goal)
	{
		if(!getPoints().containsKey(goal))return;
		getPoints().remove(goal);
		Goal g;
		if((g = sb.getGoal(goal))!=null)g.removeGame();
	}
	
	public void removePoints()
	{
		if(getPoints().size()==0)return;
		for(String s:getPoints().keySet())removePoint(s);	
	}
	
	public void incrementPoints(String goal)
	{
		if(!getPoints().containsKey(goal))return;
		getPoints().put(goal, getPoints(goal)+1);
	}
	
	public String getOpponent(String goal)
	{
		String[] arrayrep = getPoints().keySet().toArray(new String[getPoints().keySet().size()]);
		for(int i=0;i<arrayrep.length;i++)
		{
			if(!arrayrep[i].equals(goal))return arrayrep[i];
		}		
		return goal;
	}
	
	
	//Persistance -----------------------------------------------------------------------------------------
	
	@Override
	protected Map<String, Object> restore(Map<String, Object> info,	Map<String, Object> addinfo)
	{
		Map<String,Object> restored = new HashMap<String,Object>();
		
		restored.put(Fields.Mode.name(), info.get("Mode"));
		restored.put(Fields.Stadium.name(), sb.getStadium((String)info.get("Stadium")));
		restored.put(Fields.Ball.name(), sb.getBall((String)info.get("Ball")));
		restored.put(Fields.Players.name(), info.get("Players"));
		restored.put(Fields.Scoreboards.name(), info.get("Scoreboards"));
		restored.put(Fields.Points.name(), info.get("Points"));
		restored.put(Fields.Autobalance.name(), info.get("Autobalance"));
		
		
		return restored;
	}

	@Override
	protected Map<String, Object> save()
	{
		Map<String,Object> out = new HashMap<String,Object>();
		
		out.put("Mode", getMode());
		out.put("Stadium", getStadium().getName());
		out.put("Ball",    getBall().getName());
		out.put("Players", getPlayerNames());
		out.put("Scoreboards", getScoreboardNames());
		out.put("Points", getPoints());		
		out.put("Autobalance", isAutobalanceON());
		
		return out;
	}
}
