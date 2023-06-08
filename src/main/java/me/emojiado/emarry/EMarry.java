package me.emojiado.emarry;

import lombok.Getter;
import me.emojiado.emarry.commands.admin.ReloadCMD;
import me.emojiado.emarry.commands.player.MarryCommand;
import me.emojiado.emarry.configuration.ConfigUtil;
import me.emojiado.emarry.player.MarryPlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class EMarry extends JavaPlugin {

    @Getter public static EMarry instance;
    private ConfigUtil configUtil;

    @Override
    public void onEnable() {
        instance = this;
        MarryPlayer.loadData();

        // Configuration
        configUtil = new ConfigUtil(this);
        configUtil.reloadConfigFiles();

        // Register the command
        CommandExecutor reloadCommand = new ReloadCMD(this, configUtil, "emarry.reload");
        CommandExecutor marryCommand = new MarryCommand(this, configUtil);

        getCommand("emarry").setExecutor(reloadCommand);
        getCommand("marry").setExecutor(marryCommand);
    }

    @Override
    public void onDisable() {
        instance = null;
        MarryPlayer.saveData();
        configUtil.saveConfigFiles();
    }
}
