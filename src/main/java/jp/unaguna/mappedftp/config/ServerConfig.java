package jp.unaguna.mappedftp.config;

import jp.unaguna.mappedftp.map.AttributeHashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerConfig {
    private final List<AttributeHashMap> files = new ArrayList<>();

    public void putFile(AttributeHashMap attributes) {
        files.add(attributes);
    }

    public Iterator<AttributeHashMap> getFilesIterator() {
        return files.iterator();
    }
}
