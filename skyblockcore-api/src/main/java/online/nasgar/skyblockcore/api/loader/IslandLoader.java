package online.nasgar.skyblockcore.api.loader;

import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.plugin.SWMPlugin;
import online.nasgar.skyblockcore.api.Skyblock;
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import online.nasgar.skyblockcore.api.model.island.Island;
import online.nasgar.skyblockcore.api.model.island.SkyblockIsland;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class IslandLoader implements Loader<Island, SkyblockPlayer> {

    private final SlimeLoader loader;
    private final SlimeWorld worldTemplate;

    public IslandLoader(SlimeLoader loader, SlimeWorld worldModel) {
        this.loader = Objects.requireNonNull(loader, "loader");
        this.worldTemplate = Objects.requireNonNull(worldModel, "worldModel");
    }

    @Override
    public Island load(SkyblockPlayer player) {
        SWMPlugin plugin = SWMPlugin.getInstance();

        UUID uuid = Objects.requireNonNull(player, "player cannot be null").profiles().getProfileInUse().getProfileId();
        World world = Bukkit.getWorld(uuid.toString());

        if (world == null) {
            try {
                plugin.loadWorld(loader, uuid.toString(), true, worldTemplate.getPropertyMap());
            } catch (IOException | CorruptedWorldException | NewerFormatException | WorldInUseException e) {
                e.printStackTrace();
            } catch (UnknownWorldException e) {
                world = create(player, plugin);
            }
        }

        return new SkyblockIsland(player, world);
    }

    public World create(SkyblockPlayer player, SWMPlugin plugin) {
        UUID uuid = player.profiles().getOwnerId();

        try {
            CompletableFuture<SlimeWorld> completableFuture = CompletableFuture.supplyAsync(
                    () -> cloneWorld(uuid), Skyblock.SKYBLOCK_THREAD_POOL);

            plugin.generateWorld(completableFuture.get());
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        }

        return Bukkit.getWorld(uuid.toString());
    }

    private SlimeWorld cloneWorld(UUID uuid) {
        try {
            return worldTemplate.clone(uuid.toString(), loader);
        } catch (WorldAlreadyExistsException | IOException e) {
            try {
                return SWMPlugin.getInstance().createEmptyWorld(loader, worldTemplate.getName(), false, new SlimePropertyMap());
            } catch (WorldAlreadyExistsException | IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }
}