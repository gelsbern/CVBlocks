package org.cubeville.cvblocks;

import java.io.File;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;

import org.cubeville.cvblocks.commands.*;

public class CVBlocks extends JavaPlugin {

    private CommandParser commandParser;
    private static CVBlocks instance;
    
    public void onEnable() {
        instance = this;
        commandParser = new CommandParser();
        commandParser.addCommand(new CopyToRegion());
        commandParser.addCommand(new CopyToSelection());
        commandParser.addCommand(new FillRegion());
        commandParser.addCommand(new FillSelection());
        commandParser.addCommand(new LoadRegion());
        commandParser.addCommand(new SaveRegion());
        commandParser.addCommand(new SelectRegion());
        commandParser.addCommand(new SmolCopy());
        File dataFolder = new File(getDataFolder(), "data");
        if(!dataFolder.exists()) dataFolder.mkdirs();
        BlockToolUtil.setDataDirectory(dataFolder);
    }

    public void onDisable() {
        instance = null;
    }

    public static CVBlocks getInstance() {
        return instance;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("cvblocks")) {
            return commandParser.execute(sender, args);
        }
        return false;
    }
        
}
