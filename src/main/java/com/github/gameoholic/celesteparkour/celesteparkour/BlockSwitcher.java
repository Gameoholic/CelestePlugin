package com.github.gameoholic.celesteparkour.celesteparkour;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockSwitcher {

    private static BlockSwitcher[] blockSwitchers = new BlockSwitcher[5];
    private int region;
    private boolean enabled;

    private BukkitTask blockSwitcherTask = null;

    public BlockSwitcher(int region, boolean enabled) {
        blockSwitchers[region - 1] = this;
        this.region = region;
        this.enabled = enabled;

        int interval = CelesteParkour.getPlugin().getConfig().getInt("SwitchIntervalR" + region);

        if (enabled) {
            blockSwitcherTask = CelesteParkour.getPlugin().getServer().getScheduler().runTaskTimer(CelesteParkour.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    onSwitchBlocksTimeout(interval);
                }
            }, interval, interval);
        }
    }
    int safeBlockNumber = 1; //The number (1/2) of the blocks that are currently safe to stand on, AKA not falling blocks
    private void onSwitchBlocksTimeout(int interval) {
        //Switch blocks -> Create Warn Blocks -> Change Safe Block Number

        switchBlocks(1, interval);
        switchBlocks(2, interval);

        int warnDelay = CelesteParkour.getPlugin().getConfig().getInt("SwitchWarnDelayR" + region);

        if (warnDelay > 0) //Warn can be disabled by setting to -1. Can be 0 in edge case.

            Bukkit.getScheduler().runTaskLater(CelesteParkour.getPlugin(), new Runnable() {
                public void run() {
                    createWarnBlocks();
                    if (safeBlockNumber == 1)
                        safeBlockNumber = 2;
                    else
                        safeBlockNumber = 1;
                }
            }, warnDelay);

        else {
            if (safeBlockNumber == 1)
                safeBlockNumber = 2;
            else
                safeBlockNumber = 1;
        }

    }


    private void switchBlocks(int blockNumber, int interval) {
        CelesteParkour.getPlugin().getConfig().getConfigurationSection("Block" + blockNumber + "LocationsR" + region).getKeys(false).forEach(coordKey ->  {
            String materialValue = (String) CelesteParkour.getPlugin().getConfig().get("Block" + blockNumber + "LocationsR" + region + "." + coordKey);

            String[] coordArray = coordKey.split("_");
            int x = Integer.parseInt(coordArray[0]);
            int y = Integer.parseInt(coordArray[1]);
            int z = Integer.parseInt(coordArray[2]);
            String worldName = CelesteParkour.getPlugin().getConfig().getString("WorldName");

            List<String> block1 = CelesteParkour.getPlugin().getConfig().getStringList("Block1");
            List<String> block2 = CelesteParkour.getPlugin().getConfig().getStringList("Block2");
            Material material1 = Material.getMaterial(materialValue);
            Material material2 = Material.getMaterial(block2.get(0));

            //Blocks marked with block1 will be safe, block2's will be falling blocks
            if (safeBlockNumber == 1) {
                if (blockNumber == 1)
                    Bukkit.getServer().getWorld(worldName).getBlockAt(x, y, z).setType(material1);
                else {
                    Bukkit.getServer().getWorld(worldName).getBlockAt(x, y, z).setType(Material.AIR);
                    spawnFakeBlock(x, y, z, material2, interval);
                }
            }
            //Blocks marked with block2 will be safe, block1's will be falling blocks
            else {
                if (blockNumber == 1) {
                    Bukkit.getServer().getWorld(worldName).getBlockAt(x, y, z).setType(Material.AIR);
                    spawnFakeBlock(x, y, z, material2, interval);
                }
                else
                {
                    //Match block2 with block1 equivalent
                    Bukkit.getServer().getWorld(worldName).getBlockAt(x, y, z).setType(Material.getMaterial(block1.get(block2.indexOf(materialValue))));
                }
            }

        });
    }

    private void spawnFakeBlock(int x, int y, int z, Material material, int duration) {
        String worldName = CelesteParkour.getPlugin().getConfig().getString("WorldName");
        World world = Bukkit.getServer().getWorld(worldName);
        Location location = new Location(world, x, y, z);
        BlockData blockData = Bukkit.getServer().createBlockData(material);

        //We spawn each falling block X (duration) times, once for each tick.
        new BukkitRunnable()
        {
            int tick = 0;
            FallingBlock fallingBlock = null;

            public void run()
            {
                if (fallingBlock != null) {
                    fallingBlock.remove();
                }

                if (tick >= duration || !enabled) {
                    this.cancel();
                    return;
                }

                fallingBlock = world.spawnFallingBlock(location, blockData);
                fallingBlock.setDropItem(false);
                fallingBlock.setGravity(false);
                fallingBlock.setHurtEntities(false);
                fallingBlock.setInvulnerable(true);
                fallingBlock.setPersistent(true);
                fallingBlock.setSilent(true);
                fallingBlock.setMetadata("removableFallingBlock", new FixedMetadataValue(CelesteParkour.getPlugin(), region));

                tick++;
            }
        }.runTaskTimer(CelesteParkour.getPlugin(), 0L, 1L);

    }

    private void createWarnBlocks() {
        if (!enabled)
            return;
        CelesteParkour.getPlugin().getConfig().getConfigurationSection("Block" + safeBlockNumber + "LocationsR" + region).getKeys(false).forEach(coordKey ->  {
            String materialValue = (String) CelesteParkour.getPlugin().getConfig().get("Block" + safeBlockNumber + "LocationsR" + region + "." + coordKey);

            String[] coordArray = coordKey.split("_");
            int x = Integer.parseInt(coordArray[0]);
            int y = Integer.parseInt(coordArray[1]);
            int z = Integer.parseInt(coordArray[2]);
            String worldName = CelesteParkour.getPlugin().getConfig().getString("WorldName");

            String block3 = CelesteParkour.getPlugin().getConfig().getString("Block3");

            Material material3 = Material.getMaterial(block3);


            Bukkit.getServer().getWorld(worldName).getBlockAt(x, y, z).setType(material3);

        });
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (!enabled && blockSwitcherTask != null) {
            blockSwitcherTask.cancel();
        }

        int interval = CelesteParkour.getPlugin().getConfig().getInt("SwitchIntervalR" + region);

        if (enabled && (blockSwitcherTask == null || blockSwitcherTask.isCancelled())) {
            blockSwitcherTask = CelesteParkour.getPlugin().getServer().getScheduler().runTaskTimer(CelesteParkour.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    onSwitchBlocksTimeout(interval);
                }
            }, interval, interval);
        }
    }

    public static BlockSwitcher[] getBlockSwitchers() {
        return blockSwitchers;
    }
    public BukkitTask getBlockSwitcherTask() {
        return blockSwitcherTask;
    }

}
