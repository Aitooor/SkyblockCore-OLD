package online.nasgar.skyblockcore.api.model;

import online.nasgar.commons.profile.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SkyblockPlayer {

    private final PlayerProfileManager profileManager;

    public SkyblockPlayer(PlayerProfileManager profileManager) {
        this.profileManager = Objects.requireNonNull(profileManager, "profileManager");
    }

    public Player asBukkitPlayer() {
        return Bukkit.getPlayer(profileManager.getOwnerId());
    }

    public PlayerProfileManager profiles() {
        return profileManager;
    }
}