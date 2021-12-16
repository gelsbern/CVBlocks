package org.cubeville.cvblocks.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterVector;
import org.cubeville.commons.commands.CommandResponse;

public class DrawLine extends BaseCommand
 {
    public DrawLine() {
        super("drawline");
        addBaseParameter(new CommandParameterVector());
        addBaseParameter(new CommandParameterVector());
        setPermission("cvtools.drawline");
    }

    int abs(int v) {
        return (v < 0) ? -v : v;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public CommandResponse execute(CommandSender commandSender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
        throws CommandExecutionException {

        if(!(commandSender instanceof Player)) throw new CommandExecutionException("Only players can use this command!");
        Player player = (Player)commandSender;

        World world = player.getLocation().getWorld();

        Vector v1 = (Vector) baseParameters.get(0);
        Vector v2 = (Vector) baseParameters.get(1);

        int x1 = v1.getBlockX();
        int x2 = v2.getBlockX();
        int y1 = v1.getBlockY();
        int y2 = v2.getBlockY();
        int z1 = v1.getBlockZ();
        int z2 = v2.getBlockZ();

        // Bresenham
        int i, dx, dy, dz, l, m, n, x_inc, y_inc, z_inc, err_1, err_2, dx2, dy2, dz2;
        int point[] = new int[3];

        point[0] = x1;
        point[1] = x2;
        point[2] = z1;

        dx = x2 - x1;
        dy = y2 - y1;
        dz = z2 - z1;

        x_inc = (dx < 0) ? -1 : 1;
        l = abs(dx);
        y_inc = (dy < 0) ? -1 : 1;
        m = abs(dy);
        z_inc = (dz < 0) ? -1 : 1;
        n = abs(dz);

        dx2 = l * 2;
        dy2 = m * 2;
        dz2 = n * 2;

        if ((l >= m) && (l >= n)) {
            err_1 = dy2 - l;
            err_2 = dz2 - l;
            for (i = 0; i < l; i++) {
                world.getBlockAt(point[0], point[1], point[2]).setType(Material.BRICKS);
                if (err_1 > 0) {
                    point[1] += y_inc;
                    err_1 -= dx2;
                }
                if (err_2 > 0) {
                    point[2] += z_inc;
                    err_2 -= dx2;
                }
                err_1 += dy2;
                err_2 += dz2;
                point[0] += x_inc;
            }
        } else if ((m >= l) && (m >= n)) {
            err_1 = dx2 - m;
            err_2 = dz2 - m;
            for (i = 0; i < m; i++) {
                world.getBlockAt(point[0], point[1], point[2]).setType(Material.BRICKS);
                if (err_1 > 0) {
                    point[0] += x_inc;
                    err_1 -= dy2;
                }
                if (err_2 > 0) {
                    point[2] += z_inc;
                    err_2 -= dy2;
                }
                err_1 += dx2;
                err_2 += dz2;
                point[1] += y_inc;
            }
        } else {
            err_1 = dy2 - n;
            err_2 = dx2 - n;
            for (i = 0; i < n; i++) {
                world.getBlockAt(point[0], point[1], point[2]).setType(Material.BRICKS);
                if (err_1 > 0) {
                    point[1] += y_inc;
                    err_1 -= dz2;
                }
                if (err_2 > 0) {
                    point[0] += x_inc;
                    err_2 -= dz2;
                }
                err_1 += dy2;
                err_2 += dx2;
                point[2] += z_inc;
            }
        }
        world.getBlockAt(point[0], point[1], point[2]).setType(Material.BRICKS);

        return null;
    }
    
}
