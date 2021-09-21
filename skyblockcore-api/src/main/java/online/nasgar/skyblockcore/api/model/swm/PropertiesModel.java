package online.nasgar.skyblockcore.api.model.swm;

import com.grinderwolf.swm.api.world.properties.PropertyType;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimeProperty;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PropertiesModel implements ConfigurationSerializable {

    private final Map<String, Object> objectMap;

    public PropertiesModel(Map<String, Object> objectMap) {
        this.objectMap = Objects.requireNonNull(objectMap);

        // validate data
        newPropertyMap();
    }

    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>(objectMap);
    }

    public SlimePropertyMap newPropertyMap() {
        SlimePropertyMap propertyMap = new SlimePropertyMap();
        this.objectMap.forEach((k, v) -> set(propertyMap, k, v));

        return propertyMap;
    }

    private void set(SlimePropertyMap propertyMap, String nbtName, Object value) {
        PropertyType type = type(value);

        if (type == null || nbtName == null)
            return;

        SlimeProperty property = validate(nbtName);

        if (property == null)
            throw new IllegalArgumentException(String.join("invalid property name: %s", nbtName));

        switch (type) {
            case BOOLEAN:
                propertyMap.setBoolean(property, (Boolean) value);
                break;
            case INT:
                propertyMap.setInt(property, ((Number) value).intValue());
                break;
            case STRING:
                propertyMap.setString(property, (String) value);
        }
    }

    private PropertyType type(Object o) {
        if (o instanceof String)
            return PropertyType.STRING;
        else if (o instanceof Number)
            return PropertyType.INT;
        else if (o instanceof Boolean)
            return PropertyType.BOOLEAN;

        return null;
    }

    private SlimeProperty validate(String nbtName) {
        for (SlimeProperty value : SlimeProperties.VALUES) {
            if (nbtName.equals(value.getNbtName()))
                return value;
        }

        return null;
    }
}