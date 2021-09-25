package online.nasgar.skyblockcore.command;

import me.fixeddev.commandflow.annotated.part.AbstractModule;
import me.fixeddev.commandflow.bukkit.factory.PlayerPartFactory;
import online.nasgar.skyblockcore.api.manager.SkyblockPlayerManager;
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;

import java.util.Objects;

public class SkyblockCommandModule extends AbstractModule {

    private final SkyblockPlayerManager playerManager;

    public SkyblockCommandModule(SkyblockPlayerManager playerManager) {
        this.playerManager = Objects.requireNonNull(playerManager);
    }

    @Override
    public void configure() {
        this.bindFactory(SkyblockPlayer.class, new SkyblockPlayerPartFactory(playerManager));
        this.bindFactory(PlayerProfilePart.class, new PlayerPartFactory());
    }
}