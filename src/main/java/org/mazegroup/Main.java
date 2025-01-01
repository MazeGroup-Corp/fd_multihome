package org.mazegroup;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

// import org.bukkit.command.Command;
// import org.bukkit.command.CommandSender;
// import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

// import java.awt.*;

public final class Main extends JavaPlugin {
    public static Main instance;

    public static Main getInstance() {
        return instance;
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static void register_user_if_not(FileConfiguration config, String playerUUID) {
	    if (!config.contains("players." + playerUUID, true)) {

	        ConfigurationSection players_config = config.getConfigurationSection("players");
	        if (players_config == null) {
	            players_config = config.createSection("players");
	        }

	        ConfigurationSection player_config = players_config.createSection(playerUUID);

	        player_config.createSection("homes");

	        getInstance().saveConfig();
	    }
    }

    @Override
    public void onEnable() {
        // Plugin  logic
        getServer().getPluginManager().registerEvents(new homeCommand(), this);
        getLogger().info("Enabled.");

        // Verif if config parameters exist
        FileConfiguration config = getConfig();

        if (!config.contains("config.teleport-time")) {
            config.set("config.teleport-time", 5);
            saveConfig();
            getLogger().info("Added missing config.teleport-time with default value 5.");
        }

        // Get alls commands
        getCommand("sethome").setExecutor(new sethomeCommand());
        getCommand("home").setExecutor(new homeCommand());
        getCommand("delhome").setExecutor(new delhomeCommand());
        getCommand("homelist").setExecutor(new homelistCommand());
        getCommand("homecoords").setExecutor(new homecoordsCommand());
        getCommand("setdefaulthome").setExecutor(new setdefaulthomeCommand());
        getCommand("renamehome").setExecutor(new renamehomeCommand());
        getCommand("admin.homeconfigs.teleport_time").setExecutor(new setTelportTimeCommand());

        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabled.");
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
