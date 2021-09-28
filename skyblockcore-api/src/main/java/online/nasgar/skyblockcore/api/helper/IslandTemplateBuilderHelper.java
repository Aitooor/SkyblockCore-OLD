package online.nasgar.skyblockcore.api.helper;

import online.nasgar.skyblockcore.api.builder.IslandTemplateBuilder;
import online.nasgar.skyblockcore.api.manager.IslandManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IslandTemplateBuilderHelper {

    private final static String TEMPLATE_INFO_FORMAT = "(name = '%s', islandSpawn = '%s', weSchematicName = '%s', worldModelName = '%s')";
    private final IslandManager<?> islandManager;
    private final Map<String, IslandTemplateBuilder> templates;

    public IslandTemplateBuilderHelper(IslandManager<?> islandManager) {
        this.islandManager = Objects.requireNonNull(islandManager);
        this.templates = new HashMap<>();
    }

    public IslandTemplateBuilder newBuilder(String name) {
        IslandTemplateBuilder builder = new IslandTemplateBuilder(name);

        templates.put(name, builder);

        return builder;
    }

    public IslandTemplateBuilder get(String name) {
        return templates.get(Objects.requireNonNull(name));
    }

    public boolean exists(String name) {
        return templates.containsKey(name) || islandManager.getTemplate(name) != null;
    }

    public boolean register(String name) {
        try {
            IslandTemplateBuilder builder = get(name);

            if (builder != null) {
                islandManager.registerTemplate(builder.build());
            }
        } catch (Exception e) {

        }

        return false;
    }

    public String info(String name) {
        IslandTemplateBuilder builder = get(name);
        if (builder == null)
            return "";

        return String.format(TEMPLATE_INFO_FORMAT, name, builder.getIslandSpawn(), builder.getWESchematicName(), builder.getWorldModelName());
    }
}