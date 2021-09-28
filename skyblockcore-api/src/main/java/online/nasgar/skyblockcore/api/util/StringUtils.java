package online.nasgar.skyblockcore.api.util;

import online.nasgar.commons.util.LocationModel;
import org.bukkit.ChatColor;

import java.text.NumberFormat;

public class StringUtils {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    static {
        NUMBER_FORMAT.setMaximumFractionDigits(3);
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String color(String text, Object... replaces) {
        return String.format(ChatColor.translateAlternateColorCodes('&', text), replaces);
    }

    public static String[] color(String[] text) {
        for (int i = 0; i < text.length; i++) {
            text[i] = color(text[i]);
        }

        return text;
    }

    public static String format(LocationModel model) {
        return String.format("%s, %s, %s, %s, %s",
                format(model.getX()),
                format(model.getY()),
                format(model.getZ()),
                format(model.getYaw()),
                format(model.getPitch())
        );
    }

    public static String format(double n) {
        return NUMBER_FORMAT.format(n);
    }

    public static String formatWithWorld(LocationModel model) {
        return String.format("%s, %s, %s, %s, %s, %s",
                format(model.getX()),
                format(model.getY()),
                format(model.getZ()),
                format(model.getYaw()),
                format(model.getPitch()),
                model.getWorldName()
        );
    }
}