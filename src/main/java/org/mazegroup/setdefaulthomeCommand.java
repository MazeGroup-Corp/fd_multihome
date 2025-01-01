package org.mazegroup;

import java.io.File;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class setdefaulthomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command must be executed by a player.");
            return false;
        }

        Player player = (Player) sender;
        FileConfiguration homesFile = fileConfig("homes.yml");
        String playerUUID = player.getUniqueId().toString();
        String basePath = "players." + playerUUID + ".homes";

        Main.register_user_if_not(homesFile, playerUUID);

        if (args.length == 0) {
            player.sendMessage("§cYou must enter the name of the home you want to set as default after the command.");
            return false;
        } else {
            String homeName = args[0];
            ConfigurationSection homesSection = homesFile.getConfigurationSection(basePath);

            if (homesSection == null || !homesSection.contains(homeName)) {
                player.sendMessage("§cYou don't have a home named '" + homeName + "'.");
                return false;
            }

            String oldDefaultHome = null;

            Set<String> homeKeys = homesSection.getKeys(false);
            for (String key : homeKeys) {
                if (homesSection.getBoolean(key + ".default")) {
                    oldDefaultHome = key;
                }
                homesSection.set(key + ".default", false);
            }

            homesSection.set(homeName + ".default", true);

            player.sendMessage("§aThe default home is now '" + homeName + "'.");
            if (oldDefaultHome != null) {
                player.sendMessage("§eThe old one was '" + oldDefaultHome + "'.");
            } else {
                player.sendMessage("§eThere was no default home previously.");
            }
        }

        Main.getInstance().saveConfig();
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

    private void saveFileConfig(FileConfiguration config, String fileName) {
        File file = new File(Main.getInstance().getDataFolder(), fileName);
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
