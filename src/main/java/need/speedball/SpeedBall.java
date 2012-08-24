package need.speedball;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import need.speedball.commands.SBcommand;
import need.speedball.commands.SBgame;
import need.speedball.commands.SBlist;
import need.speedball.commands.SBpers;
import need.speedball.commands.SBplay;
import need.speedball.commands.SBremove;
import need.speedball.commands.SBselect;
import need.speedball.listener.PlayerListener;
import need.speedball.objects.*;
import need.speedball.objects.SBplayer.AfkCheck;
import need.speedball.objects.SBsaveable.DataType;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SpeedBall extends JavaPlugin
{
	
	// Setup --------------------------------------------------------------------------------------------------
	
	
	public PluginDescriptionFile pdfFile = null;
	PluginManager pm;
	
	@Override
	public void onDisable() 
	{
		stopAFKcheck();
		per.save();
	}

	@Override
	public void onEnable()
	{
		pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );        
        pm = getServer().getPluginManager();
        
        registerEvents();
        registerCommands();
        perms.setupPermissions();
        
        per.load();
        startAFKcheck();
	}
		
	private void registerEvents()
	{
		pm.registerEvents(playerListener, this);
	}
	
	private void registerCommands() 
	{
		for (SBcommand cmd : commands) 
		{
			cmd.setSBInstance(this);
	        getCommand(cmd.getClass().getSimpleName().toLowerCase()).setExecutor(cmd);
	    }
	}
	
	
	//Fields ---------------------------------------------------------------------------------------------------
		
	PlayerListener playerListener = new PlayerListener(this);
	
	private Map<String,Location[]> playerCuboids = new HashMap<String,Location[]>();
	private Map<String,Entity> playerEntities = new HashMap<String,Entity>();
	private Map<String,Tutorial> playerTuts = new HashMap<String, Tutorial>();
	
	
	public Persistence per = new Persistence(this);
	public need.speedball.Permissions perms = new need.speedball.Permissions(this);
	
	private Map<DataType, Map<String, SBsaveable>> saveData = new EnumMap<DataType, Map<String,SBsaveable>>(DataType.class);

	private Map<Object, String> ballHash = new HashMap<Object, String>();
	
	public SBcommand[] commands = new SBcommand[]
	{ 
			new SBselect(), new SBgame(), new SBplay(), new SBlist(), new SBpers(), new SBremove() 
	};
	
	private AfkCheck afkCheck = new AfkCheck();
		
	//Functions --------------------------------------------------------------------------------------------------

	 
	public void continueTutorial(Player p)
	{
		if(playerTuts.get(p)!=null)
		playerTuts.get(p).nextStep();
	}
	
	public void addToPlayerCuboids(Player p,Location l,int slot)
	{
		String player = p.getName();
		if(getPlayerCuboid(player)!=null&&getPlayerCuboid(player)[slot]!=null)
		{
			putPlayerCuboid(player, new Location[]{l,playerCuboids.get(p)[slot]});
		}
		else playerCuboids.put(p.getName(), new Location[]{l,null});
		
		p.sendMessage("Block " + slot + " is now at: " + PlayerCom.toString(l));
	}
	
	public void startAFKcheck()
	{		
		afkCheck.threadid = getServer().getScheduler().scheduleSyncRepeatingTask(this, afkCheck, 0, 300);
		afkCheck.setInterval(300);
	}
	
	public void stopAFKcheck()
	{
		getServer().getScheduler().cancelTask(afkCheck.threadid);
	}
	
	
	//Getter/Setter ----------------------------------------------------------------------------------------------
	
	public Map<DataType,Map<String, SBsaveable>> getSaveData()
	{
		return new EnumMap<DataType,Map<String, SBsaveable>>(saveData);
	}
	
	public void loadSaveData(Map<DataType,Map<String, SBsaveable>> saveData)
	{
		this.saveData.clear();
		this.saveData.putAll(saveData);
	}
	
	public AfkCheck getAfkCheck()
	{
		return afkCheck;
	}
	
	// Entity ------------------------------------------------------------------------
	
	private Map<String,Entity> getPlayerEntities()
	{
		return playerEntities;
	}
	
	public Entity getPlayerEntity(String player)
	{
		return getPlayerEntities().get(player);
	}
	
	public void putPlayerEntity(String player, Entity entity)
	{
		getPlayerEntities().put(player, entity);
	}
	
	// Cuboids ------------------------------------------------------------------------
	
	private Map<String, Location[]> getPlayerCuboids()
	{
		return playerCuboids;
	}
	
	public Location[] getPlayerCuboid(String player)
	{
		return getPlayerCuboids().get(player);
	}
	
	public void putPlayerCuboid(String player, Location[] cuboid)
	{
		getPlayerCuboids().put(player, cuboid);
	}
	
	public void removePlayerCuboid(String player)
	{
		getPlayerCuboids().remove(player);
	}
	
	// Tutorial ------------------------------------------------------------------------
	
	public void putPlayerTutorial(String player, Tutorial tutorial)
	{
		playerTuts.put(player, tutorial);
	}
	
	public void removePlayerTutorial(String player)
	{
		playerTuts.remove(player);
	}
	
	// Goal ------------------------------------------------------------------------
	
	public Goal getGoal(String goal)
	{
		return (Goal)getGoals().get(goal);
	}
	
	private Map<String, SBsaveable> getGoals()
	{
		return saveData.get(DataType.Goals);
	}
	
	public void addGoal(Goal goal)
	{
		getGoals().put(goal.getName(), goal);
	}
	
	public boolean removeGoal(String goal)
	{
		if(!getGoals().containsKey(goal))return false;
		getGoals().remove(goal).remove();
		return true;
	}
	
	// Stadium ------------------------------------------------------------------------
	
	public Stadium getStadium(String stadium) 
	{ 
		return (Stadium)getStadiums().get(stadium);
	}

	private Map<String, SBsaveable> getStadiums()
	{
		return saveData.get(DataType.Stadiums);
	}
	
	public Collection<SBsaveable> getAllStadiums()
	{
		return saveData.get(DataType.Stadiums).values();
	}

	public void addStadium(Stadium stadium)
	{
		getStadiums().put(stadium.getName(), stadium);
	}

	public boolean removeStadium(String stadium)
	{
		if(!getStadiums().containsKey(stadium))return false;
		getStadiums().remove(stadium).remove();
		return true;
	}

	// Scoreboard ------------------------------------------------------------------------
	
	public Scoreboard getScoreboard(String scoreboard) 
	{ 
		return (Scoreboard)getScoreboards().get(scoreboard);
	}

	private Map<String, SBsaveable> getScoreboards()
	{
		return saveData.get(DataType.Scoreboards);
	}

	public void addScoreboard(Scoreboard scoreboard)
	{
		getScoreboards().put(scoreboard.getName(), scoreboard);
	}

	public boolean removeScoreboard(String scoreboard)
	{
		if(!getScoreboards().containsKey(scoreboard))return false;
		getScoreboards().remove(scoreboard).remove();
		return true;
	}

	// Ball ------------------------------------------------------------------------
	
	public Ball getBall(String ball) 
 	{ 
		return (Ball)getBalls().get(ball);
	}

	private Map<String, SBsaveable> getBalls()
	{
		return saveData.get(DataType.Balls.name());
	}

	public void addBall(Ball ball)
	{
		getBalls().put(ball.getName(), ball);
	}

	public boolean removeBall(String ball)
	{
		if(!getBalls().containsKey(ball))return false;
		getBalls().remove(ball).remove();
		return true;
	}
	
	public Collection<SBsaveable> getAllBalls()
	{
		return getBalls().values();
	}
	
	// Game ------------------------------------------------------------------------
	
	public Game getGame(String game) 
	{ 
		return (Game)getGames().get(game);
	}

	private Map<String, SBsaveable> getGames()
	{
		return saveData.get(DataType.Games);
	}

	public void addGame(Game game)
	{
		getGames().put(game.getName(), game);
	}

	public boolean removeGame(String game)
	{
		if(!getGames().containsKey(game))return false;
		getGames().remove(game).remove();
		return true;
	}
	
	// PlayerGame ------------------------------------------------------------------------
	
	public SBplayer getSBplayer(String player) 
	{ 
		SBplayer sbplayer = (SBplayer)getPlayers().get(player);
		if(sbplayer==null)addPlayer(player);
		return sbplayer;
	}
	
	private void addPlayer(String player)
	{
		if(getPlayers().containsKey(player))return;
		SBplayer sbplayer = new SBplayer(this,player);
		getPlayers().put(player, sbplayer);
	}
	
	private Map<String, SBsaveable> getPlayers()
	{
		return saveData.get(DataType.Players);
	}

	public boolean removePlayer(String player)
	{
		if(!getPlayers().containsKey(player))return false;
		getPlayers().remove(player).remove();
		return true;
	}

	// BallHash ------------------------------------------------------------------------
	
	public String getBallHash(Object object) 
	{ 
		return (String)getBallHashs().get(object);
	}

	private Map<Object, String> getBallHashs()
	{
		return ballHash;
	}

	public void addBallHash(Object object, String ball)
	{
		getBallHashs().put(object, ball);
	}

	public void removeBallHash(Object object)
	{
		getBallHashs().remove(object);
	}
}
