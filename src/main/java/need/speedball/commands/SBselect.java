package need.speedball.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import need.speedball.GameUtils;
import need.speedball.SpeedBall;
import need.speedball.objects.Ball;
import need.speedball.objects.Goal;
import need.speedball.objects.Stadium;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBselect extends SBcommand
{ 
	private enum SelCommand {BLOCK1, BLOCK2, STADIUM, BALL, DISPLAY, GOAL, CLEAR}
	private Map<Player,Location[]> playerCuboids = new HashMap<Player,Location[]>();
	
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
        if(!SpeedBall.permissionHandler.has(player, "speedball.select."+selCommand))
        {
        	sender.sendMessage(ChatColor.RED + "No Permissions");
        	return false;
        }
        switch (selCommand) 
        {
        	case BLOCK1:  block1(player);			break;
        	case BLOCK2:  block2(player);	   		break;
        	case CLEAR:	  clearSelection(player);   break;
        	case STADIUM: stadium(player,args[1],Arrays.copyOfRange(args, 2, args.length));	break;
        	case BALL:	  ball(player,args[1]);  	break;
        	case DISPLAY: 							break;
        	case GOAL:	  goal(player,args[1]);		break;
        }
        
		return true;
	}
	
	private void block1(Player p)
	{
		Location target = p.getTargetBlock(null, 1000).getLocation();
		
		if(playerCuboids.containsKey(p)&&playerCuboids.get(p)[1]!=null)
		{
			playerCuboids.put(p, new Location[]{target,playerCuboids.get(p)[1]});
		}
		else playerCuboids.put(p, new Location[]{target,null});		
		
		p.sendMessage("Block 1 is now at: " + GameUtils.toString(target));
	}

	private void block2(Player p)
	{
		Location target = p.getTargetBlock(null, 1000).getLocation();
		
		if(playerCuboids.containsKey(p)&&playerCuboids.get(p)[0]!=null)
		{
			playerCuboids.put(p, new Location[]{playerCuboids.get(p)[0],target});
		}
		else playerCuboids.put(p, new Location[]{null,target});		
		
		p.sendMessage("Block 2 is now at: " + GameUtils.toString(target));
	}

	private void clearSelection(Player p)
	{
		playerCuboids.remove(p);
		
		p.sendMessage("Selection cleared");
	}
	
	private void stadium(Player p,String name, String[] goa)
	{
		Goal[] goas = new Goal[goa.length];
		for(int i=0;i<goa.length;i++) goas[i] = sb.Goals.get(goa[i]);
		
		sb.Stadiums.put(name, new Stadium(sb, playerCuboids.get(p)[0], playerCuboids.get(p)[1], name, goas ));
		
		p.sendMessage("Stadium selected");
	}
	
	private void ball(Player p,String name)
	{
		Block target = p.getTargetBlock(null, 1000);
		sb.Balls.put(name, new Ball(sb,target,name));
		
		p.sendMessage("Ball selected at: " + GameUtils.toString(target.getLocation()));
	}
	
	private void goal(Player p,String name)
	{
		sb.Goals.put(name, new Goal(name,playerCuboids.get(p)[0], playerCuboids.get(p)[1]));
		
		p.sendMessage("Goal selected");
	}
}
