package me.emojiado.emarry;

import lombok.Getter;
import me.emojiado.emarry.commands.admin.ReloadCMD;
import me.emojiado.emarry.configuration.ConfigUtil;
import me.emojiado.emarry.database.Database;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public final class EMarry extends JavaPlugin {

    @Getter public static EMarry instance;
    private ConfigUtil configUtil;

    @Override
    public void onEnable() {
        instance = this;

        // Configuration
        configUtil = new ConfigUtil(this);
        configUtil.reloadConfigFiles();
        Database.setConfigUtil(configUtil);

        // Database
        try {
            Class.forName ("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            Connection connection = Database.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Register the command
        CommandExecutor reloadCommand = new ReloadCMD(this, configUtil, "emarry.reload");
        getCommand("emarry").setExecutor(reloadCommand);
    }

    @Override
    public void onDisable() {
        instance = null;

        // Save the configuration files on plugin disable
        configUtil.saveConfigFiles();
    }
}
