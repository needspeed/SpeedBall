package need.speedball.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import need.speedball.Game;
import need.speedball.SpeedBall;

public class PlayerListener extends org.bukkit.event.player.PlayerListener
{
	SpeedBall sb;
	
	public PlayerListener(SpeedBall sb)
	{
		this.sb = sb;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK)
		{
			if(sb.gu.isBall(event.getClickedBlock()))
			{
				Player p = event.getPlayer();
				Game ga = sb.gu.getGame(p);
					
				if(ga!=null)
				{
					Vector toadd = new Vector();
					Vector blockv = event.getClickedBlock().getLocation().toVector();
					Vector playerv = new Vector();
					playerv.setX(p.getLocation().toVector().getBlockX());
					playerv.setY(p.getLocation().toVector().getBlockY());
					playerv.setZ(p.getLocation().toVector().getBlockZ());
					
					double xtemp = blockv.getX()-playerv.getX();
					double ztemp = blockv.getZ()-playerv.getZ();
					
					toadd.setX(Math.signum(xtemp)*5-xtemp);
					toadd.setY(0);
					toadd.setZ(Math.signum(ztemp)*5-ztemp);
										
					ga.getBall().kick(toadd);
					sb.repairBall(ga.getBall());
				 }
			}
		}
	}
	
}
