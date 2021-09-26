package online.nasgar.skyblockcore;

import com.github.imthenico.repositoryhelper.core.repository.Repository;
import com.github.imthenico.repositoryhelper.core.serialization.SimpleAdapterRegistry;
import com.github.imthenico.repositoryhelper.core.serialization.registry.AdapterRegistry;
import com.github.imthenico.repositoryhelper.json.gson.GsonAdapter;
import com.google.gson.Gson;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.plugin.SWMPlugin;
import online.nasgar.commons.configuration.Configuration;
import online.nasgar.skyblockcore.api.Skyblock;
import online.nasgar.skyblockcore.api.listener.PlayerManagerListener;
import online.nasgar.skyblockcore.api.loader.SkyblockIslandCreator;
import online.nasgar.skyblockcore.api.manager.SimpleSkyblockPlayerManager;
import online.nasgar.skyblockcore.api.manager.SkyblockIslandManager;
import online.nasgar.skyblockcore.api.manager.SkyblockPlayerManager;
import online.nasgar.skyblockcore.api.model.SkyblockPlayerData;
import online.nasgar.skyblockcore.api.model.island.IslandData;
import online.nasgar.skyblockcore.api.settings.SkyblockSettings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SkyblockService implements Skyblock {

    private final SkyblockPlayerManager skyblockPlayerManager;
    private final AdapterRegistry serializationRegistry;
    private final SkyblockSettings settings;
    private final JavaPlugin plugin;

    public SkyblockService(Configuration mainConfig, JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
        this.serializationRegistry = new SimpleAdapterRegistry();

        this.serializationRegistry.registerAdapter("gson", new GsonAdapter(new Gson()), false);

        this.settings = new SkyblockSettings(mainConfig, this);
        this.skyblockPlayerManager = initPlayerManager(settings);

        Bukkit.getPluginManager().registerEvents(new PlayerManagerListener(skyblockPlayerManager), plugin);
    }

    @Override
    public SkyblockPlayerManager playerManager() {
        return skyblockPlayerManager;
    }

    @Override
    public AdapterRegistry adapterRegistry() {
        return serializationRegistry;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public SkyblockSettings getSettings() {
        return settings;
    }

    private SkyblockPlayerManager initPlayerManager(SkyblockSettings settings) {
        SWMPlugin swmPlugin = SWMPlugin.getInstance();

        SlimeLoader loader = swmPlugin.getLoader("mysql");

        Repository<SkyblockPlayerData> playerDataRepository = settings.getRepository("player-data");
        Repository<IslandData> islandDataRepository = settings.getRepository("island-data");

        return new SimpleSkyblockPlayerManager(
                plugin,
                playerDataRepository,
                new SkyblockIslandManager(
                        new SkyblockIslandCreator(loader, plugin, islandDataRepository),
                        islandDataRepository,
                        null
                ),
                Skyblock.SKYBLOCK_THREAD_POOL
        );
    }
}