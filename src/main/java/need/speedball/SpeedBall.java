package need.speedball;

import java.util.HashMap;
import java.util.Map;

import need.speedball.commands.SBcommand;
import need.speedball.commands.SBgame;
import need.speedball.commands.SBlist;
import need.speedball.commands.SBpers;
import need.speedball.commands.SBplay;
import need.speedball.commands.SBremove;
import need.speedball.commands.SBselect;
import need.speedball.listener.EntityListener;
import need.speedball.listener.PlayerListener;
import need.speedball.objects.*;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SpeedBall extends JavaPlugin
{
	public PluginDescriptionFile pdfFile = null;
	PluginManager pm;
	
	PlayerListener playerListener = new PlayerListener(this);
	EntityListener entityListener = new EntityListener(this);
	
	public Map<Player,Location[]> playerCuboids = new HashMap<Player,Location[]>();
	public Map<Player,Entity> playerEntities = new HashMap<Player,Entity>();
	
	public GameUtils gu = new GameUtils(this);	
	public Persistence per = new Persistence(this);
	public need.speedball.Permissions perms = new need.speedball.Permissions(this);
	
	public Map<String,Stadium>Stadiums = new HashMap<String,Stadium>();
	public Map<String,Goal>Goals = new HashMap<String,Goal>();
	public Map<String,Ball>Balls = new HashMap<String,Ball>();
	public Map<String,Game>Games = new HashMap<String,Game>();
	public Map<String,Game>RunningGames = new HashMap<String,Game>();
	
	public SBcommand[] commands = new SBcommand[] 
	{
		new SBselect(), new SBgame(), new SBplay(), new SBlist(), new SBpers(), new SBremove()
	};
	
	@Override
	public void onDisable() 
	{
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
	}
	
	private void registerEvents()
	{
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, playerListener , Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Event.Priority.Highest, this);
	}
	
	 private void registerCommands() 
	 {
		 for (SBcommand cmd : commands) 
		 {
			 cmd.setSBInstance(this);
	         getCommand(cmd.getClass().getSimpleName().toLowerCase()).setExecutor(cmd);
	     }
	 }
}
