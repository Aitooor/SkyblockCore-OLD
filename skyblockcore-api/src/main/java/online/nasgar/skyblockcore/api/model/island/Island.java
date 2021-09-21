package online.nasgar.skyblockcore.api.model.island;

import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import org.bukkit.World;

public interface Island {

    SkyblockPlayer getOwner();

    World getWorld();

}