package jp.unaguna.mappedftp.filesystem;

import java.io.InputStream;
import java.io.OutputStream;

public class FileTreeItemReadOnlyFile implements FileTreeItem {
    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public OutputStream createOutputStream(long offset) {
        // TODO: throws exception
        return null;
    }

    @Override
    public InputStream createInputStream(long offset) {
        // perform as an empty file
        return new InputStream() {
            @Override
            public int read() {
                return -1;
            }
        };
    }
}
