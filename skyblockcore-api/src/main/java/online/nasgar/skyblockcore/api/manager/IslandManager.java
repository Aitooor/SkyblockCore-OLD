package online.nasgar.skyblockcore.api.manager;

import online.nasgar.skyblockcore.api.loader.Loader;
import online.nasgar.skyblockcore.api.model.island.Island;

import java.util.concurrent.CompletableFuture;

public interface IslandManager<K> {

    void loadIsland(K key);

    CompletableFuture<Island> loadIslandAsync(K key);

    Island getIsland(K key);

    Loader<Island, K> getIslandLoader();

    void saveIslands();

}