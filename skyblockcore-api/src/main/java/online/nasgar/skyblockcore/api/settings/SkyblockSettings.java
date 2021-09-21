package online.nasgar.skyblockcore.api.settings;

import com.github.imthenico.repositoryhelper.core.repository.Repository;
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
import online.nasgar.skyblockcore.api.model.SkyblockPlayer;
import online.nasgar.skyblockcore.api.model.island.Island;
import online.nasgar.skyblockcore.api.util.MapSerializationWrapper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.SQLException;

import java.util.Objects;

public class SkyblockSettings {

    private String islandWorldTemplateName;
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
        this.skyblock = Objects.requireNonNull(skyblock, "plugin");
        UserData userData = userData(configuration);

        if (userData == null) {
            Bukkit.getPluginManager().disablePlugin(skyblock.getPlugin());
            throw new UnsupportedOperationException("no specified mysql credential in config file");
        }

        HikariDataSource dataSource = (HikariDataSource) userData.getHikariDataSource();

        try {
            this.sqlHandler = new SQLQueryHelper(dataSource.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
        ConfigurationSection configurationSection = configuration.getConfigurationSection("database");

        if (configurationSection == null)
            return null;

        return new ConfigurationSerializableUserData(configuration.getValues(true));
    }

    private void initRepositories() {
        this.repositoriesData = new Registry<>();
        this.repositories = new Registry<>();

        try {
            for (String key : dataBaseFile.getConfigurationSection("repository-data").getKeys(false)) {
                SQLRepositoryData data = getFromSection(key);

                if (data != null) {
                    repositoriesData.registerEntity(key, data);
                }
            }

            // essential repositories
            repositories.registerEntity("player-data", new SQLRepository<>(
                    skyblock.serializationRegistry(),
                    new SQLDataProcessor<>(new JsonDataProcessor()),
                    SkyblockPlayer.class,
                    getSQLRepositoryData("player-data"))
            );

            repositories.registerEntity("island-data", new SQLRepository<>(
                    skyblock.serializationRegistry(),
                    new SQLDataProcessor<>(new JsonDataProcessor()),
                    Island.class,
                    getSQLRepositoryData("island-data")
            ));

            repositories.registerEntity("configuration-fields", new SQLRepository<>(
                    skyblock.serializationRegistry(),
                    new SQLDataProcessor<>(new JsonDataProcessor()),
                    Object.class,
                    getSQLRepositoryData("configuration-fields")
            ));

        } catch (Exception e) {
            Bukkit.getPluginManager().disablePlugin(skyblock.getPlugin());
            e.printStackTrace();
        }
    }

    private SQLRepositoryData getFromSection(String section) {
        ConfigurationSection configurationSection = dataBaseFile.getConfigurationSection(section);

        if (configurationSection != null)
            return MapSerializationWrapper.newRepositoryData(sqlHandler, configurationSection.getValues(true));

        return null;
    }
}