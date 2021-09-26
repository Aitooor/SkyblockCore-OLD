package online.nasgar.skyblockcore.api.manager;

import online.nasgar.skyblockcore.api.util.Asynchronous;
import online.nasgar.skyblockcore.api.loader.Loader;
import online.nasgar.skyblockcore.api.model.island.Island;
import online.nasgar.skyblockcore.api.model.island.IslandTemplate;

import java.util.concurrent.CompletableFuture;

public interface IslandManager<K> extends Asynchronous {

    CompletableFuture<Island> loadIsland(K key, boolean cache);

    Island getIsland(K key);

    CompletableFuture<Island> createIfAbsent(K key, String templateName);

    Loader<Island, K> getIslandLoader();

    CompletableFuture<Island> createIsland(K key, boolean cache);

    CompletableFuture<Island> createIslandWithTemplate(K key, String templateName, boolean cache);

    CompletableFuture<Island> createIslandWithTemplate(K key, IslandTemplate template, boolean cache);

    void registerTemplate(IslandTemplate islandTemplate);

    IslandTemplate getTemplate(String templateName);

    void saveIslands();

}