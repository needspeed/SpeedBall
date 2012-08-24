package need.speedball.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
public class SBpers extends SBcommand
{
	private enum ECommand {SAVE, LOAD}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args)
	{
		ECommand eCommand;
		
        try 
        {
        	eCommand = ECommand.valueOf(args[0].toUpperCase());
        } 
        catch (IllegalArgumentException ie) 
        {
            sender.sendMessage("Unknown pers command: " + args[0]);
            return true;
        }
        if(!checkPerms(sender,this.getClass().getSimpleName(),eCommand.name()))return true;
        switch (eCommand) 
        {
        	case SAVE:  sb.per.save();		break;
        	case LOAD:  sb.per.load(); 		break;
        }        
		return true;
	}

}
