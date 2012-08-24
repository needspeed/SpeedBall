package need.speedball;

import org.bukkit.entity.Player;

public class Tutorial
{
	Player player;
	SpeedBall sb;
	String[] steps;
	int step;
	
	public Tutorial(SpeedBall sb, Player p, int step)
	{
		this.sb = sb;
		this.player = p;
		setSteps();
		this.step = step % steps.length;
		nextStep();
	}
	
	public Tutorial(SpeedBall sb, Player p)
	{
		this.sb = sb;
		this.player = p;
		setSteps();
		this.step = 0;
		nextStep();
	}
	
	public void setSteps()
	{
		this.steps = new String[]
		{
			"First take a slimeball and leftclick the corner of your goal, then rightclick the other corner of your goal.Then type /sbselect goal <goalname>",
			"Now repeat this step with another goal.",
			"This time you do the same with the corners of the stadium.Then type in /sbselect stadium <stadiumname> <goal1name> <goal2name>{Enter}",
			"Now, to spawn a ball, use the command /sbselect ball item <ballname>",
			"It's time to create a game. Type: /sbgame create <gamename> <stadiumname> <ballname>",
			"If all players are in the stadium type this command to add them: /sbgame autoaddplayers <gamename>",
			"The only thing left is to start the game: /sbgame start <gamename>"
		};
	}
	
	public int getStepsCount()
	{
		return steps.length;
	}
	
	public void nextStep()
	{
		player.sendMessage(step + ": " + steps[step]); 
		step++;
		if(step>=steps.length)finalizeTut();			
	}
	
	public void finalizeTut()
	{	
		player.sendMessage("Congratulations you set up a SpeedBall game");
		sb.removePlayerTutorial(player.getName());
	}
	
	public void setStep(int step)
	{
		this.step = step;
	}
}
