package org.cubeville.cvblocks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import org.cubeville.commons.utils.BlockUtils;

public class BlockToolUtil
{
    private static Random random = new Random();
    private static WorldGuardPlugin worldGuard = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
    private static File dataDirectory = null;

    public static void setDataDirectory(File directory) {
        dataDirectory = directory;
    }
    
    public static void fillRegion(World world, String region, List<WeightedMaterial> materialList, List<WeightedMaterial> replacedMaterialList) {
        List<Block> blocks = BlockUtils.getBlocksInWGRegion(world, region, 50000);

        for(Block block: blocks) {
            if(isBlockOfType(block, replacedMaterialList)) {
                setBlock(block, materialList);
            }
        }
    }

    private static boolean isBlockOfType(Block block, List<WeightedMaterial> materialList) {
        if(materialList == null || materialList.size() == 0) return true;
        for(WeightedMaterial material: materialList) {
            if(material.getMaterial() == block.getType()) {
                if(material.getDataValue() == null || material.getDataValue() == block.getData()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static void setBlock(Block block, List<WeightedMaterial> materialList) {
        if(materialList.size() == 1) {
            setBlock(block, materialList.get(0));
            return;
        }
        int percentageSum = 0;
        for(WeightedMaterial material: materialList) {
            percentageSum += material.getPercentage();
        }
        int rnd = random.nextInt(percentageSum);
        int percentageVal = 0;
        for(WeightedMaterial material: materialList) {
            if(rnd >= percentageVal && rnd < percentageVal + material.getPercentage()) {
                setBlock(block, material);
            }
            percentageVal += material.getPercentage();
        }
    }
    
    private static void setBlock(Block block, WeightedMaterial material) {
        block.setType(material.getMaterial());
        if(material.getDataValue() != null) {
            block.setData((byte) ((int) material.getDataValue()));
        }
        else {
            block.setData((byte) 0);
        }
    }

    public static void copyRegion(World sourceWorld, String sourceRegionName, World targetWorld, String targetRegionName) {
        Vector smin = BlockUtils.getWGRegionMin(sourceWorld, sourceRegionName);
        Vector smax = BlockUtils.getWGRegionMax(sourceWorld, sourceRegionName);

        int sminx = smin.getBlockX();
        int sminy = smin.getBlockY();
        int sminz = smin.getBlockZ();

        int ssizex = smax.getBlockX() - sminx + 1;
        int ssizey = smax.getBlockY() - sminy + 1;
        int ssizez = smax.getBlockZ() - sminz + 1;

        Vector tmin = BlockUtils.getWGRegionMin(targetWorld, targetRegionName);
        Vector tmax = BlockUtils.getWGRegionMax(targetWorld, targetRegionName);
        
        int tminx = tmin.getBlockX();
        int tminy = tmin.getBlockY();
        int tminz = tmin.getBlockZ();

        int tsizex = tmax.getBlockX() - tminx + 1;
        int tsizey = tmax.getBlockY() - tminy + 1;
        int tsizez = tmax.getBlockZ() - tminz + 1;

        int sizex = Math.min(ssizex, tsizex);
        int sizey = Math.min(ssizey, tsizey);
        int sizez = Math.min(ssizez, tsizez);
        
        for(int x = 0; x < sizex; x++) {
            for(int y = 0; y < sizey; y++) {
                for(int z = 0; z < sizez; z++) {
                    Block targetBlock = targetWorld.getBlockAt(tminx + x, tminy + y, tminz + z);
                    Block sourceBlock = sourceWorld.getBlockAt(sminx + x, sminy + y, sminz + z);
                    targetBlock.setType(sourceBlock.getType());
                    BlockState data = sourceBlock.getState();
                    if(data instanceof Skull) {
                        targetBlock.setData(sourceBlock.getData()); // TODO: Should be changed with materialdata probably?
                        Skull sourceSkull = (Skull) data;
                        Skull targetSkull = (Skull) targetBlock.getState();
                        targetSkull.setSkullType(sourceSkull.getSkullType());
                        targetSkull.setRotation(sourceSkull.getRotation());
                        if(sourceSkull.getSkullType() == SkullType.PLAYER) {
                            targetSkull.setOwner(sourceSkull.getOwner());
                        }
                        targetSkull.update();
                    }
                    else {
                        targetBlock.setData(sourceBlock.getData());
                    }
                }
            }
        }
    }

    public static void saveRegionToFile(World sourceWorld, String sourceRegionName, String filename) {
        Vector smin = BlockUtils.getWGRegionMin(sourceWorld, sourceRegionName);
        Vector smax = BlockUtils.getWGRegionMax(sourceWorld, sourceRegionName);
        File file = new File(dataDirectory, filename + ".cvblocks.gz");
        new RegionSaver(smin, smax, sourceWorld, file.getAbsolutePath());
    }

    public static void loadRegionFromFile(World targetWorld, String targetRegionName, String filename, boolean transparent) {
        Vector tmin = BlockUtils.getWGRegionMin(targetWorld, targetRegionName);
        Vector tmax = BlockUtils.getWGRegionMax(targetWorld, targetRegionName);
        File file = new File(dataDirectory, filename + ".cvblocks.gz");
        new RegionLoader(tmin, tmax, targetWorld, file.getAbsolutePath(), transparent);
    }
    
}
