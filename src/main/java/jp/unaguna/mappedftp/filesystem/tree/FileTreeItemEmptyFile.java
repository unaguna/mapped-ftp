package jp.unaguna.mappedftp.filesystem.tree;

import java.io.IOException;
import java.io.InputStream;

public class FileTreeItemEmptyFile extends FileTreeItemReadOnlyFile {
    @Override
    public boolean isRegularFile() {
        return true;
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        return new InputStream() {
            @Override
            public int read() {
                return -1;
            }
        };
    }
}
