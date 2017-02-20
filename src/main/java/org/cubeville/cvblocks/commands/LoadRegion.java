package org.cubeville.cvblocks.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;

import org.cubeville.cvblocks.BlockToolUtil;

public class LoadRegion extends BaseCommand
{
    public LoadRegion() {
        super("loadregion");
        addBaseParameter(new CommandParameterString()); // filename
        addBaseParameter(new CommandParameterString()); // world name
        addBaseParameter(new CommandParameterString()); // region name
        setPermission("cvtools.loadregion");
    }

    public CommandResponse execute(CommandSender commandSender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
        throws CommandExecutionException {

        World world = Bukkit.getWorld((String) baseParameters.get(1));
        if(world == null) throw new CommandExecutionException("World does not exist!");
        
        String regionName = (String) baseParameters.get(2);

        BlockToolUtil.loadRegionFromFile(world, regionName, (String) baseParameters.get(0));
        return null;
    }
}
