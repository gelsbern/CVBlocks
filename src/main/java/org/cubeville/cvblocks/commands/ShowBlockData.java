package org.cubeville.cvblocks.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;


public class ShowBlockData extends BaseCommand
{
    public ShowBlockData() {
        super("showblockdata");
        setPermission("cvtools.showblockdata");
    }

    public CommandResponse execute(CommandSender commandSender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
        throws CommandExecutionException {

        Location loc = ((Player)commandSender).getLocation();
        Block block = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
        return new CommandResponse(block.getBlockData().toString());
    }

}
