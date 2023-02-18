package com.github.gameoholic.celesteparkour.celesteparkour.listeners;


import com.github.gameoholic.celesteparkour.celesteparkour.CelesteParkour;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().hasPermission("celesteparkour.general")) {
            return;
        }

        List<String> block1Blocks = CelesteParkour.getPlugin().getConfig().getStringList("Block1");
        List<Material> material1 = new ArrayList<Material>();
        for (String block1Block : block1Blocks) {
            material1.add(Material.getMaterial(block1Block));
        }
        List<String> block2Blocks = CelesteParkour.getPlugin().getConfig().getStringList("Block2");
        List<Material> material2 = new ArrayList<Material>();
        for (String block2Block : block2Blocks) {
            material2.add(Material.getMaterial(block2Block));
        }

        int x = e.getBlockPlaced().getLocation().getBlockX();
        int y = e.getBlockPlaced().getLocation().getBlockY();
        int z = e.getBlockPlaced().getLocation().getBlockZ();

        UUID uuid = e.getPlayer().getUniqueId();
        HashMap<UUID, Integer> parkourRegions = CelesteParkour.getPlugin().parkourRegions;
        if (!parkourRegions.containsKey(uuid))
            return;

        //If the player has a region selected:
        int region = parkourRegions.get(uuid);
        if (material1.contains(e.getBlockPlaced().getType())) {
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Block 1 placed" + ChatColor.GREEN + " (R" + region + ")"));
            StoreBlockLocation(1, e.getBlockPlaced().getType(), x, y, z, region);
        }
        else if (material2.contains(e.getBlockPlaced().getType())) {
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Block 2 placed" + ChatColor.GREEN + " (R" + region + ")"));
            StoreBlockLocation(2, e.getBlockPlaced().getType(), x, y, z, region);
        }

    }


    //Stores block location in config.yml, format is: x_y_z: material.
    //blockNumber - 1/2.
    private void StoreBlockLocation(int blockNumber, Material material, int x, int y, int z, int region) {
        String locationString = x + "_" + y + "_" + z;

        CelesteParkour.getPlugin().getConfig().set("Block" + blockNumber + "LocationsR" + region + "." + locationString, material.toString());
        CelesteParkour.getPlugin().saveConfig();
    }
}
