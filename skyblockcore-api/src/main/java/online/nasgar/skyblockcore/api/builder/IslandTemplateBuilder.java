package online.nasgar.skyblockcore.api.builder;

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

        spawnWorldCheck();
        return this;
    }

    public IslandTemplateBuilder setWESchematicName(String WESchematicName) {
        this.WESchematicName = WESchematicName;
        return this;
    }

    public IslandTemplateBuilder setWorldModelName(String worldModelName) {
        this.worldModelName = worldModelName;

        spawnWorldCheck();
        return this;
    }

    public LocationModel getIslandSpawn() {
        return islandSpawn;
    }

    public String getWESchematicName() {
        return WESchematicName;
    }

    public String getWorldModelName() {
        return worldModelName;
    }

    public IslandTemplate build() {
        return new IslandTemplate(islandSpawn, WESchematicName, worldModelName, name);
    }

    private void spawnWorldCheck() {
        if (worldModelName != null && islandSpawn != null) {
            if (!islandSpawn.getWorldName().equals(worldModelName)) {
                this.islandSpawn = new LocationModel(
                    islandSpawn.getX(),
                    islandSpawn.getY(),
                    islandSpawn.getZ(),
                    islandSpawn.getYaw(),
                    islandSpawn.getPitch(),
                    worldModelName
                );
            }
        }
    }
}