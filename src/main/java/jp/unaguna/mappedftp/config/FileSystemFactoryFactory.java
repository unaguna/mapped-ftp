package jp.unaguna.mappedftp.config;

import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.ftplet.FileSystemFactory;

/**
 * A factory of {@link FileSystemFactory}.
 *
 * <p>
 *     This class must have a constructor with no arguments because an instance may be created by reflection.
 * </p>
 */
public abstract class FileSystemFactoryFactory {
    /**
     * A factory method of {@link FileSystemFactory}
     *
     * @param config configurations which is specified by a xml config file etc.
     * @return instance of FileSystemFactory
     * @throws AttributeException when attributes which is specified in the configuration are wrong
     */
    protected abstract FileSystemFactory createFileSystemFactory(ServerConfig config) throws AttributeException;

    /**
     * A factory method of {@link FileSystemFactory}
     *
     * @param config configurations which is specified by a xml config file etc.
     * @return instance of FileSystemFactory
     * @throws AttributeException when attributes which is specified in the configuration are wrong
     */
    public FileSystemFactory create(ServerConfig config) throws AttributeException {
        return createFileSystemFactory(config);
    }
}
