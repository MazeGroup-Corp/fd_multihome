package org.mazegroup;

import java.io.File;
import java.util.Set;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class sethomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command must be executed by a player.");
            return false;
        }

        Player player = (Player) sender;
        FileConfiguration config = fileConfig("homes.yml");
        String playerUUID = player.getUniqueId().toString();
        String basePath = "players." + playerUUID + ".homes";

        Main.register_user_if_not(config, playerUUID);

        String homeName;
        boolean isDefaultHome;

        ConfigurationSection homesSection = config.getConfigurationSection(basePath);
        Set<String> homes = homesSection != null ? homesSection.getKeys(false) : Set.of();

        int homes_count = homes.size();
        isDefaultHome = (homes_count == 0);

        if (args.length == 0) {
            homeName = "home0";
        } else {
            homeName = args[0];
        }

        config.set(basePath + "." + homeName + ".world", player.getLocation().getWorld().getName());
        config.set(basePath + "." + homeName + ".x", player.getLocation().getX());
        config.set(basePath + "." + homeName + ".y", player.getLocation().getY());
        config.set(basePath + "." + homeName + ".z", player.getLocation().getZ());
        config.set(basePath + "." + homeName + ".pitch", player.getEyeLocation().getPitch());
        config.set(basePath + "." + homeName + ".yaw", player.getEyeLocation().getYaw());

        if (isDefaultHome) {
            if (homesSection != null) {
                for (String otherHome : homesSection.getKeys(false)) {
                    config.set(basePath + "." + otherHome + ".default", false);
                }
            }
            config.set(basePath + "." + homeName + ".default", true);
            player.sendMessage("§aYour home '" + homeName + "' was created and set as default home.");
        } else {
            config.set(basePath + "." + homeName + ".default", false);
            player.sendMessage("§aYour home '" + homeName + "' was created.");
        }

        saveFileConfig(config, "homes.yml");
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
