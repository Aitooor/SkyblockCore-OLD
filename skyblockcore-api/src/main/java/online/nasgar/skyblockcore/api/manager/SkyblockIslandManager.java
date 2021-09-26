package online.nasgar.skyblockcore.api.manager;

import com.github.imthenico.repositoryhelper.core.repository.Repository;
import online.nasgar.skyblockcore.api.loader.SkyblockIslandCreator;
import online.nasgar.skyblockcore.api.loader.Loader;
import online.nasgar.skyblockcore.api.model.island.Island;
import online.nasgar.skyblockcore.api.model.island.IslandData;
import online.nasgar.skyblockcore.api.model.island.IslandTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class SkyblockIslandManager implements IslandManager<UUID> {

    private final Map<UUID, Island> cachedIslands;
    private final SkyblockIslandCreator skyblockIslandCreator;
    private final Repository<IslandData> islandDataRepository;
    private final Map<String, IslandTemplate> cachedTemplates;
    private final Executor executor;

    public SkyblockIslandManager(
            SkyblockIslandCreator skyblockIslandCreator,
            Repository<IslandData> islandDataRepository,
            Executor executor
    ) {
        this.skyblockIslandCreator = Objects.requireNonNull(skyblockIslandCreator, "islandLoader");
        this.islandDataRepository = Objects.requireNonNull(islandDataRepository);
        this.cachedIslands = new ConcurrentHashMap<>();
        this.cachedTemplates = new ConcurrentHashMap<>();
        this.executor = executor;
    }

    @Override
    public CompletableFuture<Island> loadIsland(UUID key, boolean cache) {
        CompletableFuture<Island> completableFuture = new CompletableFuture<>();
        if (cachedIslands.containsKey(key)) {
            completableFuture.complete(getIsland(key));

            return completableFuture;
        }

        Runnable completion = () -> skyblockIslandCreator
                .prepareForAsyncLoad(key)
                .whenComplete(((island, throwable) -> completableFuture.complete(island)));

        if (asyncModeEnabled()) {
            executor.execute(completion);
        } else {
            completion.run();
        }

        completableFuture.whenComplete(((island, throwable) -> cachedIslands.put(key, island)));

        return completableFuture;
    }

    @Override
    public Island getIsland(UUID key) {
        return cachedIslands.get(key);
    }

    @Override
    public CompletableFuture<Island> createIfAbsent(UUID key, String templateName) {
        CompletableFuture<Island> completableFuture = new CompletableFuture<>();

        Island island = getIsland(key);

        if (island != null) {
            completableFuture.complete(island);
        } else {
            CompletableFuture<Island> inLoad = loadIsland(key, true);

            if (inLoad.isCancelled()) {
                if (templateName == null || !cachedTemplates.containsKey(templateName)){
                    createIsland(key, true);
                } else {
                    createIslandWithTemplate(key, templateName, true);
                }
            } else {
                return inLoad;
            }
        }

        return completableFuture;
    }

    @Override
    public Loader<Island, UUID> getIslandLoader() {
        return skyblockIslandCreator;
    }

    @Override
    public CompletableFuture<Island> createIsland(UUID key, boolean cache) {
        CompletableFuture<Island> completableFuture = new CompletableFuture<>();

        Runnable completion = () -> skyblockIslandCreator
                .createIsland(key, null)
                .whenComplete((island, throwable) -> {
                    if (cache)
                        cachedIslands.put(key, island);

                    completableFuture.complete(island);
        });

        if (asyncModeEnabled()) {
            executor.execute(completion);
        } else {
            completion.run();
        }

        return completableFuture;
    }

    @Override
    public CompletableFuture<Island> createIslandWithTemplate(UUID key, String templateName, boolean cache) {
        IslandTemplate islandTemplate = Objects.requireNonNull(cachedTemplates.get(templateName), templateName + " is not a registered template");

        return createIslandWithTemplate(key, islandTemplate, cache);
    }

    @Override
    public CompletableFuture<Island> createIslandWithTemplate(UUID key, IslandTemplate template, boolean cache) {
        CompletableFuture<Island> completableFuture = new CompletableFuture<>();

        Runnable completion = () -> skyblockIslandCreator
                .createIsland(key, template)
                .whenComplete((island, throwable) -> {
            if (cache)
                cachedIslands.put(key, island);

            completableFuture.complete(island);
        });

        if (asyncModeEnabled()) {
            executor.execute(completion);
        } else {
            completion.run();
        }

        return completableFuture;
    }

    @Override
    public void registerTemplate(IslandTemplate islandTemplate) {
        this.cachedTemplates.put(Objects.requireNonNull(islandTemplate).getName(), islandTemplate);
    }

    @Override
    public IslandTemplate getTemplate(String templateName) {
        return cachedTemplates.get(templateName);
    }

    @Override
    public void saveIslands() {
        cachedIslands.forEach((uuid, island) -> islandDataRepository.save(uuid.toString(), island.data()));
    }

    @Override
    public boolean asyncModeEnabled() {
        return executor != null;
    }
}