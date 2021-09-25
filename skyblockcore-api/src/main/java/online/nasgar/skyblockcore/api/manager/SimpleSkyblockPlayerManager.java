package online.nasgar.skyblockcore.api.manager;

import com.github.imthenico.repositoryhelper.core.repository.Repository;
import online.nasgar.commons.profile.PlayerProfile;
import online.nasgar.commons.profile.PlayerProfileManager;
import online.nasgar.skyblockcore.api.event.PlayerDataLoadEvent;
import online.nasgar.skyblockcore.api.loader.IslandLoader;
import online.nasgar.skyblockcore.api.loader.Loader;
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import online.nasgar.skyblockcore.api.model.SkyblockPlayerData;
import online.nasgar.skyblockcore.api.model.island.Island;
import online.nasgar.skyblockcore.api.model.island.IslandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SimpleSkyblockPlayerManager implements SkyblockPlayerManager {

    private final IslandLoader islandLoader;
    private final Map<UUID, Island> cachedIslands;
    private final Map<UUID, SkyblockPlayer> cachedPlayers;
    private final JavaPlugin plugin;
    private final Repository<IslandData> islandDataRepository;
    private final Repository<SkyblockPlayerData> playerDataRepository;

    public SimpleSkyblockPlayerManager(
            JavaPlugin plugin,
            IslandLoader islandLoader,
            Repository<SkyblockPlayerData> playerDataRepository,
            Repository<IslandData> islandDataRepository
    ) {
        this.islandLoader = Objects.requireNonNull(islandLoader, "islandLoader");
        this.cachedIslands = new ConcurrentHashMap<>();
        this.cachedPlayers = new ConcurrentHashMap<>();
        this.plugin = Objects.requireNonNull(plugin);
        this.islandDataRepository = Objects.requireNonNull(islandDataRepository);
        this.playerDataRepository = Objects.requireNonNull(playerDataRepository);
    }

    @Override
    public void loadIsland(UUID key) {
        loadIslandAsync(key);
    }

    @Override
    public CompletableFuture<Island> loadIslandAsync(UUID key) {
        CompletableFuture<Island> completableFuture = new CompletableFuture<>();
        if (cachedIslands.containsKey(key)) {
            completableFuture.complete(getIsland(key));

            return completableFuture;
        }

        return islandLoader.loadWithAsyncSlimeWorldLoading(key).whenComplete(((island, throwable) -> cachedIslands.put(key, island)));
    }

    @Override
    public Island getIsland(UUID key) {
        return cachedIslands.get(key);
    }

    @Override
    public Loader<Island, UUID> getIslandLoader() {
        return islandLoader;
    }

    @Override
    public void saveIslands() {
        cachedIslands.forEach((uuid, island) -> islandDataRepository.save(uuid.toString(), island.data()));
    }

    @Override
    public SkyblockPlayer loadPlayer(Player player) {
        SkyblockPlayer found = get(player);

        if (found == null) {
            found = load(player);
            loadIsland(found);
            this.cachedPlayers.put(player.getUniqueId(), found);
        }

        return found;
    }

    @Override
    public CompletableFuture<SkyblockPlayer> loadPlayerAsync(Player player) {
        return CompletableFuture.supplyAsync(() -> loadPlayer(player))
                .exceptionally((exc) -> {
                    Bukkit.getLogger().log(Level.SEVERE, "An error has occurred while loading the player data", exc);
                    return null;
                })
                .whenComplete((skyblockPlayer, throwable) -> this.cachedPlayers.put(player.getUniqueId(), skyblockPlayer));
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
    public void savePlayers() {
        cachedPlayers.forEach((uuid, skyblockPlayer) -> playerDataRepository.save(uuid.toString(), skyblockPlayer.data()));
    }

    private SkyblockPlayer load(Player player) {
        SkyblockPlayer skyblockPlayer = new SkyblockPlayer(loadPlayerData(player));

        player.setMetadata("skyblock-player-model", new FixedMetadataValue(plugin, skyblockPlayer));

        Bukkit.getPluginManager().callEvent(new PlayerDataLoadEvent(skyblockPlayer));
        return skyblockPlayer;
    }

    private SkyblockPlayerData loadPlayerData(Player player) {
        SkyblockPlayerData data = playerDataRepository.find(player.getUniqueId().toString());

        if (data == null) {
            data = createData(player);
        }

        return data;
    }

    private SkyblockPlayerData createData(Player player) {
        return new SkyblockPlayerData(
                new PlayerProfileManager(PlayerProfile.forDefaultUses(player))
        );
    }
}