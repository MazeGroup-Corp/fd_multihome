package org.mazegroup;

import java.io.File;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

class delhomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command must be executed by a player.");
            return false;
        }

        Player player = (Player) sender;
        FileConfiguration homes = fileConfig("homes.yml");
        String playerUUID = player.getUniqueId().toString();
        String basePath = "players." + playerUUID + ".homes";

        Main.register_user_if_not(homes, playerUUID);

        if (args.length == 0) {
            String defaultHome = null;

            for (String home : homes.getConfigurationSection(basePath).getKeys(false)) {
                if (homes.getBoolean(basePath + "." + home + ".default", false)) {
                    defaultHome = home;
                    break;
                }
            }

            if (defaultHome != null) {
                homes.set(basePath + "." + defaultHome, null);
                saveFileConfig(homes, "homes.yml");
                player.sendMessage("§aYour default home '" + defaultHome + "' has been deleted.");
            } else {
                player.sendMessage("§cYou don't have a default home.");
            }
        } else {
            String homeName = args[0];

            if (homes.contains(basePath + "." + homeName)) {
                boolean wasDefault = homes.getBoolean(basePath + "." + homeName + ".default", false);

                homes.set(basePath + "." + homeName, null);
                saveFileConfig(homes, "homes.yml");
                player.sendMessage("§aThe home '" + homeName + "' has been deleted.");

                if (wasDefault) {
                    Set<String> remainingHomes = homes.getConfigurationSection(basePath).getKeys(false);
                    if (!remainingHomes.isEmpty()) {
                        String newDefaultHome = remainingHomes.iterator().next();
                        homes.set(basePath + "." + newDefaultHome + ".default", true);
                        saveFileConfig(homes, "homes.yml");
                        player.sendMessage("§aThe home '" + newDefaultHome + "' has been defined as your default home.");
                    }
                }
            } else {
                player.sendMessage("§cYou don't have a home named '" + homeName + "'.");
            }
        }

        return true;
    }

    private FileConfiguration fileConfig(String fileName) {
        File file = new File(Main.getInstance().getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
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
