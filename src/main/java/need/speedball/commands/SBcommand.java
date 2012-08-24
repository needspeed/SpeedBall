package need.speedball.commands;

import need.speedball.SpeedBall;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class SBcommand implements CommandExecutor 
{
    protected SpeedBall sb;

    public void setSBInstance(SpeedBall sb) { this.sb = sb; }
    public boolean checkPerms(CommandSender sender, String c,String command)
    {
    	if((!(sender instanceof Player) || sb.perms.hasPerms((Player)sender, c.substring(2)+"."+command))) return true;
    	else sender.sendMessage(ChatColor.RED + "No Permissions");
    	return false;
    }
}