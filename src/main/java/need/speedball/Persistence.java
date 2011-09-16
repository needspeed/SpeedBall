package need.speedball;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import need.speedball.objects.Ball;
import need.speedball.objects.Goal;
import need.speedball.objects.Stadium;

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
	
	public void save()
	{
		DumperOptions options = new DumperOptions();
	    options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
		Yaml yaml = new Yaml(options);
		try
		{
			sb.getDataFolder().mkdir();
			FileWriter fw = new FileWriter(sb.getDataFolder()+"/games.yml");
					
			Map<String,ArrayList<Object>> res = new HashMap<String,ArrayList<Object>>();
			
			List<List<Object>> games = new ArrayList<List<Object>>();	
			for(Game g:sb.Games.values())
			{
				 List<Object> gameinf = new ArrayList<Object>();		 
				 gameinf.add(g.name);
				 gameinf.add(g.getStadium().name);
				 gameinf.add(g.getBall().name);				 
				 games.add(gameinf);
			}
				 
			List<List<Object>> stadiums = new ArrayList<List<Object>>();				
			for(Stadium s: sb.Stadiums.values())
			{
				 List<Object> stadiuminf = new ArrayList<Object>();
				 Location[] corners = s.getCorners();
				 stadiuminf.add(s.name);
				 stadiuminf.addAll(getCoords(corners));
				 for(Goal g:s.getGoals())stadiuminf.add(g.name);
				 stadiums.add(stadiuminf);
			 }
				 
			 List<List<Object>> goals = new ArrayList<List<Object>>();
			 for(Goal go:sb.Goals.values())
			 {
				 Location[] cornersg = go.getCorners();
				 List<Object> goalinf = new ArrayList<Object>();
				 goalinf.add(go.name);
				 goalinf.addAll(getCoords(cornersg));
				 goals.add(goalinf);
			 }
			
			 List<List<Object>> balls = new ArrayList<List<Object>>();
			 for(Ball b:sb.Balls.values())
			 {
				 List<Object> ballinf = new ArrayList<Object>();			
				 ballinf.add(b.name);
				 ballinf.add(b.source.getBlockX());
				 ballinf.add(b.source.getBlockY());
				 ballinf.add(b.source.getBlockZ());
				 ballinf.add(b.getBlock().getTypeId());
				 ballinf.add(b.getBlock().getData());
				 balls.add(ballinf);
			 }
			 
			yaml.dump(res,fw);
			fw.close();
		}
		catch (IOException e){e.printStackTrace();}
	}
	
	public void load()
	{
		
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
}
