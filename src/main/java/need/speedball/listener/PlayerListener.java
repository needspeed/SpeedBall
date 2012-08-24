package need.speedball.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import need.speedball.Game;
import need.speedball.SpeedBall;
import need.speedball.objects.Ball;
import need.speedball.objects.ItemBall;

public class PlayerListener implements Listener
{
	SpeedBall sb;
	
	public PlayerListener(SpeedBall sb)
	{
		this.sb = sb;
	}
	
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		Player p;
		if(event.getEntity() instanceof Player) p = (Player)event.getEntity();
		else return;
		if(sb.getSBplayer(p.getName())!=null)event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player p = event.getPlayer();
		Block b = event.getClickedBlock();
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK)
		{
			Game game = sb.getSBplayer(p.getName()).getGame();
			if(game!=null)
			{
				if(game.getBall().isObject(b))
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
									
					game.getBall().kick(toadd);
				}
			}
			if(event.getItem()!=null && event.getItem().getTypeId()==341)
			{
				sb.addToPlayerCuboids(p, b.getLocation(), 0);
			}
		}
		else if(event.getAction()==Action.LEFT_CLICK_BLOCK)
		{
			if(event.getItem()!=null && event.getItem().getTypeId()==341)
			{
				sb.addToPlayerCuboids(p, b.getLocation(), 1);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		Entity e = event.getRightClicked();	
		Player p = event.getPlayer();		
	
		Game game = sb.getSBplayer(p.getName()).getGame();
		
		if(game!=null)
		{
			if(game.getBall().isObject(e))
			{
				ItemBall b = (ItemBall)game.getBall();
				b.kick(Ball.getVelocity(p, e, 2));
			}
		}
		
		if (p.getItemInHand().getTypeId()==341)
		{
			sb.putPlayerEntity(p.getName(), e);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Entity e = event.getItem();	
		Player p = event.getPlayer();
		Ball b;
		
		if((b = sb.getBall(sb.getBallHash(e)))!=null)
		{			
			Game ga = b.getGame();
			event.setCancelled(true);
			
			if(ga!=null) b.kick(Ball.getVelocity(p, e, 2));
		}
	}
}
