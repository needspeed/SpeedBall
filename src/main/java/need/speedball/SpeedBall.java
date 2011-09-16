package need.speedball;

import java.util.HashMap;
import java.util.Map;

import need.speedball.commands.SBcommand;
import need.speedball.commands.SBgame;
import need.speedball.commands.SBlist;
import need.speedball.commands.SBplay;
import need.speedball.commands.SBselect;
import need.speedball.listener.PlayerListener;
import need.speedball.objects.*;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class SpeedBall extends JavaPlugin
{
	public PluginDescriptionFile pdfFile = null;
	PluginManager pm;
	
	PlayerListener playerListener = new PlayerListener(this);
	public GameUtils gu = new GameUtils(this);
	public static PermissionHandler permissionHandler;
	
	public Map<String,Stadium>Stadiums = new HashMap<String,Stadium>();
	public Map<String,Goal>Goals = new HashMap<String,Goal>();
	public Map<String,Ball>Balls = new HashMap<String,Ball>();
	public Map<String,Game>Games = new HashMap<String,Game>();
	public Map<String,Game>RunningGames = new HashMap<String,Game>();
	
	public SBcommand[] commands = new SBcommand[] 
	{
		new SBselect(), new SBgame(), new SBplay(), new SBlist()
	};
	
	@Override
	public void onDisable() {}

	@Override
	public void onEnable()
	{
		pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );        
        pm = getServer().getPluginManager();
        
        registerEvents();
        registerCommands();
        setupPermissions();
	}
	
	private void setupPermissions()
	{
	      Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

	      if (permissionHandler == null) 
	          if (permissionsPlugin != null) 
	              permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	}
	
	private void registerEvents()
	{
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
	}
	
	 private void registerCommands() 
	 {
		 for (SBcommand cmd : commands) 
		 {
			 cmd.setSBInstance(this);
	         getCommand(cmd.getClass().getSimpleName().toLowerCase()).setExecutor(cmd);
	     }
	 }
	 
	public void repairBall(final Ball b)
	{
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
	
			@Override
			public void run()
			{
				b.getBlock().setTypeId(b.id);				
			}
					
		}, 100);
	}

}
