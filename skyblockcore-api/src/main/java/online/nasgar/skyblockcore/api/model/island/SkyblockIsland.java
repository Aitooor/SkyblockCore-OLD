package online.nasgar.skyblockcore.api.model.island;

import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import org.bukkit.World;

import java.util.Objects;

public class SkyblockIsland implements Island {

    private final SkyblockPlayer owner;

    private final World world;

    public SkyblockIsland(SkyblockPlayer owner, World world) {
        this.owner = Objects.requireNonNull(owner, "owner");
        this.world = Objects.requireNonNull(world, "world");
    }

    @Override
    public SkyblockPlayer getOwner() {
        return owner;
    }

    @Override
    public World getWorld() {
        return world;
    }
}