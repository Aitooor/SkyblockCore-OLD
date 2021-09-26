package online.nasgar.skyblockcore.api.manager;

import com.github.imthenico.repositoryhelper.core.repository.Repository;
import online.nasgar.commons.profile.PlayerProfile;
import online.nasgar.commons.profile.PlayerProfileManager;
import online.nasgar.skyblockcore.api.event.PlayerDataLoadEvent;
import online.nasgar.skyblockcore.api.loader.Loader;
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import online.nasgar.skyblockcore.api.model.SkyblockPlayerData;
import online.nasgar.skyblockcore.api.model.island.Island;
import online.nasgar.skyblockcore.api.model.island.IslandTemplate;
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
import java.util.concurrent.Executor;

public class SimpleSkyblockPlayerManager implements SkyblockPlayerManager {

    private final IslandManager<UUID> delegate;
    private final Map<UUID, SkyblockPlayer> cachedPlayers;
    private final JavaPlugin plugin;
    private final Repository<SkyblockPlayerData> playerDataRepository;
    private final Executor executor;

    public SimpleSkyblockPlayerManager(
            JavaPlugin plugin,
            Repository<SkyblockPlayerData> playerDataRepository,
            IslandManager<UUID> delegate,
            Executor executor
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        this.playerDataRepository = Objects.requireNonNull(playerDataRepository);
        this.delegate = Objects.requireNonNull(delegate);
        this.cachedPlayers = new ConcurrentHashMap<>();
        this.executor = executor;
    }

    @Override
    public CompletableFuture<SkyblockPlayer> loadPlayer(Player player) {
        CompletableFuture<SkyblockPlayer> completableFuture = new CompletableFuture<>();

        completableFuture.whenComplete((skyblockPlayer, throwable) -> this.cachedPlayers.put(player.getUniqueId(), skyblockPlayer));

        Runnable completion = () -> completableFuture.complete(load(player));

        if (asyncModeEnabled()) {
            executor.execute(completion);
        } else {
            completion.run();
        }

        return completableFuture;
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

        Bukkit.getPluginManager().callEvent(new PlayerDataLoadEvent(skyblockPlayer, asyncModeEnabled()));
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

    @Override
    public CompletableFuture<Island> loadIsland(UUID key, boolean cache) {
        return delegate.loadIsland(key, cache);
    }

    @Override
    public Island getIsland(UUID key) {
        return delegate.getIsland(key);
    }

    @Override
    public CompletableFuture<Island> createIfAbsent(UUID key, String templateName) {
        return delegate.createIfAbsent(key, templateName);
    }

    @Override
    public Loader<Island, UUID> getIslandLoader() {
        return delegate.getIslandLoader();
    }

    @Override
    public CompletableFuture<Island> createIsland(UUID key, boolean cache) {
        return delegate.createIsland(key, cache);
    }

    @Override
    public CompletableFuture<Island> createIslandWithTemplate(UUID key, String templateName, boolean cache) {
        return delegate.createIslandWithTemplate(key, templateName, cache);
    }

    @Override
    public CompletableFuture<Island> createIslandWithTemplate(UUID key, IslandTemplate template, boolean cache) {
        return delegate.createIslandWithTemplate(key, template, cache);
    }

    @Override
    public void registerTemplate(IslandTemplate islandTemplate) {
        delegate.registerTemplate(islandTemplate);
    }

    @Override
    public IslandTemplate getTemplate(String templateName) {
        return delegate.getTemplate(templateName);
    }

    @Override
    public void saveIslands() {
        delegate.saveIslands();
    }

    @Override
    public boolean asyncModeEnabled() {
        return executor != null;
    }
}