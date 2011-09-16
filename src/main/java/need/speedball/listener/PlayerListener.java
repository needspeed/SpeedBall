package need.speedball.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import need.speedball.Game;
import need.speedball.SpeedBall;
import need.speedball.objects.EntityBall;

public class PlayerListener extends org.bukkit.event.player.PlayerListener
{
	SpeedBall sb;
	
	public PlayerListener(SpeedBall sb)
	{
		this.sb = sb;
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player p = event.getPlayer();
		Block b = event.getClickedBlock();
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK)
		{
			if(sb.gu.isBall(b))
			{				
				Game ga = sb.gu.getGame(p);
					
				if(ga!=null)
				{
					if(ga.getBall().isObject(b))
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
					}
				}
			}
			else
			{
				if(event.getItem()!=null)
				{
					if(event.getItem().getTypeId()==341)
					{
						sb.gu.addToPlayerCuboids(p, b.getLocation(), 0);
					}
				}
			}
		}
		else if(event.getAction()==Action.LEFT_CLICK_BLOCK)
		{
			if(event.getItem()!=null)
			{
				if(event.getItem().getTypeId()==341)
				{
					sb.gu.addToPlayerCuboids(p, b.getLocation(), 1);
				}
			}
		}
	}
	
	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		Entity e = event.getRightClicked();	
		Player p = event.getPlayer();
		
		if(sb.gu.isBall(e))
		{			
			Game ga = sb.gu.getGame(p);
			
			if(ga!=null)
			{
				if(ga.getBall().isObject(e))
				{
					EntityBall b = (EntityBall)ga.getBall();
					b.kick(sb.gu.getVelocity(p, e, 2));
				}
			}
		}
		else if (event.getPlayer().getItemInHand().getTypeId()==341)
		{
			sb.playerEntities.put(p, e);
		}
	}
	
	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Entity e = event.getItem();	
		Player p = event.getPlayer();
		
		if(sb.gu.isBall(e))
		{			
			Game ga = sb.gu.getGame(p);
			event.setCancelled(true);
			
			if(ga!=null)
			{
				if(ga.getBall().isObject(e))
				{
					EntityBall b = (EntityBall)ga.getBall();
					b.kick(sb.gu.getVelocity(p, e, 2));					
				}
				else p.sendMessage("Ball Entity");
			}
			else p.sendMessage("Ball Entity");
		}
	}
}
