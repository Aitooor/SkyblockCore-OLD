package online.nasgar.skyblockcore.api.model.island;

import online.nasgar.commons.util.LocationModel;
import online.nasgar.skyblockcore.api.manager.LocationManager;
import online.nasgar.skyblockcore.api.manager.SimpleLocationManager;
import org.bukkit.World;

import java.util.List;
import java.util.Objects;

public class IslandData {

    private final List<LocationModel> homes;

    public IslandData(
            List<LocationModel> homes
    ) {
        this.homes = Objects.requireNonNull(homes);
    }

    public LocationManager newHomesManager(World world) {
        return new SimpleLocationManager(world, homes);
    }

}