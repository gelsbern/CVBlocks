package org.cubeville.cvblocks.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandParameterVector;
import org.cubeville.commons.commands.CommandResponse;

import org.cubeville.cvblocks.BlockToolUtil;

public class LoadToCoord extends BaseCommand
{
    public LoadToCoord() {
        super("loadcoord");
        addBaseParameter(new CommandParameterString()); // filename
        addBaseParameter(new CommandParameterString()); // world name
        addBaseParameter(new CommandParameterVector()); // min vector
        addBaseParameter(new CommandParameterVector()); // max vector
        addFlag("transparent");
        setPermission("cvtools.loadcoord");
    }

    public CommandResponse execute(CommandSender commandSender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
        throws CommandExecutionException {

        World world = Bukkit.getWorld((String) baseParameters.get(1));
        if(world == null) throw new CommandExecutionException("World does not exist!");
        
        Vector tmin = (Vector) baseParameters.get(2);
        Vector tmax = (Vector) baseParameters.get(3);
        
        BlockToolUtil.loadFromFile(world, tmin, tmax, (String) baseParameters.get(0), flags.contains("transparent"));

        return null;
    }

}
