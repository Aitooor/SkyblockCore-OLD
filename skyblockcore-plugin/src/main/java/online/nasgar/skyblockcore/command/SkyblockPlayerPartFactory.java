package online.nasgar.skyblockcore.command;

import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.part.CommandPart;
import online.nasgar.skyblockcore.api.manager.SkyblockPlayerManager;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

public class SkyblockPlayerPartFactory implements PartFactory {

    private final SkyblockPlayerManager playerManager;

    public SkyblockPlayerPartFactory(SkyblockPlayerManager playerManager) {
        this.playerManager = Objects.requireNonNull(playerManager);
    }

    @Override
    public CommandPart createPart(String s, List<? extends Annotation> list) {
        return new SkyblockPlayerPart(s, playerManager);
    }
}