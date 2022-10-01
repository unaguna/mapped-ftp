package jp.unaguna.mappedftp;

import jp.unaguna.mappedftp.config.ConfigException;
import jp.unaguna.mappedftp.config.ConfigurableFileSystemFactory;
import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.config.ServerConfigLoader;
import jp.unaguna.mappedftp.filesystem.ReadOnlyFileSystemFactory;
import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UserFactory;

import java.lang.reflect.Constructor;
import java.nio.file.Paths;

public class MappedFtpServer {
    private static final Class<? extends ConfigurableFileSystemFactory> DEFAULT_FILESYSTEM_FACTORY_FACTORY =
            ReadOnlyFileSystemFactory.class;
    private FtpServer ftpServer = null;
    private ServerConfig serverConfig = null;
    private String serverConfigName = null;

    public boolean isStarted() {
        return ftpServer != null && !ftpServer.isStopped();
    }

    /**
     * set a server configuration
     *
     * @param serverConfig the configurations to be set
     * @param serverConfigName the name of configurations (this is used in error messages)
     * @throws IllegalStateException if the server has been started
     */
    public void setConfig(ServerConfig serverConfig, String serverConfigName) {
        if (this.isStarted()) {
            throw new IllegalStateException("Configuration cannot be changed after the server has been started.");
        }
        this.serverConfig = serverConfig;
        this.serverConfigName = serverConfigName;
    }

    public void start() throws FtpException, ConfigException {
        final ConfigurableFileSystemFactory fileSystemFactory = constructFileSystemFactory(serverConfig);

        try {
            fileSystemFactory.applyConfig(serverConfig);
        } catch (AttributeException e) {
            throw new ConfigException("loading config failed: " + serverConfigName, e);
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
     * Create a {@link ConfigurableFileSystemFactory} according to the configurations and return it.
     *
     * <p>
     *     It creates an instance of the {@link ConfigurableFileSystemFactory} specified in the configuration.
     *     If not specified, creates an instance of the default class.
     * </p>
     *
     * @param serverConfig the configurations
     * @return constructed instance
     * @throws ConfigException when it failed to construct ConfigurableFileSystemFactory specified in configurations
     */
    private static ConfigurableFileSystemFactory constructFileSystemFactory(ServerConfig serverConfig)
            throws ConfigException {

        if (serverConfig.getFileSystemFactoryClass() != null) {
            final Class<? extends ConfigurableFileSystemFactory> cls = serverConfig.getFileSystemFactoryClass();
            try {
                return constructFileSystemFactory(cls);
            } catch (ReflectiveOperationException e) {
                throw new ConfigException("failed to construct " + cls, e);
            }
        } else {
            // if factory class is not specified, use the default class
            try {
                return constructFileSystemFactory(DEFAULT_FILESYSTEM_FACTORY_FACTORY);
            } catch (ReflectiveOperationException e) {
                // It is not a ConfigException because the used class was not configured one.
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Create a {@link ConfigurableFileSystemFactory} instance
     *
     * @param cls the class
     * @return constructed instance
     * @throws ReflectiveOperationException when it failed to construct ConfigurableFileSystemFactory
     */
    private static ConfigurableFileSystemFactory constructFileSystemFactory(
            Class<? extends ConfigurableFileSystemFactory> cls
    ) throws ReflectiveOperationException {
        final Constructor<? extends ConfigurableFileSystemFactory> constructor = cls.getConstructor();
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

        MappedFtpServer server = new MappedFtpServer();
        server.setConfig(config, configPath);
        server.start();
    }
}
