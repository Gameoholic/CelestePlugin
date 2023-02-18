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

public class ParkourInterval implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("celesteparkour.interval")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            }

            UUID uuid = player.getUniqueId();
            HashMap<UUID, Integer> parkourRegions = CelesteParkour.getPlugin().parkourRegions;

            if (!parkourRegions.containsKey(uuid)) {
                player.sendMessage(ChatColor.RED + "Select a parkour region first to edit its values.");
                return true;
            }

            int region = parkourRegions.get(uuid);
            int interval = CelesteParkour.getPlugin().getConfig().getInt("SwitchIntervalR" + region);
            int warnInterval = CelesteParkour.getPlugin().getConfig().getInt("SwitchWarnDelayR" + region);

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Current interval of region " + region + " is " + ChatColor.YELLOW + interval + " ticks (" + interval/20.0 + " seconds).");
                return true;
            }

            int ticks = 0;
            try {
                ticks = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid interval provided. Must be whole number.");
                return true;
            }

            if (ticks <= 0) {
                player.sendMessage(ChatColor.RED + "Invalid interval provided.");
                return true;
            }

            player.sendMessage(ChatColor.GREEN + "Changed parkour region " + region + " interval to " + ChatColor.YELLOW + ticks + " ticks (" + ticks/20.0 + " seconds).");
            if (warnInterval >= interval) {
                player.sendMessage(ChatColor.GREEN + "Changed parkour region " + region + " warn interval to " + ChatColor.YELLOW + (ticks - 1) + " ticks (" + (ticks - 1)/20.0 + " seconds).");
                CelesteParkour.getPlugin().getConfig().set("SwitchWarnDelayR" + region, ticks - 1);
            }
            CelesteParkour.getPlugin().getConfig().set("SwitchIntervalR" + region, ticks);
            CelesteParkour.getPlugin().saveConfig();

            if (CelesteParkour.getPlugin().getConfig().getBoolean("SwitchEnabledR" + region)) {
                //Start task timer with new interval
                BlockSwitcher.getBlockSwitchers()[region - 1].setEnabled(false);
                BlockSwitcher.getBlockSwitchers()[region - 1].setEnabled(true);
            }

        }
        else {
            System.out.println("bruh get yo ass on the server");
        }

        return true;

    }
}
