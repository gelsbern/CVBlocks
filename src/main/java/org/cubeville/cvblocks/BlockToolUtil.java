package org.cubeville.cvblocks;

import java.io.File;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

    public static void killEntities(World world, String regionName, EntityType entityType) {
        Vector rmin = BlockUtils.getWGRegionMin(world, regionName);
        Vector rmax = BlockUtils.getWGRegionMax(world, regionName);

        List<Entity> entities = world.getEntities();
        for(Entity entity: entities) {
            if(entity.getType() == entityType) {
                Vector loc = entity.getLocation().toVector();
                if(loc.getX() >= rmin.getX() && loc.getX() <= rmax.getX() + 1 &&
                   loc.getY() >= rmin.getY() && loc.getY() <= rmax.getY() + 1 &&
                   loc.getZ() >= rmin.getZ() && loc.getZ() <= rmax.getZ() + 1) {
                    entity.remove();
                }
            }
            
        }
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
        // if(material.getDataValue() != null) { // TODO
        //     block.setData((byte) ((int) material.getDataValue()));
        // }
        // else {
        //     block.setData((byte) 0);
        // }
    }

    public static void copyToCoord(World sourceWorld, String regionName, World targetWorld, Vector tmin, boolean sync) {
        Vector smin = BlockUtils.getWGRegionMin(sourceWorld, regionName);
        Vector smax = BlockUtils.getWGRegionMax(sourceWorld, regionName);

        int sizex = smax.getBlockX() - smin.getBlockX();
        int sizey = smax.getBlockY() - smin.getBlockY();
        int sizez = smax.getBlockZ() - smin.getBlockZ();

        System.out.println("Creating region copyer: " + sync);
        new RegionCopier(sourceWorld, smin, targetWorld, tmin, sizex, sizey, sizez, sync);
    }

    public static void copyToCoord(World sourceWorld, String regionName, World targetWorld, Vector tmin) {
        copyToCoord(sourceWorld, regionName, targetWorld, tmin, false);
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
                    targetBlock.setBlockData(sourceBlock.getBlockData());
                    //targetBlock.setType(sourceBlock.getType());
                    //BlockState data = sourceBlock.getState();
                    //if(data instanceof Skull) {
                        // targetBlock.setData(sourceBlock.getData()); // TODO: Should be changed with materialdata probably?
                        // Skull sourceSkull = (Skull) data;
                        // Skull targetSkull = (Skull) targetBlock.getState();
                        // targetSkull.setSkullType(sourceSkull.getSkullType());
                        // targetSkull.setRotation(sourceSkull.getRotation());
                        // if(sourceSkull.getSkullType() == SkullType.PLAYER) {
                        //     targetSkull.setOwner(sourceSkull.getOwner());
                        // }
                        // targetSkull.update();
                    //}
                    //else {
                        // targetBlock.setData(sourceBlock.getData()); // TODO
                    //}
                }
            }
        }
    }

    public static void saveRegionToFile(World sourceWorld, String sourceRegionName, String filename, Set<Material> filteredMaterials) {
        Vector smin = BlockUtils.getWGRegionMin(sourceWorld, sourceRegionName);
        Vector smax = BlockUtils.getWGRegionMax(sourceWorld, sourceRegionName);
        File file = new File(dataDirectory, filename + ".cvblocks.gz");
        new RegionSaver(smin, smax, sourceWorld, file.getAbsolutePath(), filteredMaterials);
    }

    public static void loadRegionFromFile(World targetWorld, String targetRegionName, String filename, boolean transparent) {
        Vector tmin = BlockUtils.getWGRegionMin(targetWorld, targetRegionName);
        Vector tmax = BlockUtils.getWGRegionMax(targetWorld, targetRegionName);
        loadFromFile(targetWorld, tmin, tmax, filename, transparent);
    }

    public static void loadFromFile(World targetWorld, Vector tmin, Vector tmax, String filename, boolean transparent) {
        File file = new File(dataDirectory, filename + ".cvblocks.gz");
        new RegionLoader(tmin, tmax, targetWorld, file.getAbsolutePath(), transparent);
    }
    
}
