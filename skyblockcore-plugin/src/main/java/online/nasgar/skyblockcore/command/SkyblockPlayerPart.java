package online.nasgar.skyblockcore.command;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import online.nasgar.skyblockcore.api.manager.SkyblockPlayerManager;
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Objects;

public class SkyblockPlayerPart implements CommandPart {

    private final String name;
    private final SkyblockPlayerManager playerManager;

    public SkyblockPlayerPart(String name, SkyblockPlayerManager playerManager) {
        this.name = Objects.requireNonNull(name);
        this.playerManager = Objects.requireNonNull(playerManager);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void parse(CommandContext commandContext, ArgumentStack argumentStack) throws ArgumentParseException {
        CommandSender sender = commandContext.getObject(CommandSender.class, "SENDER");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            SkyblockPlayer SkyblockPlayer;

            List<MetadataValue> metadataValues = player.getMetadata("skyblock-player-model");

            if (metadataValues.isEmpty()) {
                SkyblockPlayer = playerManager.get(player);
            } else {
                MetadataValue value = metadataValues.get(0);

                if (value.value() instanceof SkyblockPlayer) {
                    SkyblockPlayer = (SkyblockPlayer) value.value();
                } else {
                    SkyblockPlayer = playerManager.get(player);
                }
            }

            commandContext.setValue(this, SkyblockPlayer);
        }
    }
}