package online.nasgar.skyblockcore.api.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface LocationManager {

    Location getLocation(int index);

    List<Location> getLocations();

    LocationManager addLocation(Location location);

    void teleport(Player player, int index);

    void teleportToMain(Player player);

    Location getMainLocation();

    LocationManager assignMainLocation(Location location);
}