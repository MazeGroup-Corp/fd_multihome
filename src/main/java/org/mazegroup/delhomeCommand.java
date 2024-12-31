package org.mazegroup;

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
        FileConfiguration config = Main.getInstance().getConfig();
        String playerUUID = player.getUniqueId().toString();
        String basePath = "players." + playerUUID + ".homes";
        
        Main.register_user_if_not(config, playerUUID);

        if (args.length == 0) {
            String defaultHome = null;

            for (String home : config.getConfigurationSection(basePath).getKeys(false)) {
                if (config.getBoolean(basePath + "." + home + ".default", false)) {
                    defaultHome = home;
                    break;
                }
            }

            if (defaultHome != null) {
                config.set(basePath + "." + defaultHome, null);
                Main.getInstance().saveConfig();
                player.sendMessage("§aYour default home '" + defaultHome + "' has been deleted.");
            } else {
                player.sendMessage("§cYou don't have a default home");
            }
        } else {
            String homeName = args[0];

            if (config.contains(basePath + "." + homeName)) {
                boolean wasDefault = config.getBoolean(basePath + "." + homeName + ".default", false);

                config.set(basePath + "." + homeName, null);
                Main.getInstance().saveConfig();
                player.sendMessage("§aThe home '" + homeName + "' has been deleted.");

                if (wasDefault) {
                    Set<String> remainingHomes = config.getConfigurationSection(basePath).getKeys(false);
                    if (!remainingHomes.isEmpty()) {
                        String newDefaultHome = remainingHomes.iterator().next();
                        config.set(basePath + "." + newDefaultHome + ".default", true);
                        player.sendMessage("§aThe home '" + newDefaultHome + "' has been defined like your default home");
                    }
                }
            } else {
                player.sendMessage("§cYou don't have a home nammed '" + homeName + "'.");
            }
        }

        return true;
    }
}
