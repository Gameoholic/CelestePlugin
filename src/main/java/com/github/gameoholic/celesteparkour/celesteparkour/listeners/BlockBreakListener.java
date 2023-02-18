package com.github.gameoholic.celesteparkour.celesteparkour.listeners;

import com.github.gameoholic.celesteparkour.celesteparkour.CelesteParkour;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
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

        int x = e.getBlock().getLocation().getBlockX();
        int y = e.getBlock().getLocation().getBlockY();
        int z = e.getBlock().getLocation().getBlockZ();



        if (material1.contains(e.getBlock().getType())) {
            int region = getBlockLocationRegion(1, x, y, z);
            if (region == -1)
                return;
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Block 1 broken" + ChatColor.GREEN + " (R" + region + ")"));
            removeBlockLocation(1, x, y, z);
        }

        else if (material2.contains(e.getBlock().getType())) {
            int region = getBlockLocationRegion(2, x, y, z);
            if (region == -1)
                return;
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Block 2 broken" + ChatColor.GREEN + " (R" + region + ")"));
            removeBlockLocation(2, x, y, z);
        }
    }

    //Returns -1 if the block is not saved, otherwise returns its region
    private int getBlockLocationRegion(int blockNumber, int x, int y, int z) {

        //We use AtomicInteger since we can't update int in forEach loop
        AtomicInteger region = new AtomicInteger(-1);
        //loop over all regions:
        for (int i = 1; i <= 5; i++) {
            int _i = i;
            CelesteParkour.getPlugin().getConfig().getConfigurationSection("Block" + blockNumber + "LocationsR" + i).getKeys(false).forEach(coordKey ->  {
                String[] coordArray = coordKey.split("_");

                int x0 = Integer.parseInt(coordArray[0]);
                int y0 = Integer.parseInt(coordArray[1]);
                int z0 = Integer.parseInt(coordArray[2]);
                if (x == x0 && y == y0 && z == z0) {
                    region.set(_i);
                }
            });
        }

        return region.get();
    }
    private void removeBlockLocation(int blockNumber, int x, int y, int z) {
        String locationString = x + "_" + y + "_" + z;
        //loop over all regions
        for (int i = 1; i <= 5; i++) {
            int _i = i;
            CelesteParkour.getPlugin().getConfig().getConfigurationSection("Block" + blockNumber + "LocationsR" + i).getKeys(false).forEach(coordKey ->  {
                String[] coordArray = coordKey.split("_");

                int x0 = Integer.parseInt(coordArray[0]);
                int y0 = Integer.parseInt(coordArray[1]);
                int z0 = Integer.parseInt(coordArray[2]);
                if (x == x0 && y == y0 && z == z0) {
                    CelesteParkour.getPlugin().getConfig().set("Block" + blockNumber + "LocationsR" + _i + "." + locationString, null);
                    CelesteParkour.getPlugin().saveConfig();
                }
            });
        }
    }
}
