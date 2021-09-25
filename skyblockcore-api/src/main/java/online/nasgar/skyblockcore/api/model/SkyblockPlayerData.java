package online.nasgar.skyblockcore.api.model;

import online.nasgar.commons.profile.PlayerProfileManager;

import java.util.Objects;

public class SkyblockPlayerData {

    private final PlayerProfileManager profileManager;

    public SkyblockPlayerData(PlayerProfileManager profileManager) {
        this.profileManager = Objects.requireNonNull(profileManager);
    }

    public PlayerProfileManager profiles() {
        return profileManager;
    }
}