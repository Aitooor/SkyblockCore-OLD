package online.nasgar.skyblockcore;

import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import online.nasgar.commons.configuration.Configuration;
import online.nasgar.skyblockcore.api.Skyblock;
import online.nasgar.skyblockcore.api.helper.IslandTemplateBuilderHelper;
import online.nasgar.skyblockcore.api.manager.SkyblockPlayerManager;
import online.nasgar.skyblockcore.command.CommandRegister;
import online.nasgar.skyblockcore.command.IslandTemplateManagementCommand;
import online.nasgar.skyblockcore.command.IslandCommand;
import online.nasgar.skyblockcore.command.SkyblockCommandModule;
import online.nasgar.skyblockcore.fetcher.ServiceFetcher;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SkyblockPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Configuration mainConfig = new Configuration(this, new File(folder(), "mysql-credential.yml"));

        // async SkyblockService registry
        CompletableFuture.runAsync(() -> {
                    ServicesManager servicesManager = Bukkit.getServicesManager();

                    servicesManager.register(Skyblock.class, new SkyblockService(mainConfig, this), this, ServicePriority.Lowest);
                    Bukkit.getLogger().log(Level.INFO, "Registered successfully Skyblock provider (SkyblockService)");
                }, Skyblock.SKYBLOCK_THREAD_POOL)
                .exceptionally((exc) -> {
                    Bukkit.getLogger().log(Level.SEVERE, "An exception has occurred registering Skyblock provider in async mode", exc);
                    return null;
                });

        ServiceFetcher<Skyblock, SkyblockService> serviceFetcher = new ServiceFetcher<>(Skyblock.class, SkyblockService.class, 5);

        serviceFetcher.onFound((service) -> {
                    PartInjector partInjector = PartInjector.create();
                    partInjector.install(new DefaultsModule());
                    partInjector.install(new BukkitModule());
                    partInjector.install(new SkyblockCommandModule(service.playerManager()));

                    CommandRegister commandRegister = new CommandRegister(new AnnotatedCommandTreeBuilderImpl(partInjector), new BukkitCommandManager("skyblock"));

                    commandRegister.registerCommandClass(new IslandCommand(service.playerManager()));
                    commandRegister.registerCommandClass(new IslandTemplateManagementCommand(new IslandTemplateBuilderHelper(
                            service.playerManager()
                    )));
                })
                .onFail(() -> Bukkit.getPluginManager().disablePlugin(this));

        serviceFetcher.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        ServicesManager servicesManager = Bukkit.getServicesManager();

        RegisteredServiceProvider<Skyblock> serviceProvider = servicesManager.getRegistration(Skyblock.class);

        if (serviceProvider != null) {
            SkyblockPlayerManager playerManager = serviceProvider.getProvider().playerManager();

            playerManager.savePlayers();
            playerManager.saveIslands();
        }
    }

    public File folder() {
        File folder = getDataFolder();

        if (!folder.exists())
            folder.mkdirs();

        return folder;
    }
}