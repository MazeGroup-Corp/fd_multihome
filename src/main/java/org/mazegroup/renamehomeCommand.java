package org.mazegroup;

// import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class renamehomeCommand implements CommandExecutor {
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

        if (args.length < 2) {
            player.sendMessage("§cYou must specify the current home name and the new name.");
            return false;
        } else {
            String currentHomeName = args[0];
            String newHomeName = args[1];
            ConfigurationSection homesSection = config.getConfigurationSection(basePath);

            if (homesSection == null || !homesSection.contains(currentHomeName)) {
                player.sendMessage("§cThe home '" + currentHomeName + "' does not exist.");
                return false;
            }

            if (homesSection.contains(newHomeName)) {
                player.sendMessage("§cThe home '" + newHomeName + "' already exist.");
                return false;
            }

            // Renommer le home en copiant les données et en supprimant l'ancien
            ConfigurationSection currentHomeSection = homesSection.getConfigurationSection(currentHomeName);
            ConfigurationSection newHomeSection = homesSection.createSection(newHomeName);

            for (String key : currentHomeSection.getKeys(false)) {
                newHomeSection.set(key, currentHomeSection.get(key));
            }

            homesSection.set(currentHomeName, null);  // Supprime l'ancien home

            // Informer le joueur
            player.sendMessage("§aThe home '" + currentHomeName + "' was renamed to '" + newHomeName + "'.");

            Main.getInstance().saveConfig();
            return true;
        }
    }
}
