package jp.unaguna.mappedftp.filesystem.tree;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileTreeItemFromLocalFile extends FileTreeItemReadOnlyFile {
    private final Path source;

    public FileTreeItemFromLocalFile(Path source) {
        this.source = source;
    }

    public Path getSource() {
        return source;
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        return Files.newInputStream(source);
    }
}
