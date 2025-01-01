package org.mazegroup;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class homecoordsCommand implements CommandExecutor {
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

        // Default home coordinates
        if (args.length == 0) {
            String defaultHome = null;

            for (String home : homesFile.getConfigurationSection(basePath).getKeys(false)) {
                if (homesFile.getBoolean(basePath + "." + home + ".default", false)) {
                    defaultHome = home;
                    break;
                }
            }

            if (defaultHome != null) {
                World world = Bukkit.getServer().getWorld(homesFile.getString(basePath + "." + defaultHome + ".world"));
                double x = homesFile.getDouble(basePath + "." + defaultHome + ".x");
                double y = homesFile.getDouble(basePath + "." + defaultHome + ".y");
                double z = homesFile.getDouble(basePath + "." + defaultHome + ".z");

                String worldName = getWorldName(world);

                player.sendMessage("§eDefault home cordinnates '" + defaultHome + "' : " + worldName + " " + Main.round(x, 2) + " " + Main.round(y, 2) + " " + Main.round(z, 0));
            } else {
                player.sendMessage("§cYou don't have a default home.");
            }
        } 
        // Specific home coordinates
        else {
            String homeName = args[0];

            if (homesFile.contains(basePath + "." + homeName)) {
                World world = Bukkit.getServer().getWorld(homesFile.getString(basePath + "." + homeName + ".world"));
                double x = homesFile.getDouble(basePath + "." + homeName + ".x");
                double y = homesFile.getDouble(basePath + "." + homeName + ".y");
                double z = homesFile.getDouble(basePath + "." + homeName + ".z");

                String worldName = getWorldName(world);

                player.sendMessage("§eHome cordinnates '" + homeName + "' : " + worldName + " " + Main.round(x, 2) + " " + Main.round(y, 2) + " " + Main.round(z, 0));
            } else {
                player.sendMessage("§cYou don't have a home named '" + homeName + "'.");
            }
        }

        return true;
    }

    private String getWorldName(World world) {
        if (world == null) {
            return "Unknow world";
        }
        
        Environment env = world.getEnvironment();
        switch (env) {
            case NORMAL:
                return "Overworld";
            case NETHER:
                return "Nether";
            case THE_END:
                return "The End";
            default:
                return "Personalized environnement";
        }
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
