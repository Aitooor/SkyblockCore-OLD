package online.nasgar.skyblockcore.api.manager;

import online.nasgar.commons.util.LocationModel;
import online.nasgar.commons.validate.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleLocationManager implements LocationManager {

    private final World world;
    private final List<LocationModel> locations;

    public SimpleLocationManager(LocationModel main, World world) {
        this.world = Objects.requireNonNull(world);
        this.locations = new ArrayList<>();
        this.locations.add(main);
    }

    public SimpleLocationManager(World world) {
        this(LocationModel.of(world.getSpawnLocation()), world);
    }

    public SimpleLocationManager(LocationModel main, World world, List<LocationModel> locations) {
        this(main, world);

        this.locations.addAll(locations);
    }

    public SimpleLocationManager(World world, List<LocationModel> locations) {
        this.world = Objects.requireNonNull(world);
        this.locations = new ArrayList<>(Objects.requireNonNull(locations));

        if (locations.isEmpty()) {
            this.locations.add(LocationModel.of(world.getSpawnLocation()));
        }
    }

    @Override
    public Location getLocation(int index) {
        if (index < locations.size()) {
            LocationModel model = locations.get(index);

            if (model != null)
                return model.asBukkitLocation();
        }

        return null;
    }

    @Override
    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();

        for (LocationModel location : this.locations) {
            locations.add(location.asBukkitLocation());
        }

        return locations;
    }

    @Override
    public LocationManager addLocation(Location location) {
        Objects.requireNonNull(location);

        Validate.isTrue(location.getWorld() == world);

        this.locations.add(LocationModel.of(Objects.requireNonNull(location)));
        return this;
    }

    @Override
    public void teleport(Player player, int index) {
        Location location = getLocation(index);

        if (location != null)
            player.teleport(location);
    }

    @Override
    public void teleportToMain(Player player) {
        player.teleport(getMainLocation());
    }

    @Override
    public Location getMainLocation() {
        return getLocation(0);
    }

    @Override
    public LocationManager assignMainLocation(Location location) {
        Objects.requireNonNull(location);

        Validate.isTrue(location.getWorld() == world);

        if (locations.size() == 0) {
            this.locations.add(LocationModel.of(location));
        } else {
            this.locations.set(0, LocationModel.of(location));
        }

        return this;
    }

    private Location toLocation(LocationModel locationModel) {
        return new Location(
                world,
                locationModel.getX(),
                locationModel.getY(),
                locationModel.getZ(),
                locationModel.getYaw(),
                locationModel.getPitch()
        );
    }
}