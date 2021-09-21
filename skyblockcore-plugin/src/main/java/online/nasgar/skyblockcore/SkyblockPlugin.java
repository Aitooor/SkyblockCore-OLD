package online.nasgar.skyblockcore;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.plugin.SWMPlugin;
import online.nasgar.commons.configuration.Configuration;
import online.nasgar.skyblockcore.api.Skyblock;
import online.nasgar.skyblockcore.api.loader.IslandLoader;
import online.nasgar.skyblockcore.api.manager.SimpleSkyblockPlayerManager;
import online.nasgar.skyblockcore.api.manager.SkyblockPlayerManager;
import online.nasgar.skyblockcore.api.settings.SkyblockSettings;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SkyblockPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Configuration mainConfig = new Configuration(this, new File(folder(), "mysql-credential.yml"));

        Skyblock skyblock = new SkyblockService(mainConfig, this);

    }

    public File folder() {
        File folder = getDataFolder();

        if (!folder.exists())
            folder.mkdirs();

        return folder;
    }
}