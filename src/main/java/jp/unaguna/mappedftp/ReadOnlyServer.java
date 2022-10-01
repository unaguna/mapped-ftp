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

import java.nio.file.Paths;

public class ReadOnlyServer {
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
        FileSystemFactoryFactory fileSystemFactoryFactory = new ReadOnlyFileSystemFactoryFactory();
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
