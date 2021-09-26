package online.nasgar.skyblockcore.api.util;

import org.bukkit.Bukkit;

import java.util.function.Function;
import java.util.logging.Level;

public class Exceptions {

    public static <T> Function<Throwable, ? extends T> bukkitDebug() {
        return (throwable) -> {
            Bukkit.getLogger().log(Level.SEVERE, "An unexpected exception has occurred", throwable);
            return null;};
    }
}