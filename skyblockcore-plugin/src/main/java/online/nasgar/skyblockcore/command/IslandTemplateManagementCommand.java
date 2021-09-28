package online.nasgar.skyblockcore.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.Named;
import online.nasgar.commons.util.LocationModel;
import online.nasgar.skyblockcore.api.builder.IslandTemplateBuilder;
import online.nasgar.skyblockcore.api.helper.IslandTemplateBuilderHelper;
import online.nasgar.skyblockcore.api.util.StringUtils;
import org.bukkit.entity.Player;

import static online.nasgar.skyblockcore.api.util.StringUtils.*;
import java.util.Objects;

@Command(names = {"istc", "istemplatecreator", "istemplate"})
public class IslandTemplateManagementCommand implements CommandClass {

    private final IslandTemplateBuilderHelper islandTemplateBuilderHelper;

    public IslandTemplateManagementCommand(
            IslandTemplateBuilderHelper islandTemplateBuilderHelper
    ) {
        this.islandTemplateBuilderHelper = Objects.requireNonNull(islandTemplateBuilderHelper);
    }

    @Command(names = "create")
    public boolean createIslandTemplate(Player player, String name) {
        if (islandTemplateBuilderHelper.exists(name)) {
            player.sendMessage(color("&cIt already exists an template or builder with name '%s'", name));
        } else {
            islandTemplateBuilderHelper.newBuilder(name);
            player.sendMessage(color("&aNew template builder created with name %s", name));
        }
        return true;
    }

    @Command(names = "setweschematicname")
    public boolean setWESchematicName(Player player, @Named("name") String name, @Named("schematicName") String schematicName) {
        IslandTemplateBuilder islandTemplateBuilder = islandTemplateBuilderHelper.get(name);

        if (islandTemplateBuilder == null) {
            player.sendMessage(color("&cUnknown template builder %s", name));
        } else {
            islandTemplateBuilder.setWESchematicName(schematicName);
            player.sendMessage(color("&aSet schematicName(%s) successfully", schematicName));
        }

        return true;
    }

    @Command(names = "setworldmodelname")
    public boolean setWorldModelName(Player player, @Named("name") String name, @Named("worldModelName") String worldModelName) {
        IslandTemplateBuilder islandTemplateBuilder = islandTemplateBuilderHelper.get(name);

        if (islandTemplateBuilder == null) {
            player.sendMessage(color("&cUnknown template builder %s", name));
        } else {
            islandTemplateBuilder.setWESchematicName(worldModelName);
            player.sendMessage(color("&aSet worldModelName(%s) successfully", worldModelName));
        }

        return true;
    }

    @Command(names = "settemplatespawn")
    public boolean setIslandLocation(Player player, @Named("name") String name) {
        IslandTemplateBuilder islandTemplateBuilder = islandTemplateBuilderHelper.get(name);

        if (islandTemplateBuilder == null) {
            player.sendMessage(color("&cUnknown template builder %s", name));
        } else {
            LocationModel locationModel = LocationModel.of(player.getLocation());
            islandTemplateBuilder.setIslandSpawn(locationModel);

            player.sendMessage(color("&aSet islandSpawn(%s) successfully", StringUtils.format(locationModel)));
        }

        return true;
    }

    @Command(names = "buildtemplate")
    public boolean build(Player player, @Named("name") String name) {
        IslandTemplateBuilder islandTemplateBuilder = islandTemplateBuilderHelper.get(name);

        if (islandTemplateBuilder == null) {
            player.sendMessage(color("&cUnknown template builder %s", name));
        } else {
            if (islandTemplateBuilderHelper.register(name)) {
                player.sendMessage(color("&aSet schematicName(%s) successfully", name));
            } else {
                player.sendMessage(color("&aUnable to build island template (%s). Please check fields and try again later", name));
            }
        }

        return true;
    }

    @Command(names = "builderinfo")
    public boolean builderInfo(Player player, @Named("name") String name) {
        IslandTemplateBuilder islandTemplateBuilder = islandTemplateBuilderHelper.get(name);

        if (islandTemplateBuilder == null) {
            player.sendMessage(color("&cUnknown template builder %s", name));
        } else {
            player.sendMessage(color("&b" + islandTemplateBuilderHelper.info(name)));
        }

        return true;
    }
}