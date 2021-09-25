package online.nasgar.skyblockcore.api.model.island;

import online.nasgar.skyblockcore.api.manager.LocationManager;
import org.bukkit.World;

import java.util.Objects;
import java.util.UUID;

public class SkyblockIsland implements Island {

    private final UUID owner;
    private final World world;
    private final LocationManager homeManager;
    private final IslandData data;

    public SkyblockIsland(UUID id, World world, IslandData data) {
        this.owner = Objects.requireNonNull(id, "id");
        this.world = Objects.requireNonNull(world, "world");
        this.data = Objects.requireNonNull(data);

        this.homeManager = data.newHomesManager(world);
    }

    @Override
    public UUID getId() {
        return owner;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public LocationManager getHomes() {
        return homeManager;
    }

    @Override
    public IslandData data() {
        return data;
    }
}