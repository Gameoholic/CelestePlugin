package com.github.gameoholic.celesteparkour.celesteparkour.commands;

import com.github.gameoholic.celesteparkour.celesteparkour.CelesteParkour;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ParkourRegion implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {

            if (!player.hasPermission("celesteparkour.selectregion")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            }

            UUID uuid = player.getUniqueId();
            HashMap<UUID, Integer> parkourRegions = CelesteParkour.getPlugin().parkourRegions;

            if (args.length == 0) {
                if (!parkourRegions.containsKey(uuid))
                    player.sendMessage(ChatColor.GREEN + "You do not have a parkour region selected.");
                else
                    player.sendMessage(ChatColor.GREEN + "Your selected parkour editing region is " + ChatColor.YELLOW + parkourRegions.get(uuid) + ".");
                return true;
            }

            int region = 0;
            try {
                region = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid region provided. Must be between 1-5.");
                return true;
            }

            if (region < 0 || region > 5) {
                player.sendMessage(ChatColor.RED + "Invalid region provided. Must be between 0 and 5.");
                return true;
            }
            if (region == 0) {
                if (parkourRegions.containsKey(uuid))
                    parkourRegions.remove(uuid);
                player.sendMessage(ChatColor.GREEN + "You've deselected your parkour region.");
                return true;
            }
            else {
                parkourRegions.put(uuid, region);
                player.sendMessage(ChatColor.GREEN + "You've selected parkour region " + ChatColor.YELLOW + region + ".");
                return true;
            }

        }
        else {
            System.out.println("bruh get yo ass on the server");
        }
        return true;
    }
}
