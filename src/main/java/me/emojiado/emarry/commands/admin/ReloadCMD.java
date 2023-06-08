package me.emojiado.emarry.commands.admin;

import me.emojiado.emarry.EMarry;
import me.emojiado.emarry.configuration.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCMD implements CommandExecutor {
    private EMarry plugin;
    private ConfigUtil configUtil;
    private String permission;

    public ReloadCMD(EMarry plugin, ConfigUtil configUtil, String permission) {
        this.plugin = plugin;
        this.configUtil = configUtil;
        this.permission = permission;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(permission)) {
            configUtil.sendMessage(sender, "no_permissions");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            configUtil.reloadConfigFiles();
            configUtil.sendMessage(sender, "reload_success");
            return true;
        }

        return false;
    }
}

