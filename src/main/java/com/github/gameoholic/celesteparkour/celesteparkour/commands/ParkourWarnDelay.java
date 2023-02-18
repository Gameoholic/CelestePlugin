package com.github.gameoholic.celesteparkour.celesteparkour.commands;

import com.github.gameoholic.celesteparkour.celesteparkour.CelesteParkour;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ParkourWarnDelay implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("celesteparkour.warninterval")) {
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
                player.sendMessage(ChatColor.RED + "Current warn delay of region " + region + " is " + ChatColor.YELLOW + warnInterval + " ticks (" + warnInterval/20.0 + " seconds).");
                return true;
            }


            int ticks = 0;
            try {
                ticks = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid warn delay provided. Must be whole number.");
                return true;
            }

            if (ticks == -1) {
                player.sendMessage(ChatColor.GREEN + "Disabled block switch warning for region " + region + ".");
                CelesteParkour.getPlugin().getConfig().set("SwitchWarnDelayR" + region, -1);
                return true;
            }
            if (ticks <= 0 || ticks >= interval) {
                player.sendMessage(ChatColor.RED + "Invalid warn delay provided.");
                return true;
            }

            player.sendMessage(ChatColor.GREEN + "Changed parkour region " + region + " warn delay to " + ChatColor.YELLOW + ticks + " ticks (" + ticks/20.0 + " seconds).");
            CelesteParkour.getPlugin().getConfig().set("SwitchWarnDelayR" + region, ticks);
            CelesteParkour.getPlugin().saveConfig();

        }
        else {
            System.out.println("bruh get yo ass on the server");
        }

        return true;

    }
}
