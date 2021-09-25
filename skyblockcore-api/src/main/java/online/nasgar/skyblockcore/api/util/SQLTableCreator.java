package online.nasgar.skyblockcore.api.util;

import com.github.imthenico.sqlutil.helper.SQLQueryHelper;

public class SQLTableCreator {

    private final SQLQueryHelper queryHelper;

    public SQLTableCreator(SQLQueryHelper queryHelper) {
        this.queryHelper = queryHelper;
    }

    public void checkJsonTable(String tableName) {
        String query = String.format("CREATE TABLE IF NOT EXISTS %s(" +
                "UUID VARCHAR(255)," +
                "JSON LONGTEXT)", tableName);

        queryHelper.executeUpdate(query);
    }
}