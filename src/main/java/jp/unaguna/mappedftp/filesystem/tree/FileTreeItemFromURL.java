package jp.unaguna.mappedftp.filesystem.tree;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileTreeItemFromURL extends FileTreeItemReadOnlyFile {
    private final URL source;

    public FileTreeItemFromURL(URL source) {
        this.source = source;
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        URLConnection connection = source.openConnection();
        return connection.getInputStream();
    }
}
