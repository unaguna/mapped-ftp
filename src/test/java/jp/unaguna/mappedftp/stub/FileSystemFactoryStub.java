package jp.unaguna.mappedftp.stub;

import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.filesystem.ConfigurableFileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.User;

public class FileSystemFactoryStub implements ConfigurableFileSystemFactory {
    private final FileSystemView fileSystemView;
    private ServerConfig appliedConfig = null;

    public FileSystemFactoryStub() {
        this.fileSystemView = new FileSystemViewStub();
    }

    public FileSystemView getFileSystemView() {
        return fileSystemView;
    }

    @Override
    public FileSystemView createFileSystemView(User user) {
        return null;
    }

    @Override
    public boolean isConfigured() {
        return appliedConfig != null;
    }

    @Override
    public void applyConfig(ServerConfig serverConfig) {
        appliedConfig = serverConfig;
    }
}
