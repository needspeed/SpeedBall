package need.speedball.objects;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import need.speedball.SpeedBall;
import need.speedball.PlayerCom.MessageType;
import need.speedball.objects.Goal.Field;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class SBplayer extends SBobject
{
	public static AfkCheck afkCheck;
	private Location lastPosition;
	private int idleTime;
	//private boolean idle;
	
	private enum Fields{Name, Game, Player};
	
	public SBplayer(SpeedBall sb, String player)
	{
		super(sb);
		setName(player);
		this.idleTime = 0;
		this.lastPosition = getPlayer().getLocation();
	}	
	
	// Getter/Setter ----------------------------------------------------------------------------------------------------------
	
	public Player getPlayer()
	{
		return sb.getServer().getPlayer((String) data.get(Fields.Player.name()));
	}
	
	@Override
	public void removeGame()
	{
		if(getGame()==null)return;
		((need.speedball.Game)data.remove(Field.Game.name())).removePlayer(this);
	}

	@Override
	public void remove()
	{
		removeGame();
		sb.getAfkCheck().removePlayer(this);
		sb.removePlayer(getName());
	}
	
	public void giveTricot(int id, byte dat)
	{
		//p.getInventory().setChestplate(new ItemStack(GameUtils.chestPlates[stadium.getGoals().indexOf(g)]));	chestplates 	
		getPlayer().getInventory().addItem(getPlayer().getInventory().getHelmet());
		getPlayer().getInventory().setHelmet(new ItemStack(id,1,(short)0,dat));
	}
	
	public void removeTricot()
	{
		getPlayer().getInventory().setHelmet(new ItemStack(0));
	}
	
	// Functions ---- ----------------------------------------------------------------------------------------------------------
	
	public boolean isOnline()
	{
		return getPlayer()!=null;
	}
	
	public long getIdleTime()
	{
		return idleTime;
	}
	
	public boolean checkIdle()
	{
		return (getPlayer().getLocation().equals(lastPosition) || !isOnline());
	}
	
	public int refreshIdleTime(int toAdd)
	{
		if(checkIdle())return (idleTime += toAdd);
		else return 0;
	}

	public static class AfkCheck implements Runnable
	{
		public int threadid;
		List<SBplayer> players;
		int interval;
		
		@Override
		public void run()
		{
			for(SBplayer player:players)
			{
				if(player.refreshIdleTime(interval)>60)player.remove();
			}
		}
		
		public void addPlayer(SBplayer player)
		{
			players.add(player);
		}
		
		public void addPlayers(List<SBplayer> player)
		{
			players.addAll(player);
			HashSet<SBplayer> h = new HashSet<SBplayer>(players);
			players.clear();
			players.addAll(h);
		}
		
		public void removePlayer(SBplayer player)
		{
			players.remove(player);
		}
		
		public void removePlayers(List<SBplayer> player)
		{
			players.removeAll(player);
		}
		
		public void setInterval(int interval)
		{
			this.interval = interval;
		}		
	}
	
	public void send(MessageType type, String message, Object... o)
	{
		switch(type)
		{
			case Info: info(message,o); 
			case Warning: warn(message,o);
			case Error: error(message,o);
		}
	}
	
	public void error(String message, Object... o)
	{
		getPlayer().sendMessage(ChatColor.RED +"SpeedBall: Error: "+ String.format(message,o));
	}
	public void info(String message, Object... o)
	{
		getPlayer().sendMessage(ChatColor.GRAY + "SpeedBall: Info: "+String.format(message,o));
	}
	public void warn(String message, Object... o)
	{
		getPlayer().sendMessage(ChatColor.YELLOW +"SpeedBall: Warning: "+ String.format(message,o));
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected Map<String, Object> restoreObject(Map<String, Object> info, Map<String, Object> addinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Object> saveObject()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
