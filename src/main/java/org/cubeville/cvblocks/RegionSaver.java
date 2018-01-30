package org.cubeville.cvblocks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Material;
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

    private Set<Material> filteredMaterials;
    
    private String filename;
    
    private World world;
    
    public RegionSaver(Vector min, Vector max, World world, String filename, Set<Material> filteredMaterials) {
        System.out.println("Start saving region to file " + filename);
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

        this.filteredMaterials = filteredMaterials;
        runTaskTimer(CVBlocks.getInstance(), 1, 1);
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < 10) {
            Block block = world.getBlockAt(x, y, z);
            if(filteredMaterials != null && filteredMaterials.contains(block.getType()) == false) {
                buffer[bufferPointer++] = 0;
                buffer[bufferPointer++] = 0;
            }
            else {
                buffer[bufferPointer++] = (byte) block.getTypeId();
                buffer[bufferPointer++] = block.getData();
            }
            x += 1;
            if(x > max.getBlockX()) {
                x = min.getBlockX();
                z += 1;
                if(z > max.getBlockZ()) {
                    z = min.getBlockZ();
                    y++;
                    System.out.println("Region saver: y = " + y);
                    if(y > max.getBlockY()) {
                        finishThread();
                        break;
                    }
                }
            }
        }
    }

    private void finishThread() {
        System.out.println("Region saver done.");
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
