package need.speedball.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import need.speedball.GameUtils;
import need.speedball.objects.Ball;
import need.speedball.objects.BlockBall;
import need.speedball.objects.EntityBall;
import need.speedball.objects.Goal;
import need.speedball.objects.Stadium;
import net.minecraft.server.EntityFallingSand;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SBselect extends SBcommand
{ 
	private enum SelCommand {STADIUM, BALL, DISPLAY, GOAL, CLEAR}
		
	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
	{
		SelCommand selCommand;
		Player player = (Player)sender;
		
        try 
        {
            selCommand = SelCommand.valueOf(args[0].toUpperCase());
        } 
        catch (IllegalArgumentException ie) 
        {
            sender.sendMessage("Unknown selection command: " + args[0]);
            return true;
        }
        if(!sb.perms.hasPerms(player, "selection." + selCommand.name()))
        {
        	sender.sendMessage(ChatColor.RED + "No Permissions");
        	return false;
        }
        switch (selCommand) 
        {
        	case CLEAR:	  clearSelection(player);   break;
        	case STADIUM: stadium(player,args[1],Arrays.copyOfRange(args, 2, args.length));	break;
        	case BALL:	  ball(player,args[1],args[2]);  	break;
        	case DISPLAY: 							break;
        	case GOAL:	  goal(player,args[1]);		break;
        }
        
		return true;
	}
	
	private void clearSelection(Player p)
	{
		sb.playerCuboids.remove(p);
		 
		p.sendMessage("Selection cleared");
	}
	
	private void stadium(Player p,String name, String[] goa)
	{
		List<Goal> goals = new ArrayList<Goal>();
		for(String s:goa) goals.add(sb.Goals.get(s));
		
		sb.Stadiums.put(name, new Stadium(sb.playerCuboids.get(p)[0], sb.playerCuboids.get(p)[1], name, goals ));
		
		for(Goal g:goals)g.setStadium(sb.Stadiums.get(name));
		p.sendMessage("Stadium selected");
	}
	
	private void ball(Player p,String type,String name)
	{
		if(type.equals("block"))
		{
			Location target = sb.playerCuboids.get(p)[0];
			sb.Balls.put(name, new BlockBall(sb,p.getWorld().getBlockAt(target),name));
			p.sendMessage("Ball selected at: " + GameUtils.toString(target));
		}
		
		if(type.equals("entity"))
		{
			
			Ball ball = new EntityBall(sb, sb.playerEntities.get(p), name);
			sb.Balls.put(name, ball);
			p.sendMessage("Selected Ball is a: " + sb.playerEntities.get(p).getClass().getInterfaces()[0].getSimpleName());
		}
		
		if(type.equals("physics"))
		{
			Location pLoc = p.getLocation();
			net.minecraft.server.World world = ((CraftWorld)p.getWorld()).getHandle();
			net.minecraft.server.Entity sand = new EntityFallingSand(world, pLoc.getBlockX(),pLoc.getBlockY()+1,pLoc.getBlockZ(), 12)
			{
				@Override
				public void s_()
				{
					this.move(motX, motY, motZ);
				}
			};
			world.addEntity(sand);
			Ball ball = new EntityBall(sb,sand.getBukkitEntity(),name);
			sb.Balls.put(name, ball);
			p.sendMessage("Selected Ball is a: " + sand.getBukkitEntity().getClass().getInterfaces()[0].getSimpleName());
		}
		
		if(type.equals("item"))
		{
			Entity e = p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.SLIME_BALL,1));	
			Ball ball = new EntityBall(sb,e,name);
			sb.Balls.put(name, ball);
			p.sendMessage("Selected Ball is a: " + "Slimeball");
		}
	}
	
	private void goal(Player p,String name)
	{
		sb.Goals.put(name, new Goal(name,sb.playerCuboids.get(p)[0], sb.playerCuboids.get(p)[1]));
		
		p.sendMessage("Goal selected");
	}
}
