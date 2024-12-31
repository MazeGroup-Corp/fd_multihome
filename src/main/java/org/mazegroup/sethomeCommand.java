package org.mazegroup;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        FileConfiguration config = Main.getInstance().getConfig();
        String playerUUID = player.getUniqueId().toString();
        String basePath = "players." + playerUUID + ".homes";
        
        Main.register_user_if_not(config, playerUUID);

        String homeName;
        boolean isDefaultHome;
        
        ConfigurationSection homesSection = Main.getInstance().getConfig().getConfigurationSection(basePath);
        Set<String> homes = homesSection.getKeys(false);
        
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
            for (String otherHome : config.getConfigurationSection(basePath).getKeys(false)) {
                config.set(basePath + "." + otherHome + ".default", false);
            }
            config.set(basePath + "." + homeName + ".default", true);
            player.sendMessage("§aYour home '" + homeName + "' was created and set as default home.");
        } else {
            config.set(basePath + "." + homeName + ".default", false);
            player.sendMessage("§aYour home '" + homeName + "' was created.");
        }

        Main.getInstance().saveConfig();
        return true;
    }
}
