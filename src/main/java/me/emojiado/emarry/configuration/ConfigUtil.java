package me.emojiado.emarry.configuration;

import lombok.Getter;
import me.emojiado.emarry.EMarry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ConfigUtil {
    private EMarry plugin;
    private FileConfiguration configFile;
    private FileConfiguration messagesFile;

    public ConfigUtil(EMarry plugin) {
        this.plugin = plugin;
        createConfigFiles();
        reloadConfigFiles();
    }

    private void createConfigFiles() {
        File configYaml = new File(plugin.getDataFolder(), "config.yml");
        if (!configYaml.exists()) {
            plugin.saveResource("config.yml", false);
        }

        File messagesYaml = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesYaml.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    public void reloadConfigFiles() {
        configFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        messagesFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    public void saveConfigFiles() {
        try {
            configFile.save(new File(plugin.getDataFolder(), "config.yml"));
            messagesFile.save(new File(plugin.getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFormattedString(FileConfiguration fileConfiguration, String path) {
        String value = fileConfiguration.getString(path);
        if (value != null) {
            return ChatColor.translateAlternateColorCodes('&', value);
        }
        return null;
    }

    public String getConfigString(String path) {
        return getFormattedString(configFile, path);
    }

    public int getConfigInt(String path) {
        return configFile.getInt(path);
    }

    public double getConfigDouble(String path) {
        return configFile.getDouble(path);
    }

    public String getMessageString(String path) {
        return getFormattedString(messagesFile, path);
    }

    public int getMessageInt(String path) {
        return messagesFile.getInt(path);
    }

    public double getMessageDouble(String path) {
        return messagesFile.getDouble(path);
    }

    public void sendMessage(CommandSender sender, String path) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String message = getMessageString(path);
            if (message != null) {
                player.sendMessage(message);
            } else {
                player.sendMessage(ChatColor.RED + "Message not found in the configuration.");
            }
        } else {
            plugin.getLogger().warning("Attempted to send a message to a non-player sender.");
        }
    }

    public List<String> getMessageList(String path) {
        List<String> messages = messagesFile.getStringList(path);
        if (messages != null && !messages.isEmpty()) {
            List<String> formattedMessages = new ArrayList<>();
            for (String message : messages) {
                formattedMessages.add(ChatColor.translateAlternateColorCodes('&', message));
            }
            return formattedMessages;
        }
        return null;
    }

    public String formatMessageList(List<String> messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append("\n");
        }
        return sb.toString().trim();
    }
}

