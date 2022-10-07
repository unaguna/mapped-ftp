package jp.unaguna.mappedftp;

import jp.unaguna.mappedftp.config.ConfigException;
import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.config.ServerConfigLoader;
import jp.unaguna.mappedftp.filesystem.ConfigurableFileSystemFactory;
import jp.unaguna.mappedftp.filesystem.ReadOnlyFileSystemFactory;
import jp.unaguna.mappedftp.utils.ClasspathUtils;
import jp.unaguna.mappedftp.map.AttributeException;
import jp.unaguna.mappedftp.user.ConfigurablePropertiesUserManagerFactory;
import jp.unaguna.mappedftp.user.ConfigurableUserManagerFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.file.Paths;

public class MappedFtpServer {
    private static final Class<? extends ConfigurableFileSystemFactory> DEFAULT_FILESYSTEM_FACTORY_FACTORY =
            ReadOnlyFileSystemFactory.class;
    private static final Class<? extends ConfigurableUserManagerFactory> DEFAULT_USER_MANAGER_FACTORY =
            ConfigurablePropertiesUserManagerFactory.class;

    private static final Logger LOG = LoggerFactory.getLogger(MappedFtpServer.class.getName());

    private FtpServerFactory ftpServerFactory = null;
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

    /**
     * specify FtpServerFactory
     *
     * <p>
     *     If not specified, default factory is used.
     * </p>
     * @param ftpServerFactory
     */
    public void setFtpServerFactory(FtpServerFactory ftpServerFactory) {
        this.ftpServerFactory = ftpServerFactory;
    }

    public void start() throws FtpException, ConfigException {
        final ConfigurableFileSystemFactory fileSystemFactory = constructFileSystemFactory(serverConfig);
        final ConfigurableUserManagerFactory userManagerFactory = constructUserManagerFactory(serverConfig);

        try {
            fileSystemFactory.applyConfig(serverConfig);
            userManagerFactory.applyConfig(serverConfig);
        } catch (AttributeException e) {
            throw new ConfigException("loading config failed: " + serverConfigName, e);
        }

        if (ftpServerFactory == null) {
            ftpServerFactory = new FtpServerFactory();
        }

        UserManager userManager = userManagerFactory.createUserManager();
        ftpServerFactory.setUserManager(userManager);

        ftpServerFactory.setFileSystem(fileSystemFactory);

        this.ftpServer = ftpServerFactory.createServer();
        this.ftpServer.start();
    }

    /**
     * Create a {@link ConfigurableUserManagerFactory} according to the configurations and return it.
     *
     * <p>
     *     It creates an instance of the {@link ConfigurableUserManagerFactory} specified in the configuration.
     *     If not specified, creates an instance of the default class.
     * </p>
     *
     * @param serverConfig the configurations
     * @return constructed instance
     * @throws ConfigException when it failed to construct ConfigurableUserManagerFactory specified in configurations
     */
    private static ConfigurableUserManagerFactory constructUserManagerFactory(ServerConfig serverConfig)
            throws ConfigException {

        if (serverConfig.getUserManagerFactoryClass() != null) {
            final Class<? extends ConfigurableUserManagerFactory> cls = serverConfig.getUserManagerFactoryClass();
            try {
                return constructUserManagerFactory(cls);
            } catch (ReflectiveOperationException e) {
                throw new ConfigException("failed to construct " + cls, e);
            }
        } else {
            // if factory class is not specified, use the default class
            try {
                return constructUserManagerFactory(DEFAULT_USER_MANAGER_FACTORY);
            } catch (ReflectiveOperationException e) {
                // It is not a ConfigException because the used class was not configured one.
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Create a {@link ConfigurableUserManagerFactory} instance
     *
     * @param cls the class
     * @return constructed instance
     * @throws ReflectiveOperationException when it failed to construct ConfigurableUserManagerFactory
     */
    private static ConfigurableUserManagerFactory constructUserManagerFactory(
            Class<? extends ConfigurableUserManagerFactory> cls
    ) throws ReflectiveOperationException {
        final Constructor<? extends ConfigurableUserManagerFactory> constructor = cls.getConstructor();
        return constructor.newInstance();
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
        final ServerConfigLoader configLoader = new ServerConfigLoader();

        final String configPath;
        final ServerConfig config;
        if (args.length == 0) {
            final URL defaultConfigUrl = ClasspathUtils.getResource("jp.unaguna.mappedftp/default_config.xml");
            if (defaultConfigUrl == null) {
                throw new RuntimeException("The default configuration file is not found.");
            }

            LOG.info("Load default configuration file: " + defaultConfigUrl);
            configPath = defaultConfigUrl.toString();
            try {
                config = configLoader.load(defaultConfigUrl);
            } catch (Exception e) {
                throw new ConfigException("loading default config failed", e);
            }
        } else {
            configPath = args[0];
            LOG.info("Load configuration file: " + args[0]);
            try {
                config = configLoader.load(Paths.get(configPath));
            } catch (Exception e) {
                throw new ConfigException("loading config failed: " + configPath, e);
            }
        }

        MappedFtpServer server = new MappedFtpServer();
        server.setConfig(config, configPath);
        server.start();
    }
}
