package jp.unaguna.mappedftp;


import java.nio.file.Path;

public class TemporaryFile {
    private final Path path;

    public TemporaryFile(Path path) {
        this.path = path;
    }

    public Path toPath() {
        return path;
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
