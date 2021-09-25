package online.nasgar.skyblockcore.api.manager;

import online.nasgar.commons.profile.PlayerProfile;
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import online.nasgar.skyblockcore.api.model.island.Island;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SkyblockPlayerManager extends IslandManager<UUID> {

    SkyblockPlayer loadPlayer(Player player);

    CompletableFuture<SkyblockPlayer> loadPlayerAsync(Player player);

    boolean isCached(Player player);

    SkyblockPlayer get(Player player);

    Optional<SkyblockPlayer> getPlayer(Player player);

    default void loadIsland(SkyblockPlayer skyblockPlayer) {
        Objects.requireNonNull(skyblockPlayer);

        PlayerProfile playerProfile = skyblockPlayer.profiles().getProfileInUse();

        loadIsland(playerProfile.getPlayerId());
    }

    default Island getPlayerIsland(SkyblockPlayer skyblockPlayer) {
        return getIsland(skyblockPlayer.profiles().getProfileInUse().getProfileId());
    }

    void savePlayers();

}