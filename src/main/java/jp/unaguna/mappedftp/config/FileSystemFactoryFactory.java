package jp.unaguna.mappedftp.config;

import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.ftplet.FileSystemFactory;

public abstract class FileSystemFactoryFactory {
    protected abstract FileSystemFactory createFileSystemFactory(ServerConfig config) throws AttributeException;

    public FileSystemFactory create(ServerConfig config) throws AttributeException {
        return createFileSystemFactory(config);
    }
}
