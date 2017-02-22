package org.cubeville.cvblocks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

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

    private byte[] buffer;
    private int bufferPointer;

    private String filename;
    
    private World world;
    
    public RegionSaver(Vector min, Vector max, World world, String filename) {
        this.min = min;
        this.max = max;
        this.world = world;

        x = min.getBlockX();
        y = min.getBlockY();
        z = min.getBlockZ();

        Vector size = max.clone();
        size.subtract(min);
        size.add(new Vector(1, 1, 1));
        int bufferSize = size.getBlockX() * size.getBlockY() * size.getBlockZ() * 2;
        buffer = new byte[bufferSize];
        bufferPointer = 0;
        this.filename = filename;

        runTaskTimer(CVBlocks.getInstance(), 1, 1);
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < 10) {
            Block block = world.getBlockAt(x, y, z);
            buffer[bufferPointer++] = (byte) block.getTypeId();
            buffer[bufferPointer++] = block.getData();
            x += 1;
            if(x > max.getBlockX()) {
                x = min.getBlockX();
                z += 1;
                if(z > max.getBlockZ()) {
                    z = min.getBlockZ();
                    y++;
                    if(y > max.getBlockY()) {
                        finishThread();
                        break;
                    }
                }
            }
        }
    }

    private void finishThread() {
        cancel();
        FileOutputStream file = null;
        try {
            file = new FileOutputStream(filename);
            GZIPOutputStream stream = null;
            try {
                stream = new GZIPOutputStream(file);
                stream.write(buffer);
            }
            catch (IOException e) {}
            finally {
                if(stream != null) {
                    try { stream.close(); } catch (IOException e) {}
                }
            }
        }
        catch (IOException e) {}
        finally {
            if(file != null) {
                try { file.close(); } catch (IOException e) {}
            }
        }
        
    }
}
