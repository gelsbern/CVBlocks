package org.cubeville.cvblocks.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.util.Vector;
import org.bukkit.command.CommandSender;

import org.cubeville.commons.commands.BaseCommand;

import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandParameterVector;
import org.cubeville.commons.commands.CommandParameterWorld;
import org.cubeville.commons.commands.CommandResponse;

import org.cubeville.cvblocks.BlockToolUtil;

public class CopyToCoord extends BaseCommand
{
    public CopyToCoord() {
        super("copytocoord");
        addBaseParameter(new CommandParameterWorld()); // world of source region
        addBaseParameter(new CommandParameterString()); // name of source region
        addBaseParameter(new CommandParameterWorld()); // name of target world
        addBaseParameter(new CommandParameterVector()); // coordinates (min.) to copy to
        addFlag("sync");
        setPermission("cvtools.copytocoord");
    }

    public CommandResponse execute(CommandSender commandSender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
        throws CommandExecutionException {

        World sourceWorld = (World) baseParameters.get(0);
        String regionName = (String) baseParameters.get(1);

        World targetWorld = (World) baseParameters.get(2);
        Vector tmin = (Vector) baseParameters.get(3);

        BlockToolUtil.copyToCoord(sourceWorld, regionName, targetWorld, tmin, flags.contains("sync"));

        return null;
    }
}
