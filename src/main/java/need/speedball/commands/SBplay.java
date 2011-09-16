package need.speedball.commands;

import need.speedball.Game;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SBplay extends SBcommand
{
private enum PlayCommand {START, STOP}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
	{
		PlayCommand playCommand;
		Player player = (Player)sender;
		
        try 
        {
        	playCommand = PlayCommand.valueOf(args[0].toUpperCase());
        } 
        catch (IllegalArgumentException ie) 
        {
            sender.sendMessage("Unknown play command: " + args[0]);
            return true;
        }
        if(!sb.perms.hasPerms(player, "play." +playCommand.name()))
        {
        	sender.sendMessage(ChatColor.RED + "No Permissions");
        	return false;
        }
        switch (playCommand) 
        {
        	case START:  start(player,args[1],args[2]);		break;
        	case STOP:   stop(player);				break;
        }
        
		return true;
	}
	
	private void start(Player p,String ga,String goa)
	{
		Game game = sb.Games.get(ga);
		game.addPlayer(sb.Goals.get(goa),p);
		p.teleport(game.getBall().getLocation());
	}
	
	private void stop(Player p)
	{
		Game game = sb.gu.getGame(p);
		game.remPlayer(p);
		p.getInventory().setChestplate(new ItemStack(0));
	}
}
