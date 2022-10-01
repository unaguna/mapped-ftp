package jp.unaguna.mappedftp;

import jp.unaguna.mappedftp.config.ConfigException;
import jp.unaguna.mappedftp.config.FileSystemFactoryFactory;
import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.config.ServerConfigLoader;
import jp.unaguna.mappedftp.filesystem.ReadOnlyFileSystemFactoryFactory;
import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UserFactory;

import java.lang.reflect.Constructor;
import java.nio.file.Paths;

public class ReadOnlyServer {
    private static final Class<? extends FileSystemFactoryFactory> DEFAULT_FILESYSTEM_FACTORY_FACTORY =
            ReadOnlyFileSystemFactoryFactory.class;
    private FtpServer ftpServer = null;
    private ServerConfig serverConfig = null;

    public boolean isStarted() {
        return ftpServer != null && !ftpServer.isStopped();
    }

    /**
     * set a server configuration
     *
     * @param serverConfig the configurations to be set
     * @throws IllegalStateException if the server has been started
     */
    public void setConfig(ServerConfig serverConfig) {
        if (this.isStarted()) {
            throw new IllegalStateException("Configuration cannot be changed after the server has been started.");
        }
        this.serverConfig = serverConfig;
    }

    public void start() throws FtpException, ConfigException {
        final FileSystemFactoryFactory fileSystemFactoryFactory = constructFileSystemFactoryFactory(serverConfig);

        FileSystemFactory fileSystemFactory;
        try {
            fileSystemFactory = fileSystemFactoryFactory.create(serverConfig);
        } catch (AttributeException e) {
            throw new ConfigException("loading config failed: " + serverConfig.getConfigIdentifier(), e);
        }

        FtpServerFactory ftpServerFactory = new FtpServerFactory();

        UserFactory userFactory = new UserFactory();
        userFactory.setName("anonymous");
        User anonymous = userFactory.createUser();

        UserManager userManager = ftpServerFactory.getUserManager();
        userManager.save(anonymous);

        ftpServerFactory.setFileSystem(fileSystemFactory);

        this.ftpServer = ftpServerFactory.createServer();
        this.ftpServer.start();
    }

    /**
     * Create a {@link FileSystemFactoryFactory} according to the configurations and return it.
     *
     * <p>
     *     It creates an instance of the {@link FileSystemFactoryFactory} specified in the configuration.
     *     If not specified, creates an instance of the default class.
     * </p>
     *
     * @param serverConfig the configurations
     * @return constructed instance
     * @throws ConfigException when it failed to construct FileSystemFactoryFactory specified in configurations
     */
    private static FileSystemFactoryFactory constructFileSystemFactoryFactory(ServerConfig serverConfig)
            throws ConfigException {

        if (serverConfig.getFileSystemFactoryFactoryClass() != null) {
            final Class<? extends FileSystemFactoryFactory> cls = serverConfig.getFileSystemFactoryFactoryClass();
            try {
                return constructFileSystemFactoryFactory(cls);
            } catch (ReflectiveOperationException e) {
                throw new ConfigException("failed to construct " + cls, e);
            }
        } else {
            // if factory class is not specified, use the default class
            try {
                return constructFileSystemFactoryFactory(DEFAULT_FILESYSTEM_FACTORY_FACTORY);
            } catch (ReflectiveOperationException e) {
                // It is not a ConfigException because the used class was not configured one.
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Create a {@link FileSystemFactoryFactory} instance
     *
     * @param cls the class
     * @return constructed instance
     * @throws ReflectiveOperationException when it failed to construct FileSystemFactoryFactory
     */
    private static FileSystemFactoryFactory constructFileSystemFactoryFactory(
            Class<? extends FileSystemFactoryFactory> cls
    ) throws ReflectiveOperationException {
        final Constructor<? extends FileSystemFactoryFactory> constructor = cls.getConstructor();
        return constructor.newInstance();
    }

    public static void main(String[] args) throws FtpException, ConfigException {
        ServerConfigLoader configLoader = new ServerConfigLoader();
        String configPath = args[0];
        ServerConfig config;
        try {
            config = configLoader.load(Paths.get(configPath));
        } catch (Exception e) {
            throw new ConfigException("loading config failed: " + configPath, e);
        }

        ReadOnlyServer server = new ReadOnlyServer();
        server.setConfig(config);
        server.start();
    }
}
