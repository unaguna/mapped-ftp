package jp.unaguna.mappedftp.filesystem;

import java.io.InputStream;
import java.io.OutputStream;

public class FileTreeItemDirectory implements FileTreeItem {
    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public OutputStream createOutputStream(long offset) {
        return null;
    }

    @Override
    public InputStream createInputStream(long offset) {
        return null;
    }
}
