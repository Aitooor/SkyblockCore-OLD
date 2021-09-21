package online.nasgar.skyblockcore.api.manager;

import online.nasgar.commons.profile.GlobalProfileManager;
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface SkyblockPlayerManager extends IslandManager<SkyblockPlayer> {

    SkyblockPlayer loadPlayer(Player player);

    boolean isCached(Player player);

    SkyblockPlayer get(Player player);

    Optional<SkyblockPlayer> getPlayer(Player player);

    GlobalProfileManager profileManager();

}