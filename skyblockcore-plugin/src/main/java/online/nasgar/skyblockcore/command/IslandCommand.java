package online.nasgar.skyblockcore.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import online.nasgar.skyblockcore.api.manager.SkyblockPlayerManager;
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import online.nasgar.skyblockcore.api.model.island.Island;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Command(names = {"is", "island"})
public class IslandCommand implements CommandClass {

    private final SkyblockPlayerManager playerManager;

    public IslandCommand(SkyblockPlayerManager playerManager) {
        this.playerManager = Objects.requireNonNull(playerManager);
    }

    @Command(names = "")
    public boolean rootCommand(SkyblockPlayer skyblockPlayer) {
        Player player = skyblockPlayer.asBukkitPlayer();
        CompletableFuture<Island> islandCompletableFuture = playerManager.createIslandIfAbsent(skyblockPlayer);

        islandCompletableFuture.whenComplete((island, thr) -> island.getHomes().teleportToMain(player));
        
        return true;
    }
}