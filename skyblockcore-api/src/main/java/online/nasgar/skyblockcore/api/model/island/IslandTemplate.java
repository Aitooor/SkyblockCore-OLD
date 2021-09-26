package online.nasgar.skyblockcore.api.model.island;

import online.nasgar.commons.util.LocationModel;
import online.nasgar.skyblockcore.api.builder.IslandTemplateBuilder;

import java.util.Objects;

public class IslandTemplate {

    private final LocationModel islandHome;
    private final String WESchematicName;
    private final String name;
    private final String worldModelName;

    public IslandTemplate(
            LocationModel islandHome,
            String WESchematicName,
            String worldModelName,
            String name
    ) {
        this.islandHome = Objects.requireNonNull(islandHome);
        this.WESchematicName = WESchematicName;
        this.name = Objects.requireNonNull(name);
        this.worldModelName = worldModelName;

        if (WESchematicName == null && worldModelName == null) {
            throw new IllegalArgumentException("no structure type provided");
        }
    }

    public LocationModel getIslandHome() {
        return islandHome;
    }

    public String getWESchematicName() {
        return WESchematicName;
    }

    public String getName() {
        return name;
    }

    public String getWorldModelName() {
        return worldModelName;
    }

    public static IslandTemplateBuilder builder(String name) {
        return new IslandTemplateBuilder(name);
    }
}