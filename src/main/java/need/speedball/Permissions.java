package need.speedball;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;

public class Permissions
{
	public static PermissionHandler permissionHandler;
	SpeedBall sb;
	boolean opstyle;
	
	public Permissions(SpeedBall sb)
	{
		this.sb = sb;
	}
	
	public void setupPermissions()
	{
	      Plugin permissionsPlugin = sb.getServer().getPluginManager().getPlugin("Permissions");

	      if (permissionHandler == null) 
	      {
	          if (permissionsPlugin != null) 
	          {
	              permissionHandler = ((com.nijikokun.bukkit.Permissions.Permissions) permissionsPlugin).getHandler();
	          }
	          else opstyle=true;
	      }
	}

	public boolean hasPerms(Player p,String node)
	{
		if(opstyle) return p.isOp();
		else return permissionHandler.has(p, "speedball."+node);
	}
}
