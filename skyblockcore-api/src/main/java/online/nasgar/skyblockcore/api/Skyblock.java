package online.nasgar.skyblockcore.api;

import com.github.imthenico.repositoryhelper.core.serialization.registry.AdapterRegistry;
import online.nasgar.skyblockcore.api.manager.SkyblockPlayerManager;
import online.nasgar.skyblockcore.api.settings.SkyblockSettings;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public interface Skyblock {

    Executor SKYBLOCK_THREAD_POOL = Executors.newFixedThreadPool(5);

    SkyblockPlayerManager playerManager();

    AdapterRegistry adapterRegistry();

    JavaPlugin getPlugin();

    SkyblockSettings getSettings();
}