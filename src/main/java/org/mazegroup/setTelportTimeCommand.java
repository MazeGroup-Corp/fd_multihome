package org.mazegroup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class setTelportTimeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command must be executed by a player.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("§cPlease specify a time in seconds.");
            return false;
        }

        try {
            double time = Double.parseDouble(args[0]);

            if (time < 0) {
                sender.sendMessage("§cTime must be greater than <0.");
                return false;
            }

            Main.getInstance().getConfig().set("config.teleport-time", time);
            Main.getInstance().saveConfig();

            sender.sendMessage("§aTeleportation time has been set to " + time + " seconds.");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid time format. Please specify a valid number.");
        }

        return true;
    }
}
