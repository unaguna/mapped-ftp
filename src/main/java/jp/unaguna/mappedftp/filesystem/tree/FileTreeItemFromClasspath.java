package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.utils.ClasspathUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.MissingResourceException;

public class FileTreeItemFromClasspath extends FileTreeItemReadOnlyFile {
    private final String source;
    private URL sourceUrl = null;

    public FileTreeItemFromClasspath(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public URL getSourceUrl() {
        if (sourceUrl == null) {
            sourceUrl = ClasspathUtils.getResource(source);
            if (sourceUrl == null) {
                throw new MissingResourceException("no such resource: " + source, null, null);
            }
        }
        return sourceUrl;
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        URLConnection connection = getSourceUrl().openConnection();
        return connection.getInputStream();
    }
}
