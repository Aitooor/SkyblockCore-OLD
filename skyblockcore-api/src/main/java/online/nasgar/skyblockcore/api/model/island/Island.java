package online.nasgar.skyblockcore.api.model.island;

import online.nasgar.skyblockcore.api.manager.LocationManager;
import org.bukkit.World;

import java.util.UUID;

public interface Island {

    UUID getId();

    World getWorld();

    LocationManager getHomes();

    IslandData data();
}