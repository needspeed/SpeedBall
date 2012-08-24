package need.speedball;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class PlayerCom
{
	SpeedBall sb;
	public enum MessageType
	{
		Info(ChatColor.GRAY), Warning(ChatColor.YELLOW), Error(ChatColor.RED);
		
		private final ChatColor color;
		
		MessageType(ChatColor color) 
		{
	        this.color = color;
	    }
		
		public ChatColor getColor()
		{
			return this.color;
		}		
	}	
	
	public PlayerCom(SpeedBall instance)
	{
		sb = instance;
	}
	
	public static void send(MessageType type, CommandSender player, String message, Object... o)
	{
		switch(type)
		{
			case Info: info(player,message,o); 
			case Warning: warn(player,message,o);
			case Error: error(player,message,o);
		}
	}
	
	public static void error(CommandSender player,String message, Object... o)
	{
		if(player!=null)
		player.sendMessage(ChatColor.RED +"SpeedBall: Error: "+ String.format(message,o));
	}
	public static void info(CommandSender player,String message, Object... o)
	{
		if(player!=null)
		player.sendMessage(ChatColor.GRAY + "SpeedBall: Info: "+String.format(message,o));
	}
	public static void warn(CommandSender player,String message, Object... o)
	{
		if(player!=null)
		player.sendMessage(ChatColor.YELLOW +"SpeedBall: Warning: "+ String.format(message,o));
	}
	
	static public void log(MessageType type, String s, Object... o)
	{
		System.out.println("SpeedBall:"+type.getColor()+type.name()+String.format(s,o));
	}
	
	static public String toString(Location loc)
	{
		return "X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ();
	}	
	
}
