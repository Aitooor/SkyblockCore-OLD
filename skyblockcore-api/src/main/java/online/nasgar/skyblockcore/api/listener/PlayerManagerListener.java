package online.nasgar.skyblockcore.api.listener;

import online.nasgar.skyblockcore.api.manager.SkyblockPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class PlayerManagerListener implements Listener {

    private final SkyblockPlayerManager playerManager;

    public PlayerManagerListener(SkyblockPlayerManager playerManager) {
        this.playerManager = Objects.requireNonNull(playerManager);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerManager.loadPlayerAsync(event.getPlayer());
    }

}