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
    public static void main(String[] args) throws FtpException, ConfigException {
        ServerConfigLoader configLoader = new ServerConfigLoader();
        String configPath = args[0];
        ServerConfig config;
        try {
            config = configLoader.load(Paths.get(configPath));
        } catch (Exception e) {
            throw new ConfigException("loading config failed: " + configPath, e);
        }

        FileSystemFactoryFactory fileSystemFactoryFactory = new ReadOnlyFileSystemFactoryFactory();
        FileSystemFactory fileSystemFactory;
        try {
            fileSystemFactory = fileSystemFactoryFactory.create(config);
        } catch (AttributeException e) {
            throw new ConfigException("loading config failed: " + configPath, e);
        }

        FtpServerFactory ftpServerFactory = new FtpServerFactory();

        UserFactory userFactory = new UserFactory();
        userFactory.setName("anonymous");
        User anonymous = userFactory.createUser();

        UserManager userManager = ftpServerFactory.getUserManager();
        userManager.save(anonymous);

        ftpServerFactory.setFileSystem(fileSystemFactory);

        FtpServer server = ftpServerFactory.createServer();
        server.start();
    }
}
