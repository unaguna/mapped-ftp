package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.utils.ClasspathUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileTreeItemFromClasspath extends FileTreeItemReadOnlyFile {
    private final String source;
    private URL sourceUrl = null;

    public FileTreeItemFromClasspath(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public URL getSourceUrl() throws FileNotFoundException {
        if (sourceUrl == null) {
            sourceUrl = ClasspathUtils.getResource(source);
            if (sourceUrl == null) {
                throw new FileNotFoundException("no such resource: " + source);
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
