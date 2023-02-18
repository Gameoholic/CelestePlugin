package com.github.gameoholic.celesteparkour.celesteparkour.commands;

import com.github.gameoholic.celesteparkour.celesteparkour.CelesteParkour;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Block2Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {

            if (!player.hasPermission("celesteparkour.setblocks")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            }

            //block2 is a list containing all blocks that are regarded as block2
            List<String> block2Blocks = CelesteParkour.getPlugin().getConfig().getStringList("Block2");
            if (args.length == 0) {

                player.sendMessage("Block 2 is set to " + ChatColor.YELLOW + block2Blocks + ".");
            }
            else {
                block2Blocks.clear();
                for (int i = 0; i < args.length; i++) {
                    Material material = Material.getMaterial(args[i].toString());
                    if (material == null) {
                        player.sendMessage(ChatColor.RED + "Block type " + args[i].toString() + " doesn't exist. See https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html for full list of block types.");
                        return true;
                    }
                    else
                        block2Blocks.add(material.toString());
                }
                CelesteParkour.getPlugin().getConfig().set("Block2", block2Blocks);
                CelesteParkour.getPlugin().saveConfig();

                player.sendMessage(ChatColor.GREEN + "Set block 2 to " + ChatColor.YELLOW + block2Blocks + ".");
            }
        }
        else {
            System.out.println("bruh get yo ass on the server");
        }
        return true;
    }
}
