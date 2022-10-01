package jp.unaguna.mappedftp.config;

import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.ftplet.FileSystemFactory;

public interface ConfigurableFileSystemFactory extends FileSystemFactory {
    void applyConfig(ServerConfig serverConfig) throws AttributeException;
}
