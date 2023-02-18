package com.github.gameoholic.celesteparkour.celesteparkour.commands;

import com.github.gameoholic.celesteparkour.celesteparkour.BlockSwitcher;
import com.github.gameoholic.celesteparkour.celesteparkour.CelesteParkour;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ParkourDisable implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {
            if (!player.hasPermission("celesteparkour.disable")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            }

            UUID uuid = player.getUniqueId();
            HashMap<UUID, Integer> parkourRegions = CelesteParkour.getPlugin().parkourRegions;

            if (!parkourRegions.containsKey(uuid)) {
                player.sendMessage(ChatColor.RED + "Select a parkour region first to edit its status.");
                return true;
            }
            int region = parkourRegions.get(uuid);

            player.sendMessage(ChatColor.RED + "Disabled parkour block switch for region " + region + " and reset blocks.");
            CelesteParkour.getPlugin().getConfig().set("SwitchEnabledR" + region, false);
            CelesteParkour.getPlugin().saveConfig();

            BlockSwitcher.getBlockSwitchers()[region - 1].setEnabled(false); //This disables tasks

            //Remove falling blocks from region:
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof FallingBlock) {
                        FallingBlock fallingBlock = (FallingBlock) entity;
                        if (fallingBlock.hasMetadata("removableFallingBlock") && fallingBlock.getMetadata("removableFallingBlock").get(0).asInt() == region) {
                            entity.remove();
                        }

                    }
                }
            }
            //Cancel block switcher tasks and reset blocks:

            resetBlocks(1, region);
            resetBlocks(2, region);


        }
        else {
            System.out.println("bruh get yo ass on the server");
        }

        return true;


    }


    //Resets all blocks back to their default state
    private void resetBlocks(int blockNumber, int region) {
        CelesteParkour.getPlugin().getConfig().getConfigurationSection("Block" + blockNumber + "LocationsR" + region).getKeys(false).forEach(coordKey ->  {
            String materialValue = (String) CelesteParkour.getPlugin().getConfig().get("Block" + blockNumber + "LocationsR" + region + "." + coordKey);

            String[] coordArray = coordKey.split("_");
            int x = Integer.parseInt(coordArray[0]);
            int y = Integer.parseInt(coordArray[1]);
            int z = Integer.parseInt(coordArray[2]);
            String worldName = CelesteParkour.getPlugin().getConfig().getString("WorldName");



            Bukkit.getServer().getWorld(worldName).getBlockAt(x, y, z).setType(Material.getMaterial(materialValue));


        });
    }
}
