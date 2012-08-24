package need.speedball.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import need.speedball.PlayerCom;
import need.speedball.Tutorial;
import need.speedball.objects.Ball;
import need.speedball.objects.BlockBall;
import need.speedball.objects.ItemBall;
import need.speedball.objects.Goal;
import need.speedball.objects.Scoreboard;
import need.speedball.objects.Stadium;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SBselect extends SBcommand
{ 
	private enum ECommand {STADIUM, BALL, GOAL, SCOREBOARD, CLEAR, TUTORIAL}
		
	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
	{
		ECommand eCommand;
		Player player = (Player)sender;
		
		if(args.length<1)
		{
			PlayerCom.error(player, "Specify a subcommand");
			return true;
		}	

		String[] nargs = Arrays.copyOfRange(args, 1,args.length);
		
        try 
        {
            eCommand = ECommand.valueOf(args[0].toUpperCase());
        } 
        catch (IllegalArgumentException ie) 
        {
            sender.sendMessage("Unknown selection command: " + args[0]);
            return true;
        }
        if(!checkPerms(sender,this.getClass().getSimpleName(),eCommand.name()))return true;
        
        
        if(eCommand != ECommand.CLEAR && eCommand != ECommand.TUTORIAL && !(eCommand == ECommand.BALL && nargs.length>=1 && nargs[0].equalsIgnoreCase("item")))
        {
    		if(sb.getPlayerCuboid(player.getName())==null)
    		{
    			PlayerCom.error(player, "Please select first your corners.");
    			return true;
    		}
        }
        switch (eCommand) 
        {
        	case CLEAR:	  clearSelection(player);   break;
        	case STADIUM: stadium(player,nargs);		break;
        	case BALL:	  ball(player,nargs);  		break;
        	case GOAL:	  goal(player,nargs);		break;
        	case SCOREBOARD: scoreboard(player,nargs); break;
        	case TUTORIAL: turorial(player,nargs); 	break;
        }
        
		return true;
	}

	private void clearSelection(Player p)
	{
		sb.removePlayerCuboid(p.getName());
		 
		p.sendMessage("Selection cleared");
	}
	
	private void stadium(Player p,String[] args)
	{
		if(args.length<3)
		{
			PlayerCom.error(p, "Missing Arguments");
			return;
		}
		String name = args[0]; 
		String[] goa = Arrays.copyOfRange(args, 1, args.length);
		
		List<Goal> goals = new ArrayList<Goal>();
		for(String s:goa)
		{
			Goal goo;
			if((goo = sb.getGoal(s))==null)
			{
				PlayerCom.error(p, "Goal " + s + " does not exist!");
				return;
			}
			goals.add(goo);
		}
		
		Stadium thisstad = new Stadium(sb,sb.getPlayerCuboid(p.getName())[0], sb.getPlayerCuboid(p.getName())[1], name, goals );
		
		sb.addStadium(thisstad);
		
		for(Goal g:goals)g.setStadium(thisstad);
		p.sendMessage("Stadium selected");
		sb.continueTutorial(p);
	}
	
	private void ball(Player p,String[] args)
	{
		if(args.length<2)
		{
			PlayerCom.error(p, "Missing Arguments");
			return;
		}
		String type = args[0];
		String name = args[1];
		
		if(type.equals("block"))
		{
			Location target = sb.getPlayerCuboid(p.getName())[0];
			Block block = target.getBlock();
			BlockBall blockball = new BlockBall(sb, block,target,name, block.getTypeId(), block.getData());
			sb.addBall(blockball);
			PlayerCom.info(p, "Ball selected at: " + PlayerCom.toString(target));
		}
		
		if(type.equals("entity"))
		{
			Entity entity = sb.getPlayerEntity(p.getName());
			Ball entityball = new ItemBall(sb, entity, entity.getLocation(), name);
			sb.addBall(entityball);
			PlayerCom.info(p, "Selected Ball is a: " + sb.getPlayerEntity(p.getName()).getClass().getInterfaces()[0].getSimpleName());
		}
		
		/*if(type.equals("physics"))
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
		}*/
		
		if(type.equals("item"))
		{
			Entity item = p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(p.getItemInHand().getType(),1));	
			Ball itemball = new ItemBall(sb, item, item.getLocation(), name);
			sb.addBall(itemball);
			PlayerCom.info(p, "Selected Ball is a: " + p.getItemInHand().getType().name());
		}
		sb.continueTutorial(p);
	}
	
	private void goal(Player p,String[] args)
	{
		if(args.length!=1)
		{
			PlayerCom.error(p, "Argument has to be name!");
			return;
		}
		String name = args[0];
		
		Goal goal = new Goal(sb, name,sb.getPlayerCuboid(p.getName())[0], sb.getPlayerCuboid(p.getName())[1]);
		sb.addGoal(goal);		
		PlayerCom.info(p, "Goal selected");		
		sb.continueTutorial(p);
	}
	
	private void scoreboard(Player p, String[] args)
	{
		if(args.length!=1)
		{
			PlayerCom.error(p, "Argument has to be name!");
			return;
		}
		String name = args[0];
		
		Block signblock = sb.getPlayerCuboid(p.getName())[1].getBlock();
		if (signblock.getType() != Material.SIGN && signblock.getType() != Material.SIGN_POST)
		{
			PlayerCom.error(p, "Your selected Block is not a sign. Select a new one through leftclicking it with a slimeball!");
			return;
		}
		
		Scoreboard sc = new Scoreboard(sb,name,(Sign)signblock.getState());
		sb.addScoreboard(sc);
		PlayerCom.info(p, "Scoreboard selected");
	}

	private void turorial(Player p,String[] args)
	{
		int step=0;
		if(args.length<1)step=0;
		else
		{
			try{step = Integer.decode(args[0]);}
			catch(Exception e){step = 0;}
		}
		Tutorial t = new Tutorial(sb,p,step);
		PlayerCom.info(p, "Tutorial started at step: " + step);
		sb.putPlayerTutorial(p.getName(), t);
	}
}
