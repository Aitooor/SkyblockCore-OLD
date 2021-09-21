package online.nasgar.skyblockcore.api.util;

import com.github.imthenico.repositoryhelper.sql.SQLRepositoryData;
import com.github.imthenico.sqlutil.helper.SQLQueryHelper;

import java.util.Map;

public class MapSerializationWrapper {

    public static SQLRepositoryData newRepositoryData(SQLQueryHelper queryHelper, Map<String, Object> objectMap) {
        String tableName = (String) objectMap.get("table");
        String idColumnName = (String) objectMap.get("id-column-name");
        String valueColumnName = (String) objectMap.get("value-column-name");

        if (tableName == null || idColumnName == null || valueColumnName == null)
            return null;

        return new SQLRepositoryData(queryHelper, tableName, idColumnName, valueColumnName);
    }
}