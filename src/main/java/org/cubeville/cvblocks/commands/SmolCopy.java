package org.cubeville.cvblocks.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import org.cubeville.commons.utils.BlockUtils;

import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.commons.commands.CommandParameterVector;

public class SmolCopy extends BaseCommand
{
    public SmolCopy() {
        super("smolcopy");
        addBaseParameter(new CommandParameterVector());
    }

    @SuppressWarnings("unchecked")
    @Override
    public CommandResponse execute(CommandSender commandSender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
        throws CommandExecutionException {
        if(!(commandSender instanceof Player)) throw new CommandExecutionException("Can only be used by players!");
        Player player = (Player) commandSender;

        Location min = BlockUtils.getWESelectionMin(player);
        Location max = BlockUtils.getWESelectionMax(player);
        World world = min.getWorld();
        
        double blockDist = 0.187;
        double yOffset = 2.7 * blockDist;

        Vector userOffset = (Vector) baseParameters.get(0);

        for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    //if(block.getTypeId() == 0) continue; // TODO
                    Location l = player.getLocation();
                    l.add((x - min.getBlockX()) * blockDist + userOffset.getX() * blockDist,
                          (y - min.getBlockY()) * blockDist - yOffset + userOffset.getY() * blockDist,
                          (z - min.getBlockZ()) * blockDist + userOffset.getZ() * blockDist);
                    l.setPitch(0);
                    l.setYaw(0);
                    player.sendMessage("Create armor stand at " + l);
                    ArmorStand stand = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
                    stand.setGravity(false);
                    stand.setBasePlate(false);
                    stand.setArms(true);
                    stand.setSmall(true);
                    stand.setVisible(false);
                    //ItemStack item = new ItemStack(block.getTypeId(), 1, (short)0, (byte)block.getData()); // TODO these 3 lines
                    //stand.setItemInHand(item);
                    //stand.setRightArmPose(new EulerAngle(Math.toRadians(-15),Math.toRadians(45),Math.toRadians(0)));
                }
            }
        }
        
        Location l = player.getLocation();
        l.setPitch(0);
        l.setYaw(0);

        return null;
    }
}
