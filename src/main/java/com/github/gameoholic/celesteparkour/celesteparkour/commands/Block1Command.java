package com.github.gameoholic.celesteparkour.celesteparkour.commands;

import com.github.gameoholic.celesteparkour.celesteparkour.CelesteParkour;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.StringBufferInputStream;
import java.util.List;

public class Block1Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {

            if (!player.hasPermission("celesteparkour.setblocks")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            }

            //block1 is a list containing all blocks that are regarded as block1
            List<String> block1Blocks = CelesteParkour.getPlugin().getConfig().getStringList("Block1");
            if (args.length == 0) {

                player.sendMessage("Block 1 is set to " + ChatColor.YELLOW + block1Blocks + ".");
            }
            else {
                block1Blocks.clear();
                for (int i = 0; i < args.length; i++) {
                    Material material = Material.getMaterial(args[i].toString());
                    if (material == null) {
                        player.sendMessage(ChatColor.RED + "Block type " + args[i].toString() + " doesn't exist. See https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html for full list of block types.");
                        return true;
                    }
                    else
                        block1Blocks.add(material.toString());
                }
                CelesteParkour.getPlugin().getConfig().set("Block1", block1Blocks);
                CelesteParkour.getPlugin().saveConfig();

                player.sendMessage(ChatColor.GREEN + "Set block 1 to " + ChatColor.YELLOW + block1Blocks + ".");
            }
        }
        else {
            System.out.println("bruh get yo ass on the server");
        }
        return true;
    }

}
