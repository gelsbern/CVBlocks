package org.cubeville.cvblocks;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.World;

public class RegionLoader extends BukkitRunnable
{
    private Vector min;
    private Vector max;
    private World targetWorld;
    private boolean transparent;
    
    private int x;
    private int y;
    private int z;

    private byte[] buffer;
    private int bufferPointer = 0;
    
    public RegionLoader(Vector min, Vector max, World world, String filename, boolean transparent) {
        this.min = min;
        this.max = max;
        this.transparent = transparent;

        targetWorld = world;
        x = min.getBlockX();
        y = min.getBlockY();
        z = min.getBlockZ();

        Vector size = max.clone();
        size.subtract(min);
        size.add(new Vector(1, 1, 1));
        int bufferSize = size.getBlockX() * size.getBlockY() * size.getBlockZ() * 2;
        buffer = new byte[bufferSize];

        System.out.println("CVBlocks: Load file " + filename);

        FileInputStream file = null;
        try {
            file = new FileInputStream(filename);
            GZIPInputStream stream = null;
            try {
                stream = new GZIPInputStream(file);
                int readTotal = 0;
                while(readTotal < bufferSize) {
                    int readBytes = stream.read(buffer, readTotal, bufferSize - readTotal);
                    if(readBytes == -1 || readBytes == 0) return;
                    readTotal += readBytes;
                }
                if(readTotal != bufferSize) return;
                runTaskTimer(CVBlocks.getInstance(), 1, 1);
            }
            catch (IOException e) { e.printStackTrace(); }
            finally {
                if (stream != null) {
                    try { stream.close(); } catch (IOException e) {}
                }
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally {
            if(file != null) {
                try { file.close(); } catch (IOException e) {}
            }
        }
    }

    public void run() {
        try {
            long startTime = System.currentTimeMillis();
            while(System.currentTimeMillis() - startTime < 10) {
                int mat = buffer[bufferPointer++];
                if(mat < 0) mat += 256;
                byte dat = buffer[bufferPointer++];
                if(transparent == false || mat != 0) {
                    Block block = targetWorld.getBlockAt(x, y, z);
                    //block.setTypeId(mat); // TODO!
                    //block.setData(dat);
                }
                x += 1;
                if(x > max.getBlockX()) {
                    x = min.getBlockX();
                    z += 1;
                    if(z > max.getBlockZ()) {
                        z = min.getBlockZ();
                        y++;
                        if(y > max.getBlockY()) {
                            System.out.println("CVBlocks: Loading file done.");
                            cancel();
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            cancel();
        }
    }
    
}
