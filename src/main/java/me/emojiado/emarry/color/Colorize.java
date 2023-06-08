package me.emojiado.emarry.color;

import org.bukkit.ChatColor;

public class Colorize {

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
