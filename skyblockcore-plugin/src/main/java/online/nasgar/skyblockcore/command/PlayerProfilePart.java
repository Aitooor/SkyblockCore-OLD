package online.nasgar.skyblockcore.command;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;

import java.util.Objects;

public class PlayerProfilePart implements CommandPart {

    private final String name;

    public PlayerProfilePart(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void parse(CommandContext commandContext, ArgumentStack argumentStack) throws ArgumentParseException {
        SkyblockPlayer SkyblockPlayer = commandContext.getObject(SkyblockPlayer.class, "SkyblockPlayer");

        if (SkyblockPlayer != null) {
            commandContext.setValue(this, SkyblockPlayer.profiles().getProfileInUse());
        }
    }
}