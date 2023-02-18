package com.github.gameoholic.celesteparkour.celesteparkour;

import com.github.gameoholic.celesteparkour.celesteparkour.commands.*;
import com.github.gameoholic.celesteparkour.celesteparkour.listeners.BlockBreakListener;
import com.github.gameoholic.celesteparkour.celesteparkour.listeners.BlockPlaceListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class CelesteParkour extends JavaPlugin {

    private static CelesteParkour plugin;

    //UUID of players and their selected parkour regions
    public HashMap<UUID, Integer> parkourRegions = new HashMap<UUID, Integer>();
    @Override
    public void onEnable() {
        plugin = this;
        //getConfig().options().copyDefaults();
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);

        getCommand("parkourenable").setExecutor(new ParkourEnable());
        getCommand("parkourdisable").setExecutor(new ParkourDisable());
        getCommand("parkourinterval").setExecutor(new ParkourInterval());
        getCommand("parkourwarndelay").setExecutor(new ParkourWarnDelay());
        getCommand("block1").setExecutor(new Block1Command());
        getCommand("block2").setExecutor(new Block2Command());
        getCommand("parkourregion").setExecutor(new ParkourRegion());

        new BlockSwitcher(1, getConfig().getBoolean("SwitchEnabledR1"));
        new BlockSwitcher(2, getConfig().getBoolean("SwitchEnabledR2"));
        new BlockSwitcher(3, getConfig().getBoolean("SwitchEnabledR3"));
        new BlockSwitcher(4, getConfig().getBoolean("SwitchEnabledR4"));
        new BlockSwitcher(5, getConfig().getBoolean("SwitchEnabledR5"));

        //Reset all parkour courses:
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof FallingBlock) {
                    FallingBlock fallingBlock = (FallingBlock) entity;
                    if (fallingBlock.hasMetadata("removableFallingBlock"))
                        entity.remove();
                    }
                }
            }



    }


    public static CelesteParkour getPlugin() {
        return plugin;
    }
    //ctrl+q

}
