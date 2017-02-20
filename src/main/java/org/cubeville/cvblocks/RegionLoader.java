package org.cubeville.cvblocks;

import java.io.FileInputStream;
import java.io.IOException;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.World;

public class RegionLoader extends BukkitRunnable
{
    private Vector min;
    private Vector max;
    World targetWorld;
    boolean transparent;
    
    private int x;
    private int y;
    private int z;

    private FileInputStream istream;

    
    public RegionLoader(Vector min, Vector max, World world, boolean transparent) {
        this.min = min;
        this.max = max;
        this.transparent = transparent;

        targetWorld = world;
        x = min.getBlockX();
        y = min.getBlockY();
        z = min.getBlockZ();

        try {
            istream = new FileInputStream("/tmp/testsave");
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        runTaskTimer(CVBlocks.getInstance(), 1, 1);
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < 10) {
            int mat;
            int dat;
            try {
                mat = istream.read();
                dat = istream.read();
            }
            catch(IOException e) {
                System.out.println("IOException: " + e.getMessage());
                cancel();
                break;
            }
            if(transparent == false || mat != 0) {
                Block block = targetWorld.getBlockAt(x, y, z);
                block.setTypeId(mat);
                block.setData((byte) dat);
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
                        System.out.println("Region loading finished.");
                        //istream.close();
                        cancel();
                        break;
                    }
                }
            }
        }
    }
    
}
