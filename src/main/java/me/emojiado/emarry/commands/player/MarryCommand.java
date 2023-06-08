package me.emojiado.emarry.commands.player;

import me.emojiado.emarry.EMarry;
import me.emojiado.emarry.color.Colorize;
import me.emojiado.emarry.configuration.ConfigUtil;
import me.emojiado.emarry.player.MarryPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MarryCommand implements CommandExecutor {

    private EMarry plugin;
    private ConfigUtil configUtil;

    public MarryCommand(EMarry plugin, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.configUtil = configUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sólo los jugadores pueden utilizar este comando.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            List<String> helpMessages = configUtil.getMessageList("help-marry");
            if (helpMessages != null) {
                String formattedMessage = configUtil.formatMessageList(helpMessages);
                player.sendMessage(formattedMessage);
            } else {
                player.sendMessage(ChatColor.RED + "Help messages not found in the configuration.");
            }
            return true;
        }


        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length < 2) {
                configUtil.sendMessage(player, "marry-accept-usage");
                return true;
            }

            String requestPlayerName = args[1];
            Player requestPlayer = player.getServer().getPlayer(requestPlayerName);

            if (requestPlayer == null) {
                configUtil.sendMessage(player, "marry-no-online");
                return true;
            }

            UUID requestPlayerId = requestPlayer.getUniqueId();

            if (!MarryPlayer.hasMarryRequest(player.getUniqueId(), requestPlayerId)) {
                configUtil.sendMessage(player, "marry-no-requests");
                return true;
            }

            MarryPlayer.acceptMarryRequest(player.getUniqueId(), requestPlayerId);

            player.sendMessage(Colorize.translate("&aHas aceptado la petición de matrimonio de &l" + requestPlayer.getName() + "&a."));
            requestPlayer.sendMessage(Colorize.translate("&a&l" + player.getName() + " &aha aceptado tu petición de matrimonio."));

            return true;
        }

        if (args[0].equalsIgnoreCase("deny")) {
            if (args.length < 2) {
                configUtil.sendMessage(player, "marry-deny-usage");
                return true;
            }

            String requestPlayerName = args[1];
            Player requestPlayer = player.getServer().getPlayer(requestPlayerName);

            if (requestPlayer == null) {
                configUtil.sendMessage(player, "marry-no-online");
                return true;
            }

            UUID requestPlayerId = requestPlayer.getUniqueId();

            if (!MarryPlayer.hasMarryRequest(player.getUniqueId(), requestPlayerId)) {
                configUtil.sendMessage(player, "marry-no-requests");
                return true;
            }

            MarryPlayer.denyMarryRequest(player.getUniqueId(), requestPlayerId);

            player.sendMessage(Colorize.translate("&aHas denegado la solicitud de matrimonio de &a&l" + requestPlayer.getName()));
            requestPlayer.sendMessage(Colorize.translate("&c&l" + player.getName() + "&c ha denegado tu solicitud de matrimonio."));

            return true;
        }

        if (args[0].equalsIgnoreCase("divorce")) {
            if (args.length < 2) {
                configUtil.sendMessage(player, "marry-divorce-usage");
                return true;
            }

            String partnerName = args[1];

            MarryPlayer.marryDivorce(player.getName(), partnerName);

            player.sendMessage(Colorize.translate("&cAhora estás divorciado de &l" + partnerName));

            return true;
        }

        // Assuming the argument is the player to propose to
        String proposePlayerName = args[0];
        Player proposePlayer = player.getServer().getPlayer(proposePlayerName);

        if (proposePlayer == null) {
            configUtil.sendMessage(player, "marry-no-online");
            return true;
        }

        UUID proposePlayerId = proposePlayer.getUniqueId();

        if (proposePlayerId.equals(player.getUniqueId())) {
            configUtil.sendMessage(player, "marry-no-yourself");
            return true;
        }

        if (MarryPlayer.hasMarryRequest(proposePlayerId, player.getUniqueId())) {
            configUtil.sendMessage(player, "marry-already-sent");
            return true;
        }

        if (MarryPlayer.isPlayerMarried(proposePlayerId)) {
            configUtil.sendMessage(player, "marry-already-target");
            return true;
        }

        if (MarryPlayer.isPlayerMarried(player.getUniqueId())) {
            configUtil.sendMessage(player, "marry-already");
            return true;
        }

        MarryPlayer.loadData(); // Load data before modifying it

        MarryPlayer proposeMarryPlayer = MarryPlayer.getPlayer(proposePlayerId);
        if (proposeMarryPlayer == null) {
            proposeMarryPlayer = new MarryPlayer(proposePlayerId, proposePlayer.getName(), null, null);
        }
        proposeMarryPlayer.addMarryRequest(player.getUniqueId());
        MarryPlayer.addPlayer(proposeMarryPlayer);

        MarryPlayer.saveData(); // Save data after modifying it

        player.sendMessage(Colorize.translate("&aHas enviado una solicitud de matrimonio a &l" + proposePlayer.getName()));
        proposePlayer.sendMessage(Colorize.translate("&a&l" + player.getName() + "&a te ha enviado una solicitud de matrimonio."));

        return true;
    }
}
