package jp.unaguna.mappedftp.filesystem;

import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.ftplet.FileSystemFactory;

public interface ConfigurableFileSystemFactory extends FileSystemFactory {
    /**
     * Returns whether {@link #applyConfig(ServerConfig)} has already been called.
     */
    boolean isConfigured();

    void applyConfig(ServerConfig serverConfig) throws AttributeException;
}
