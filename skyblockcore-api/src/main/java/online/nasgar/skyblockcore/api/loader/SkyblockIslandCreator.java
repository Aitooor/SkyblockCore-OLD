package online.nasgar.skyblockcore.api.loader;

import com.github.imthenico.repositoryhelper.core.repository.Repository;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.plugin.SWMPlugin;
import online.nasgar.skyblockcore.api.model.island.Island;
import online.nasgar.skyblockcore.api.model.island.IslandData;
import online.nasgar.skyblockcore.api.model.island.IslandTemplate;
import online.nasgar.skyblockcore.api.model.island.SkyblockIsland;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static online.nasgar.skyblockcore.api.util.Exceptions.bukkitDebug;

public class SkyblockIslandCreator implements Loader<Island, UUID> {

    private final SlimeLoader loader;
    private final SlimeWorld worldTemplate;
    private final JavaPlugin javaPlugin;
    private final Repository<IslandData> islandDataRepository;
    private final Map<String, SlimeWorld> cachedModelWorlds;
    private final SWMPlugin swmPlugin;

    public SkyblockIslandCreator(
            SlimeLoader loader,
            SlimeWorld worldModel,
            JavaPlugin javaPlugin,
            Repository<IslandData> islandDataRepository
    ) {
        this.loader = Objects.requireNonNull(loader, "loader");
        this.worldTemplate = Objects.requireNonNull(worldModel, "worldModel");
        this.javaPlugin = Objects.requireNonNull(javaPlugin);
        this.islandDataRepository = Objects.requireNonNull(islandDataRepository);
        this.cachedModelWorlds = new HashMap<>();
        this.swmPlugin = SWMPlugin.getInstance();
    }

    @Override
    public Island load(UUID uuid) {
        String worldName = uuid.toString();
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            generate(getSlimeWorld(worldName));

            world = Bukkit.getWorld(worldName);
        }

        return newIsland(uuid, world);
    }

    public CompletableFuture<Island> prepareForAsyncLoad(UUID uuid) {
        CompletableFuture<Island> completableFuture = new CompletableFuture<>();

        String worldName = uuid.toString();
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CompletableFuture<World> futureWorldPromise = generate(getSlimeWorld(worldName));

            if (futureWorldPromise.isCancelled()) {
                completableFuture.cancel(true);
                return completableFuture;
            }

            futureWorldPromise.whenComplete((bukkitWorld, exc) -> completableFuture.complete(newIsland(uuid, bukkitWorld)));
        }

        return completableFuture.exceptionally(bukkitDebug());
    }

    public CompletableFuture<Island> createIsland(UUID uuid, IslandTemplate template) {
        if (template != null) {
            return createFromTemplate(uuid, uuid.toString(), template);
        } else {
            return createEmptyIsland(uuid, uuid.toString());
        }
    }

    private IslandData createData() {
        return new IslandData(new ArrayList<>());
    }

    private SlimeWorld loadSlimeWorld(String worldName) throws CorruptedWorldException, NewerFormatException, WorldInUseException, UnknownWorldException, IOException {
        return swmPlugin.loadWorld(loader, worldName, true, worldTemplate.getPropertyMap());
    }

    private CompletableFuture<World> generate(SlimeWorld slimeWorld) {
        CompletableFuture<World> completableFuture = new CompletableFuture<>();
        if (slimeWorld == null) {
            completableFuture.cancel(true);
            return completableFuture;
        }

        String currentThreadName = Thread.currentThread().getName();

        Runnable generationWorldRun = () -> {
            swmPlugin.generateWorld(slimeWorld);
            completableFuture.complete(Bukkit.getWorld(slimeWorld.getName()));
        };

        if (!currentThreadName.equals("main")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, generationWorldRun);
        } else {
            generationWorldRun.run();
        }

        return completableFuture;
    }

    private SlimeWorld cloneWorld(String toClone, String worldName) {
        if (toClone == null)
            return null;

        try {
            SlimeWorld worldModel = getSlimeWorld(toClone);

            if (worldModel != null)
                return worldTemplate.clone(worldName, loader);
        } catch (WorldAlreadyExistsException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SlimeWorld getSlimeWorld(String worldName) {
        SlimeWorld slimeWorld = this.cachedModelWorlds.get(worldName);

        if (slimeWorld == null) {
            try {
                slimeWorld = loadSlimeWorld(worldName);
                this.cachedModelWorlds.put(worldName, slimeWorld);
            } catch (CorruptedWorldException | NewerFormatException | WorldInUseException | IOException e) {
                throw new RuntimeException(e);
            } catch (UnknownWorldException ignored) {

            }
        }

        return slimeWorld;
    }

    private Island newIsland(UUID params, World world) {
        IslandData islandData = islandDataRepository.find(params.toString());

        if (islandData == null)
            islandData = createData();

        return new SkyblockIsland(params, world, islandData);
    }

    private CompletableFuture<Island> createFromTemplate(UUID uuid, String finalName, IslandTemplate template) {
        CompletableFuture<Island> completableFuture = new CompletableFuture<>();

        String schematicName = template.getWESchematicName();
        String worldModelName = template.getWorldModelName();

        SlimeWorld world = cloneWorld(worldModelName, finalName);
        world = world != null ? world : createEmptyWorld(finalName);

        generate(world)
                .whenComplete((bukkitWorld, exc) -> completableFuture.complete(newIsland(uuid, bukkitWorld)));

        if (schematicName != null) {
            // paste structure
        }

        return completableFuture;
    }

    private CompletableFuture<Island> createEmptyIsland(UUID uuid, String finalName) {
        CompletableFuture<Island> completableFuture = new CompletableFuture<>();
        SlimeWorld world = createEmptyWorld(finalName);

        generate(world)
                .whenComplete((bukkitWorld, exc) -> completableFuture.complete(newIsland(uuid, bukkitWorld)));

        return completableFuture;
    }

    private SlimeWorld createEmptyWorld(String worldName) {
        try {
            swmPlugin.createEmptyWorld(loader, worldName, true, new SlimePropertyMap());
        } catch (WorldAlreadyExistsException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}