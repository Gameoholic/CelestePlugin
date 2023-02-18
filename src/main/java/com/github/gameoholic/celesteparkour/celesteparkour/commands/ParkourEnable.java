package com.github.gameoholic.celesteparkour.celesteparkour.commands;

import com.github.gameoholic.celesteparkour.celesteparkour.BlockSwitcher;
import com.github.gameoholic.celesteparkour.celesteparkour.CelesteParkour;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ParkourEnable implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {
            if (!player.hasPermission("celesteparkour.enable")) {
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

            player.sendMessage(ChatColor.GREEN + "Enabled parkour block switch for region " + region + ".");
            BlockSwitcher.getBlockSwitchers()[region - 1].setEnabled(true);
            CelesteParkour.getPlugin().getConfig().set("SwitchEnabledR" + region, true);
            CelesteParkour.getPlugin().saveConfig();

        }
        else {
            System.out.println("bruh get yo ass on the server");
        }

        return true;

    }
}
