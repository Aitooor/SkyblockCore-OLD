package online.nasgar.skyblockcore.api.loader;

import com.github.imthenico.repositoryhelper.core.repository.Repository;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.plugin.SWMPlugin;
import online.nasgar.skyblockcore.api.Skyblock;
import online.nasgar.skyblockcore.api.model.island.Island;
import online.nasgar.skyblockcore.api.model.island.IslandData;
import online.nasgar.skyblockcore.api.model.island.SkyblockIsland;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class IslandLoader implements Loader<Island, UUID> {

    private final SlimeLoader loader;
    private final SlimeWorld worldTemplate;
    private final JavaPlugin javaPlugin;
    private final Repository<IslandData> islandDataRepository;

    public IslandLoader(
            SlimeLoader loader,
            SlimeWorld worldModel,
            JavaPlugin javaPlugin,
            Repository<IslandData> islandDataRepository
    ) {
        this.loader = Objects.requireNonNull(loader, "loader");
        this.worldTemplate = Objects.requireNonNull(worldModel, "worldModel");
        this.javaPlugin = Objects.requireNonNull(javaPlugin);
        this.islandDataRepository = Objects.requireNonNull(islandDataRepository);
    }

    @Override
    public Island load(UUID uuid) {
        SWMPlugin plugin = SWMPlugin.getInstance();

        String worldName = uuid.toString();
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            generate(getSlimeWorld(worldName, plugin), worldName);

            world = Bukkit.getWorld(worldName);
        }

        return newIsland(uuid, world);
    }

    public CompletableFuture<Island> loadWithAsyncSlimeWorldLoading(UUID params) {
        SWMPlugin plugin = SWMPlugin.getInstance();

        String worldName = params.toString();
        World world = Bukkit.getWorld(worldName);

        CompletableFuture<Island> futureIsland = new CompletableFuture<>();

        if (world == null) {
            CompletableFuture.supplyAsync(() -> getSlimeWorld(worldName, plugin), Skyblock.SKYBLOCK_THREAD_POOL)
                    .whenComplete(((slimeWorld, throwable) ->
                            generate(slimeWorld, worldName).whenComplete((bukkitWorld, throwable1) ->
                                    futureIsland.complete(newIsland(params, bukkitWorld)))))
                    .exceptionally((throwable) -> {
                        Bukkit.getLogger().log(Level.SEVERE, "An exception has occurred while loading the world");
                        return null;
                    });
        } else {
            futureIsland.complete(newIsland(params, world));
        }

        return futureIsland;
    }

    private IslandData createData() {
        return new IslandData(new ArrayList<>());
    }

    private SlimeWorld loadSlimeWorld(String worldName, SWMPlugin plugin) throws CorruptedWorldException, NewerFormatException, WorldInUseException, UnknownWorldException, IOException {
        return plugin.loadWorld(loader, worldName, true, worldTemplate.getPropertyMap());
    }

    private CompletableFuture<World> generate(SlimeWorld slimeWorld, String worldName) {
        SWMPlugin plugin = SWMPlugin.getInstance();

        CompletableFuture<World> completableFuture = new CompletableFuture<>();

        Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin,
                () -> {
                    plugin.generateWorld(slimeWorld);
                    completableFuture.complete(Bukkit.getWorld(worldName));
                });

        return completableFuture;
    }

    private SlimeWorld cloneWorld(String worldName) {
        try {
            return worldTemplate.clone(worldName, loader);
        } catch (WorldAlreadyExistsException | IOException e) {
            try {
                return SWMPlugin.getInstance().createEmptyWorld(loader, worldTemplate.getName(), true, new SlimePropertyMap());
            } catch (WorldAlreadyExistsException | IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    private SlimeWorld getSlimeWorld(String worldName, SWMPlugin plugin) {
        SlimeWorld slimeWorld;
        try {
            slimeWorld = loadSlimeWorld(worldName, plugin);
        } catch (CorruptedWorldException | NewerFormatException | WorldInUseException | IOException e) {
            throw new RuntimeException(e);
        } catch (UnknownWorldException e) {
            slimeWorld = cloneWorld(worldName);
        }

        return slimeWorld;
    }

    private Island newIsland(UUID params, World world) {
        IslandData islandData = islandDataRepository.find(params.toString());

        if (islandData == null)
            islandData = createData();

        return new SkyblockIsland(params, world, islandData);
    }
}