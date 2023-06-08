package me.emojiado.emarry.player;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class MarryPlayer {
    private UUID playerId;
    private String playerName;
    private UUID partnerId;
    private String partnerName;
    private Map<UUID, Long> marryRequests;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final File databaseFile = new File("plugins/eMarry/marry_data.json");

    @Getter public static Map<UUID, MarryPlayer> cachedData;

    public MarryPlayer() {
        // Default constructor required for deserialization
    }

    public MarryPlayer(UUID playerId, String playerName, UUID partnerId, String partnerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.marryRequests = new HashMap<>();
    }

    public static void loadData() {
        if (cachedData != null) {
            return;
        }

        if (databaseFile.exists()) {
            try {
                cachedData = objectMapper.readValue(databaseFile, new TypeReference<Map<UUID, MarryPlayer>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            cachedData = new HashMap<>();
            saveData();
        }
    }

    public static void saveData() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(databaseFile, cachedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MarryPlayer getPlayer(UUID playerId) {
        loadData();
        return cachedData.get(playerId);
    }

    public static void addPlayer(MarryPlayer player) {
        loadData();
        cachedData.put(player.getPlayerId(), player);
        saveData();
    }

    public static void removePlayer(UUID playerId) {
        loadData();
        cachedData.remove(playerId);
        saveData();
    }

    public void addMarryRequest(UUID requestPlayerId) {
        marryRequests.put(requestPlayerId, System.currentTimeMillis());
        saveData();
    }

    public void removeMarryRequest(UUID requestPlayerId) {
        marryRequests.remove(requestPlayerId);
        saveData();
    }

    public void setPartner(UUID partnerId, String partnerName) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        saveData();
    }

    public static boolean isPlayerMarried(UUID playerId) {
        MarryPlayer player = getPlayer(playerId);
        return player != null && player.getPartnerId() != null;
    }

    public static UUID getPlayerPartnerId(UUID playerId) {
        MarryPlayer player = getPlayer(playerId);
        return player != null ? player.getPartnerId() : null;
    }

    public static boolean hasMarryRequest(UUID playerId, UUID requestPlayerId) {
        MarryPlayer player = getPlayer(playerId);
        return player != null && player.getMarryRequests().containsKey(requestPlayerId);
    }

    public static Long getMarryRequest(UUID playerId, UUID requestPlayerId) {
        MarryPlayer player = getPlayer(playerId);
        return player != null ? player.getMarryRequests().get(requestPlayerId) : null;
    }

    public static void acceptMarryRequest(UUID playerId, UUID requestPlayerId) {
        MarryPlayer player = getPlayer(playerId);
        if (player != null) {
            MarryPlayer requestPlayer = getPlayer(requestPlayerId);
            if (requestPlayer != null) {
                player.setPartner(requestPlayerId, requestPlayer.getPlayerName());
                requestPlayer.setPartner(playerId, player.getPlayerName());
                player.removeMarryRequest(requestPlayerId);
                requestPlayer.removeMarryRequest(playerId);
            }
        }
    }

    public static void denyMarryRequest(UUID playerId, UUID requestPlayerId) {
        MarryPlayer player = getPlayer(playerId);
        if (player != null) {
            player.removeMarryRequest(requestPlayerId);
        }
    }

    public static void marryDivorce(String player1Name, String player2Name) {
        MarryPlayer player1 = null;
        MarryPlayer player2 = null;

        for (MarryPlayer marryPlayer1 : cachedData.values()) {
            if (marryPlayer1.getPlayerName().equalsIgnoreCase(player1Name)) {
                player1 = marryPlayer1;
            } else if (marryPlayer1.getPlayerName().equalsIgnoreCase(player2Name)) {
                player2 = marryPlayer1;
            }

            if (player1 != null && player2 != null) {
                break;
            }
        }

        if (player1 != null && player2 != null) {
            cachedData.remove(player1.getPlayerId());
            cachedData.remove(player2.getPlayerId());
            saveData(); // Save data after modifying it
        }
    }

}
