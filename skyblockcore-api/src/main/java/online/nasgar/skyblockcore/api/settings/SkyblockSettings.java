package online.nasgar.skyblockcore.api.settings;

import com.github.imthenico.repositoryhelper.core.repository.Repository;
import com.github.imthenico.repositoryhelper.core.serialization.Adapter;
import com.github.imthenico.repositoryhelper.json.JsonDataProcessor;
import com.github.imthenico.repositoryhelper.sql.SQLDataProcessor;
import com.github.imthenico.repositoryhelper.sql.SQLRepository;
import com.github.imthenico.repositoryhelper.sql.SQLRepositoryData;
import com.github.imthenico.sqlutil.database.ConfigurationSerializableUserData;
import com.github.imthenico.sqlutil.database.UserData;
import com.github.imthenico.sqlutil.helper.SQLQueryHelper;
import com.zaxxer.hikari.HikariDataSource;
import online.nasgar.commons.configuration.Configuration;
import online.nasgar.commons.registry.Registry;
import online.nasgar.skyblockcore.api.Skyblock;
import online.nasgar.skyblockcore.api.model.SkyblockPlayerData;
import online.nasgar.skyblockcore.api.model.island.IslandData;
import online.nasgar.skyblockcore.api.util.MapSerializationWrapper;
import online.nasgar.skyblockcore.api.util.SQLTableCreator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SkyblockSettings {

    private String islandWorldTemplateName = "skyblock-is-template";
    private SQLQueryHelper sqlHandler;
    private final Configuration dataBaseFile;
    private Registry<String, SQLRepositoryData> repositoriesData;
    private Registry<String, Repository<?>> repositories;
    private final Skyblock skyblock;

    public SkyblockSettings(
            Configuration configuration,
            Skyblock skyblock
    ) {
        this.dataBaseFile = Objects.requireNonNull(configuration, "configuration");
        this.skyblock = Objects.requireNonNull(skyblock, "skyblock");
        UserData userData = userData(configuration);

        if (userData == null) {
            Bukkit.getPluginManager().disablePlugin(skyblock.getPlugin());
            throw new UnsupportedOperationException("no specified mysql credential in config file");
        }

        initConnection(userData);
        initRepositories();
    }

    public Configuration getDataBaseFile() {
        return dataBaseFile;
    }

    public SQLQueryHelper getSQLHandler() {
        return sqlHandler;
    }

    public String getIslandWorldTemplateName() {
        return islandWorldTemplateName;
    }

    public SQLRepositoryData getSQLRepositoryData(String id) {
        return repositoriesData.getById(id);
    }

    @SuppressWarnings("unchecked")
    public <T> Repository<T> getRepository(String id) {
        return (Repository<T>) repositories.getById(id);
    }

    private UserData userData(Configuration configuration) {
        ConfigurationSection configurationSection = configuration.getConfigurationSection("credential");

        if (configurationSection == null)
            return null;

        Map<String, Object> objectMap = configurationSection.getValues(true);

        return new ConfigurationSerializableUserData(objectMap);
    }

    private void initConnection(UserData userData) {
        try {
            HikariDataSource dataSource = (HikariDataSource) userData.getHikariDataSource();

            Connection connection = dataSource.getConnection();
            this.sqlHandler = new SQLQueryHelper(connection);

            SQLTableCreator sqlTableCreator = new SQLTableCreator(sqlHandler);

            sqlTableCreator.checkJsonTable("player_data");
            sqlTableCreator.checkJsonTable("island_data");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initRepositories() {
        this.repositoriesData = new Registry<>();
        this.repositories = new Registry<>();

        try {
            ConfigurationSection section = dataBaseFile.getConfigurationSection("repository-data");

            if (section == null)
                throw new UnsupportedOperationException("repository section is null");

            Set<String> keys = section.getKeys(false);

            for (String key : keys) {
                ConfigurationSection section1 = section.getConfigurationSection(key);
                SQLRepositoryData data = getFromSection(section1);

                if (data != null) {
                    repositoriesData.registerEntity(key, data);
                }
            }

            // essential repositories
            Adapter<?> adapter = skyblock.adapterRegistry().findAdapter("gson");

            repositories.registerEntity("player-data", new SQLRepository<>(
                    adapter,
                    new SQLDataProcessor<>(new JsonDataProcessor()),
                    SkyblockPlayerData.class,
                    getSQLRepositoryData("player-data"),
                    Skyblock.SKYBLOCK_THREAD_POOL
            ));

            repositories.registerEntity("island-data", new SQLRepository<>(
                    adapter,
                    new SQLDataProcessor<>(new JsonDataProcessor()),
                    IslandData.class,
                    getSQLRepositoryData("island-data"),
                    Skyblock.SKYBLOCK_THREAD_POOL
            ));

            repositories.registerEntity("configuration-fields", new SQLRepository<>(
                    adapter,
                    new SQLDataProcessor<>(new JsonDataProcessor()),
                    Object.class,
                    getSQLRepositoryData("configuration-fields"),
                    Skyblock.SKYBLOCK_THREAD_POOL
            ));

        } catch (Exception e) {
            Bukkit.getPluginManager().disablePlugin(skyblock.getPlugin());
            e.printStackTrace();
        }
    }

    private SQLRepositoryData getFromSection(ConfigurationSection configurationSection) {
        if (configurationSection != null)
            return MapSerializationWrapper.newRepositoryData(sqlHandler, configurationSection.getValues(true));

        return null;
    }
}