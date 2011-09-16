package need.speedball;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import need.speedball.objects.Ball;
import need.speedball.objects.BlockBall;
import need.speedball.objects.EntityBall;
import need.speedball.objects.Goal;
import need.speedball.objects.Stadium;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class Persistence
{
	public static SpeedBall sb;
	public Persistence(SpeedBall instance) 
	{
		 sb = instance;
	}
	
	public void save()
	{
		DumperOptions options = new DumperOptions();
	    options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
		Yaml yaml = new Yaml(options);
		try
		{
			sb.getDataFolder().mkdir();
			FileWriter fw = new FileWriter(sb.getDataFolder()+"/games.yml");
					
			Map<String,List<List<Object>>> res = new HashMap<String,List<List<Object>>>();
			
			List<List<Object>>  games = new ArrayList<List<Object>> ();	
			for(Game g:sb.Games.values())
			{
				 List<Object>  gameinf = new ArrayList<Object> ();		 
				 gameinf.add(g.name);
				 gameinf.add(g.getStadium().name);
				 gameinf.add(g.getBall().getName());				 
				 games.add(gameinf);
			}
				 
			List<List<Object>>  stadiums = new ArrayList<List<Object>> ();				
			for(Stadium s: sb.Stadiums.values())
			{
				List<Object>  stadiuminf = new ArrayList<Object> ();
				 Location[] corners = s.getCorners();
				 stadiuminf.add(s.name);
				 stadiuminf.addAll(getCoords(corners));
				 for(Goal g:s.getGoals())stadiuminf.add(g.name);
				 stadiums.add(stadiuminf);
			 }
				 
			 List<List<Object>>  goals = new ArrayList<List<Object>> ();
			 for(Goal go:sb.Goals.values())
			 {
				 Location[] cornersg = go.getCorners();
				 List<Object>   goalinf = new ArrayList<Object>();
				 goalinf.add(go.name);
				 goalinf.add(cornersg[0].getWorld().getName());
				 goalinf.addAll(getCoords(cornersg));
				 goals.add(goalinf);
			 }
			
			 List<List<Object>>  balls = new ArrayList<List<Object>> ();
			 for(Ball b:sb.Balls.values())
			 {
				 List<Object>   ballinf =  new ArrayList<Object> ();			
				 ballinf.add(b.getName());
				 ballinf.add(b.getSource().getWorld().getName());
				 ballinf.add(b.getSource().getBlockX());
				 ballinf.add(b.getSource().getBlockY());
				 ballinf.add(b.getSource().getBlockZ());
				 ballinf.add(b.getSpecials());
				 balls.add(ballinf);
				
			 }
			 
			res.put("Games", games);
			res.put("Stadiums", stadiums);
			res.put("Goals", goals);
			res.put("Balls", balls);
			 
			yaml.dump(res,fw);
			fw.close();
		}
		catch (IOException e){e.printStackTrace();}
	}
	
	@SuppressWarnings("unchecked")
	public void load()
	{
		
		Yaml yaml = new Yaml();
		FileReader fr;
		try
		{
			sb.getDataFolder().mkdir();
			File f = new File(sb.getDataFolder() + "/games.yml");
			if(!f.exists()){try{f.createNewFile();} catch(IOException e){e.printStackTrace();}}
			fr = new FileReader(sb.getDataFolder()+"/games.yml");
			
			Map<String,List<List<Object>> > res = (Map<String,List<List<Object>> >) yaml.load(fr);
			if (res==null)return;			

			for(List<Object> l:res.get("Goals"))
			{
				Goal g = new Goal((String)l.get(0),getLocation(l.get(1),l.get(2),l.get(3),l.get(4)),getLocation(l.get(1),l.get(5),l.get(6),l.get(7)));
				sb.Goals.put(g.name,g);
			}	
			
			for(List<Object> l:res.get("Stadiums"))
			{
				String worldname = sb.Goals.get((String)l.get(7)).getCorners()[0].getWorld().getName();
				List<Goal> goals= new ArrayList<Goal>();
				for(int i=7;i<l.size();i++)
				{
					goals.add(sb.Goals.get((String)l.get(i)));
				}
				Stadium s = new Stadium(getLocation(worldname,l.get(1),l.get(2),l.get(3)),getLocation(worldname,l.get(4),l.get(5),l.get(6)),(String)l.get(0),goals);
				sb.Stadiums.put(s.name, s);
			}
			
			for(List<Object> l:res.get("Balls"))
			{		
				Map<String,Object> Specials = (HashMap<String,Object>)l.get(5);
				if(Specials.get("Type").equals("Block"))
				{
					Ball b = new BlockBall(sb,sb.getServer().getWorld((String)l.get(1)).getBlockAt(getLocation(l.get(1),l.get(2),l.get(3),l.get(4))),(String) l.get(0));
					sb.Balls.put(b.getName(), b);
				}
				else if(Specials.get("Type").equals("Entity"))
				{
					UUID entityuid = UUID.fromString((String) Specials.get("Entity"));
					World world = sb.getServer().getWorld((String) Specials.get("World"));
					Location entityloc = getLocation(l.get(1),l.get(2),l.get(3),l.get(4));
					world.loadChunk(entityloc.getBlockX(), entityloc.getBlockZ());
					//Entity entity = sb.gu.getEntity(entityuid, world.getChunkAt(entityloc));
					Entity entity = sb.gu.getEntity(entityuid, sb.gu.getStadium(entityloc));
					
					Ball b = new EntityBall(sb, entity,(String)l.get(0));
					sb.Balls.put(b.getName(), b);
				}
			}					
			
			for(List<Object> l:res.get("Games"))
			{
				Game g = new Game(sb,(String)l.get(0),sb.Stadiums.get((String)l.get(1)),sb.Balls.get((String)l.get(2)));
				sb.Games.put(g.name, g);
			}			
			
			save();
		}
		catch (FileNotFoundException e){e.printStackTrace();save();}
	}

	List<Integer> getCoords(Location[] l)
	{
		List<Integer> res = new ArrayList<Integer>();
		res.add(l[0].getBlockX());
		res.add(l[0].getBlockY());
		res.add(l[0].getBlockZ());
		res.add(l[1].getBlockX());
		res.add(l[1].getBlockY());
		res.add(l[1].getBlockZ());
		return res;		
	}

	Location getLocation(Object w,Object x,Object y,Object z)
	{
		return new Location(sb.getServer().getWorld((String)w),(Integer)x,(Integer)y,(Integer)z);
	}
}