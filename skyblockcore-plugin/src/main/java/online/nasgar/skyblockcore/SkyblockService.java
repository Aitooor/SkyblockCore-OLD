package online.nasgar.skyblockcore;

import com.github.imthenico.repositoryhelper.core.serialization.SimpleAdapterRegistry;
import com.github.imthenico.repositoryhelper.core.serialization.SimpleSerializationRegistry;
import com.github.imthenico.repositoryhelper.core.serialization.registry.AdapterRegistry;
import com.github.imthenico.repositoryhelper.core.serialization.registry.SerializationRegistry;
import com.github.imthenico.repositoryhelper.json.gson.GsonAdapter;
import com.google.gson.Gson;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.plugin.SWMPlugin;
import online.nasgar.commons.configuration.Configuration;
import online.nasgar.commons.profile.PlayerProfileManager;
import online.nasgar.commons.registry.RepositoryObjectRegistry;
import online.nasgar.skyblockcore.api.Skyblock;
import online.nasgar.skyblockcore.api.loader.IslandLoader;
import online.nasgar.skyblockcore.api.manager.SimpleSkyblockPlayerManager;
import online.nasgar.skyblockcore.api.manager.SkyblockPlayerManager;
import online.nasgar.skyblockcore.api.settings.SkyblockSettings;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class SkyblockService implements Skyblock {

    private final SkyblockPlayerManager skyblockPlayerManager;
    private final AdapterRegistry serializationRegistry;
    private final SkyblockSettings settings;
    private final JavaPlugin plugin;
    private SlimeWorld islandWorldTemplate;

    public SkyblockService(Configuration mainConfig, JavaPlugin plugin) {
        this.serializationRegistry = new SimpleAdapterRegistry();

        this.serializationRegistry.registerAdapter("gson", new GsonAdapter(new Gson()), false);

        this.settings = new SkyblockSettings(mainConfig, this);
        this.skyblockPlayerManager = initPlayerManager(this.settings);
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public SkyblockPlayerManager playerManager() {
        return skyblockPlayerManager;
    }

    @Override
    public AdapterRegistry serializationRegistry() {
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

        RepositoryObjectRegistry<UUID, PlayerProfileManager> registry = new RepositoryObjectRegistry<>(
                settings.getRepository("player-data")
        );

        try {
            islandWorldTemplate = swmPlugin.loadWorld(loader, "skyblock-is-template", true, new SlimePropertyMap());
        } catch (UnknownWorldException | CorruptedWorldException | IOException | NewerFormatException | WorldInUseException e) {
            e.printStackTrace();
        }

        return new SimpleSkyblockPlayerManager(new IslandLoader(loader, islandWorldTemplate), registry);
    }
}