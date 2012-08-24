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

import need.speedball.objects.*;
import need.speedball.objects.SBsaveable.DataType;

import org.bukkit.Location;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class Persistence
{
	public static SpeedBall sb;
	public Persistence(SpeedBall instance) 
	{
		 sb = instance;
	}
	
	/*
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
				 List<String> scoreboardstrings = new ArrayList<String>();
				 for(Scoreboard sc:g.getScoreboards())scoreboardstrings.add(sc.getName());
				 gameinf.add(scoreboardstrings);
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
			 
			 List<List<Object>>  scoreboards = new ArrayList<List<Object>> ();
			 for(Scoreboard sc:sb.Scoreboards.values())
			 {
				 Location signloc = sc.getSignLoc();
				 List<Object>   scoreinf = new ArrayList<Object>();
				 scoreinf.add(sc.getName());
				 scoreinf.add(sc.getGame());
				 scoreinf.add(signloc.getWorld().getName());
				 scoreinf.addAll(getCoords(signloc));
				 scoreboards.add(scoreinf);
			 }
			 
			res.put("Games", games);
			res.put("Stadiums", stadiums);
			res.put("Goals", goals);
			res.put("Balls", balls);
			res.put("Scoreboards",scoreboards);
			 
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
			
			for(List<Object> l:res.get("Scoreboards"))
			{
				String name = (String)l.get(0);
				String game = (String)l.get(1);
				String world = (String)l.get(2);
			    Block b = getLocation((String) world,l.get(3),l.get(4),l.get(5)).getBlock();
			    if(b.getType() != Material.SIGN && b.getType() != Material.SIGN_POST) continue;
			    
				Scoreboard sc = new Scoreboard(name,(Sign)b.getState(),game);
				sb.Scoreboards.put(sc.getName(),sc);
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
					if(entity==null)
					{
						entity = entityloc.getWorld().dropItem(entityloc, new ItemStack(Material.SLIME_BALL));
					}
					
					Ball b = new EntityBall(sb, entity,(String)l.get(0));
					sb.Balls.put(b.getName(), b);
				}
			}					
			
			for(List<Object> l:res.get("Games"))
			{
				List<String> scorenames = (List<String>)l.get(3);
				List<Scoreboard>scoreboards = new ArrayList<Scoreboard>();
				for(String s: scorenames)scoreboards.add(sb.Scoreboards.get(s));
				Game g = new Game(sb,(String)l.get(0),sb.Stadiums.get((String)l.get(1)),sb.Balls.get((String)l.get(2)),scoreboards);
				sb.Games.put(g.name, g);
			}			
			
			save();
		}
		catch (FileNotFoundException e){save();load();}
	}
	*/
	
	public void save()
	{
		DumperOptions options = new DumperOptions();
	    options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
		Yaml yaml = new Yaml(options);
		try
		{
			sb.getDataFolder().mkdir();
			FileWriter fw = new FileWriter(sb.getDataFolder()+"/savedata.yml");
					
			Map<String,Object> dump = new HashMap<String,Object>();						
			Map<DataType, Map<String, SBsaveable>> savedata = sb.getSaveData();
			
			for(DataType savename: savedata.keySet())
			{
				if(!savename.isSaveable())continue;
				Map<String, Object> dumpdown1 = new HashMap<String, Object>();
				Map<String, SBsaveable> objects = savedata.get(savename);
				for(String objectsname : objects.keySet())
				{
					dumpdown1.put(objectsname, objects.get(objectsname).saveData());
				}
				dump.put(savename.name(), dumpdown1);
			}
			
			yaml.dump(dump,fw);
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
			File f = new File(sb.getDataFolder() + "/savedata.yml");
			if(!f.exists()){try{f.createNewFile();} catch(IOException e){System.out.println("SpeedBall: Could not create savedata.yml!");}}
			fr = new FileReader(f);
			
			Map<DataType, Map<String, SBsaveable>> savedata = new HashMap<DataType, Map<String, SBsaveable>>();
			
			Map<String,Object> dump = (Map<String,Object>) yaml.load(fr);
			if (dump==null)return;		
			
			for(String typename:dump.keySet())
			{
				DataType type = DataType.valueOf(typename);
				Map<String, Object> dumpdown1 = (Map<String, Object>) dump.get(typename);
				
				Map<String, SBsaveable> savedatadown1 = new HashMap<String, SBsaveable>();
				for(String name:dumpdown1.keySet())
				{
					Map<String,Object> info = (Map<String, Object>) dumpdown1.get(name);
					SBsaveable saveable = SBsaveable.getSaveable(sb,info,type);
					savedatadown1.put(name,saveable);
				}
				savedata.put(type, savedatadown1);
				
			}
			
			sb.loadSaveData(savedata);
			
			save();
		}
		catch (FileNotFoundException e){save();load();}
	}
	

	
	public static List<Object> getNames(List<SBobject> objects)
	{
		List<Object> out = new ArrayList<Object>();
		for(SBobject o:objects) out.add(o.getName());
		return out;
	}

	public static List<Object> getCoords(Location[] l)
	{
		List<Object> corners = new ArrayList<Object>();
		for(Location loc:l)corners.add(loc);
		return corners;		
	}
	
	public static Map<String,Object> getCoords(Location l)
	{
		Map<String,Object> res = new HashMap<String,Object>();
		res.put("World",l.getWorld().getName());
		res.put("X",l.getBlockX());
		res.put("Y",l.getBlockY());
		res.put("Z",l.getBlockZ());
		return res;	
	}

	public static Location getLocation(SpeedBall sb, Map<String,Object> pos)
	{
		return new Location(sb.getServer().getWorld((String)pos.get("World")),(Integer)pos.get("X"),(Integer)pos.get("Y"),(Integer)pos.get("Z"));
	}
	
	@SuppressWarnings("unchecked")
	public static Location[] getLocation(SpeedBall sb, Object/*List<Object>*/ pos)
	{
		List<Object> loclist = (List<Object>)pos;
		Location[] locs = new Location[loclist.size()];
		for(int i=0;i<loclist.size();i++)locs[i] = getLocation(sb,(Map<String,Object>)loclist.get(i));
		return locs;
	}
}