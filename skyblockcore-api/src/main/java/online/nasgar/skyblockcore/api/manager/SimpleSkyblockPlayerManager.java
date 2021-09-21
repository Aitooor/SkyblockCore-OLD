package online.nasgar.skyblockcore.api.manager;

import online.nasgar.commons.profile.GlobalProfileManager;
import online.nasgar.commons.profile.PlayerProfileManager;
import online.nasgar.commons.registry.RepositoryObjectRegistry;
import online.nasgar.skyblockcore.api.event.PlayerDataLoadEvent;
import online.nasgar.skyblockcore.api.loader.IslandLoader;
import online.nasgar.skyblockcore.api.loader.Loader;
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import online.nasgar.skyblockcore.api.model.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class SimpleSkyblockPlayerManager implements SkyblockPlayerManager {

    private final IslandLoader islandLoader;
    private final Map<UUID, Island> cachedIslands;
    private final GlobalProfileManager profileManager;
    private final Map<UUID, SkyblockPlayer> cachedPlayers;

    public SimpleSkyblockPlayerManager(
            IslandLoader islandLoader,
            RepositoryObjectRegistry<UUID, PlayerProfileManager> profileRepositoryRegistry
    ) {
        this.islandLoader = Objects.requireNonNull(islandLoader, "islandLoader");
        this.profileManager = new GlobalProfileManager(Objects.requireNonNull(profileRepositoryRegistry, "profileRepositoryRegistry"));
        this.cachedIslands = new HashMap<>();
        this.cachedPlayers = new HashMap<>();
    }

    @Override
    public Island loadIsland(SkyblockPlayer key) {
        Island found = getIsland(key);

        if (found == null) {
            found = islandLoader.load(key);
            this.cachedIslands.put(key.profiles().getProfileInUse().getProfileId(), found);
        }

        return found;
    }

    @Override
    public Island getIsland(SkyblockPlayer key) {
        UUID uuid = key.profiles().getProfileInUse().getProfileId();

        return cachedIslands.get(uuid);
    }

    @Override
    public Loader<Island, SkyblockPlayer> getIslandLoader() {
        return islandLoader;
    }

    @Override
    public SkyblockPlayer loadPlayer(Player player) {
        SkyblockPlayer found = get(player);

        if (found == null) {
            found = loadPlayerData(player);
            this.cachedPlayers.put(player.getUniqueId(), found);
        }

        return found;
    }

    @Override
    public boolean isCached(Player player) {
        return this.cachedPlayers.containsKey(player.getUniqueId());
    }

    @Override
    public SkyblockPlayer get(Player player) {
        return this.cachedPlayers.get(player.getUniqueId());
    }

    @Override
    public Optional<SkyblockPlayer> getPlayer(Player player) {
        return Optional.ofNullable(get(player));
    }

    @Override
    public GlobalProfileManager profileManager() {
        return profileManager;
    }

    private SkyblockPlayer loadPlayerData(Player player) {
        SkyblockPlayer skyblockPlayer = new SkyblockPlayer(profileManager.loadIfAbsent(player));

        loadIsland(skyblockPlayer);

        Bukkit.getPluginManager().callEvent(new PlayerDataLoadEvent(skyblockPlayer));

        return skyblockPlayer;
    }
}