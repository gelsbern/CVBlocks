package org.cubeville.cvblocks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;

import org.cubeville.cvblocks.commands.*;

public class CVBlocks extends JavaPlugin {

    private CommandParser commandParser;
    
    public void onEnable() {
        commandParser = new CommandParser();
        commandParser.addCommand(new CopyToRegion());
        commandParser.addCommand(new CopyToSelection());
        commandParser.addCommand(new FillRegion());
        commandParser.addCommand(new FillSelection());
        commandParser.addCommand(new SelectRegion());
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("cvblocks")) {
            return commandParser.execute(sender, args);
        }
        return false;
    }
        
}
