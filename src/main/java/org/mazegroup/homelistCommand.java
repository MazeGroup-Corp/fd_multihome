package org.mazegroup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Set;

public class homelistCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command must be executed by a player.");
            return false;
        }

        Player player = (Player) sender;
        FileConfiguration homesFile = fileConfig("homes.yml");
        String playerUUID = player.getUniqueId().toString();
        String path = "players." + playerUUID + ".homes";
        
        Main.register_user_if_not(homesFile, playerUUID);

        if (homesFile.contains(path)) {
            ConfigurationSection homesSection = homesFile.getConfigurationSection(path);
            Set<String> homes = homesSection.getKeys(false);

            if (homes.isEmpty()) {
                player.sendMessage("§cYou don't have a home.");
            } else {
                player.sendMessage("");
                player.sendMessage("§eHomes list (" + homes.size() + ") :");
                for (String home : homes) {
                    boolean isDefault = homesSection.getBoolean(home + ".default", false);
                    String defaultMarker = isDefault ? "§b[default] " : "§e";
                    player.sendMessage("§e • " + defaultMarker + home);
                }
            }
        } else {
            player.sendMessage("§cYou don't have a home.");
        }

        return true;
    }

    public FileConfiguration fileConfig(String fileName) {
        File file = new File(Main.getInstance().getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
