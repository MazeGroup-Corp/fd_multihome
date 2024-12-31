package org.mazegroup;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;

public class homeCommand implements CommandExecutor, Listener {

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

        double teleportTime = config.getDouble("config.teleport-time", 5.0); // Default to 5 seconds if not set

        String homeName = args.length == 0 ? getDefaultHome(config, basePath) : args[0];
        if (homeName == null || !config.contains(basePath + "." + homeName)) {
            player.sendMessage("§cYou don't have a home '" + (homeName == null ? "default" : homeName) + "'.");
            return true;
        }

        Location targetLocation = getHomeLocation(config, basePath, homeName);
        if (targetLocation == null) {
            player.sendMessage("§cError: Invalid home location.");
            return true;
        }

        player.sendMessage("§eTeleporting to your home '" + homeName + "' in " + teleportTime + " seconds. Don't move!");

        Location initialLocation = player.getLocation(); // Position initiale au moment de la commande
        new BukkitRunnable() {
            @Override
            public void run() {
                if (hasPlayerMoved(initialLocation, player.getLocation())) {
                    player.sendMessage("§cTeleportation canceled because you moved.");
                    return;
                }
                player.teleport(targetLocation);
                player.sendMessage("§aYou have been teleported to your home '" + homeName + "'.");
            }
        }.runTaskLater(Main.getInstance(), (long) (teleportTime * 20)); // Delayed by teleportTime in seconds

        return true;
    }

    private String getDefaultHome(FileConfiguration config, String basePath) {
        for (String home : config.getConfigurationSection(basePath).getKeys(false)) {
            if (config.getBoolean(basePath + "." + home + ".default", false)) {
                return home;
            }
        }
        return null;
    }

    private Location getHomeLocation(FileConfiguration config, String basePath, String homeName) {
        World world = Bukkit.getServer().getWorld(config.getString(basePath + "." + homeName + ".world"));
        if (world == null) return null;
        double x = config.getDouble(basePath + "." + homeName + ".x");
        double y = config.getDouble(basePath + "." + homeName + ".y");
        double z = config.getDouble(basePath + "." + homeName + ".z");
        float pitch = (float) config.getDouble(basePath + "." + homeName + ".pitch");
        float yaw = (float) config.getDouble(basePath + "." + homeName + ".yaw");
        return new Location(world, x, y, z, yaw, pitch);
    }

    private boolean hasPlayerMoved(Location from, Location to) {
        double deltaX = Math.abs(from.getX() - to.getX());
        double deltaY = Math.abs(from.getY() - to.getY());
        double deltaZ = Math.abs(from.getZ() - to.getZ());

        // Seuil de détection de mouvement significatif
        return deltaX > 0.1 || deltaY > 0.1 || deltaZ > 0.1;
    }
}
