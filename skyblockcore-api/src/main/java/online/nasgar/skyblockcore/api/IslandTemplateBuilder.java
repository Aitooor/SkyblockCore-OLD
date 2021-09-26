package online.nasgar.skyblockcore.api;

import online.nasgar.commons.util.LocationModel;
import online.nasgar.skyblockcore.api.model.island.IslandTemplate;

import java.util.Objects;

public class IslandTemplateBuilder {

    private LocationModel islandSpawn;
    private String worldModelName;
    private String WESchematicName;
    private final String name;

    public IslandTemplateBuilder(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public IslandTemplateBuilder setIslandSpawn(LocationModel islandSpawn) {
        this.islandSpawn = islandSpawn;
        return this;
    }

    public IslandTemplateBuilder setWESchematicName(String WESchematicName) {
        this.WESchematicName = WESchematicName;
        return this;
    }

    public IslandTemplateBuilder setWorldModelName(String worldModelName) {
        this.worldModelName = worldModelName;
        return this;
    }

    public IslandTemplate build() {
        return new IslandTemplate(islandSpawn, WESchematicName, worldModelName, name);
    }
}