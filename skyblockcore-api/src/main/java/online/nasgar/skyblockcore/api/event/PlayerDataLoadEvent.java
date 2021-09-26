package online.nasgar.skyblockcore.api.event;

import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;

public class PlayerDataLoadEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final SkyblockPlayer player;

    public PlayerDataLoadEvent(SkyblockPlayer player, boolean async) {
        super(async);
        this.player = Objects.requireNonNull(player, "player");
    }

    public SkyblockPlayer getPlayerModel() {
        return player;
    }

    public Player getPlayer() {
        return player.asBukkitPlayer();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}