package org.cubeville.cvblocks;

import java.io.FileOutputStream;
import java.io.IOException;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.World;

public class RegionSaver extends BukkitRunnable
{
    private Vector min;
    private Vector max;
    
    private int x;
    private int y;
    private int z;

    private FileOutputStream ostream;

    World world;
    
    public RegionSaver(Vector min, Vector max, World world) {
        this.min = min;
        this.max = max;
        this.world = world;
        x = min.getBlockX();
        y = min.getBlockY();
        z = min.getBlockZ();
        try {
            ostream = new FileOutputStream("/tmp/testsave");
        }
        catch(IOException e) {
        }
        runTaskTimer(CVBlocks.getInstance(), 1, 1);
    }

    private void abortThread() {
        try {
            ostream.close();
        }
        catch(IOException e) {}
        cancel();
    }
    
    public void run() {
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < 10) {
            Block block = world.getBlockAt(x, y, z);
            try {
                ostream.write(block.getTypeId());
                ostream.write(block.getData());
            }
            catch(IOException e) {
                System.out.println("IOException, abort");
                abortThread();
                break;
            }
            x += 1;
            if(x > max.getBlockX()) {
                x = min.getBlockX();
                z += 1;
                if(z > max.getBlockZ()) {
                    z = min.getBlockZ();
                    y++;
                    System.out.println("Start layer " + (y - min.getBlockY()) + " of " + (max.getBlockY() - min.getBlockY()));
                    if(y > max.getBlockY()) {
                        System.out.println("Region save finished.");
                        abortThread();
                        break;
                    }
                }
            }
        }
    }
    
}
