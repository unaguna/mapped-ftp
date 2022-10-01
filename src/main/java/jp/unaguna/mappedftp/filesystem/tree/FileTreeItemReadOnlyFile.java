package jp.unaguna.mappedftp.filesystem.tree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileTreeItemReadOnlyFile implements FileTreeItem {
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
    public abstract InputStream createInputStream(long offset) throws IOException;
}
