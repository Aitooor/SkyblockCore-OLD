package online.nasgar.skyblockcore.api.model;

import online.nasgar.commons.profile.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SkyblockPlayer {

    private final SkyblockPlayerData data;

    public SkyblockPlayer(SkyblockPlayerData skyblockPlayerData) {
        this.data = Objects.requireNonNull(skyblockPlayerData);
    }

    public Player asBukkitPlayer() {
        return Bukkit.getPlayer(profiles().getOwnerId());
    }

    public PlayerProfileManager profiles() {
        return data().profiles();
    }

    public SkyblockPlayerData data() {
        return data;
    }
}