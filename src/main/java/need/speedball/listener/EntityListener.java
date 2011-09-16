package need.speedball.listener;

import org.bukkit.event.entity.EntityTargetEvent;

import need.speedball.SpeedBall;

public class EntityListener extends org.bukkit.event.entity.EntityListener
{
	SpeedBall sb;
	
	public EntityListener(SpeedBall instance)
	{
		sb = instance;
	}
	
	public void onEntityTarget(EntityTargetEvent event)
	{
		if(sb.gu.isBall(event.getEntity()))
		{
			event.setCancelled(true);
			sb.getServer().broadcastMessage("Cancelled");
		}
		//System.out.println(event.isCancelled());
	}
}
