package org.cubeville.cvblocks;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.World;

public class RegionCopier extends BukkitRunnable
{
    private World sourceWorld;
    private World targetWorld;
    //private boolean transparent;

    private int sizeX;
    private int sizeY;
    private int sizeZ;

    private int x = 0;
    private int y = 0;
    private int z = 0;
    
    private int sourceX;
    private int sourceY;
    private int sourceZ;

    private int targetX;
    private int targetY;
    private int targetZ;
    
    public RegionCopier(World sourceWorld, Vector sourceMin, World targetWorld, Vector targetLoc,
                        int sizeX, int sizeY, int sizeZ, boolean sync) {
        targetX = targetLoc.getBlockX();
        targetY = targetLoc.getBlockY();
        targetZ = targetLoc.getBlockZ();

        sourceX = sourceMin.getBlockX();
        sourceY = sourceMin.getBlockY();
        sourceZ = sourceMin.getBlockZ();

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        
        this.sourceWorld = sourceWorld;
        this.targetWorld = targetWorld;

        if(sync) {
            System.out.println("Synchronously copying data");
            for(int x = 0; x <= sizeX; x++) {
                for(int z = 0; z <= sizeZ; z++) {
                    for(int y = 0; y <= sizeY; y++) {
                        Block sourceBlock = sourceWorld.getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                        Block targetBlock = targetWorld.getBlockAt(targetX + x, targetY + y, targetZ + z);
                        targetBlock.setBlockData(sourceBlock.getBlockData());
                    }
                }
            }
        }
        else {
            runTaskTimer(CVBlocks.getInstance(), 1, 1);
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < 10) {
            Block sourceBlock = sourceWorld.getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
            Block targetBlock = targetWorld.getBlockAt(targetX + x, targetY + y, targetZ + z);
            targetBlock.setBlockData(sourceBlock.getBlockData());
            if(++x > sizeX) {
                x = 0;
                if(++z > sizeZ) {
                    z = 0;
                    if(++y > sizeY) {
                        cancel();
                        break;
                    }
                }
            }
        }
    }
}
