package jp.unaguna.mappedftp.map;

import java.util.HashMap;

public class AttributeHashMap extends HashMap<String, String> {
    public AttributeHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public AttributeHashMap() {
        super();
    }

    /**
     * get value of the specified attribute
     *
     * @param name the name of the attribute whose value is to be returned
     * @return the value of the specified attribute
     * @throws AttributeMissingException if the key is not contained
     */
    public String get(String name) throws AttributeMissingException {
        if (!this.containsKey(name)) {
            throw new AttributeMissingException(name);
        }
        return super.get(name);
    }

    /**
     * get and remove value to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped
     * @throws AttributeMissingException if the key is not contained
     */
    public String pop(String key) throws AttributeMissingException {
        if (!this.containsKey(key)) {
            throw new AttributeMissingException(key);
        }
        return super.remove(key);
    }

    /**
     * Returns and removes the value of the specified attribute, or defaultValue if this map contains no value for the attribute.
     *
     * @param name         the name of the attribute whose value is to be returned
     * @param defaultValue the value to be returned when this map does not contain the specified attribute
     * @return the value of the specified attribute, or defaultValue if this map contains no value for the attribute
     */
    public String popOrDefault(String name, String defaultValue) {
        if (!this.containsKey(name)) {
            return defaultValue;
        }
        return super.remove(name);
    }
}
