package need.speedball.commands;

import need.speedball.SpeedBall;

import org.bukkit.command.CommandExecutor;

public abstract class SBcommand implements CommandExecutor 
{
    protected SpeedBall sb;

    public void setSBInstance(SpeedBall sb) { this.sb = sb; }
}