package online.nasgar.skyblockcore.api.manager;

import online.nasgar.skyblockcore.api.loader.Loader;
import online.nasgar.skyblockcore.api.model.island.Island;

public interface IslandManager<K> {

    Island loadIsland(K key);

    Island getIsland(K key);

    Loader<Island, K> getIslandLoader();

}