package jp.unaguna.mappedftp.user;

import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;

import java.io.File;
import java.nio.file.Path;

public class ConfigurablePropertiesUserManagerFactory implements ConfigurableUserManagerFactory {
    private boolean isConfigured = false;

    private Path userPropertiesPath = null;

    @Override
    public boolean isConfigured() {
        return isConfigured;
    }

    @Override
    public void applyConfig(ServerConfig serverConfig) throws AttributeException {
        this.userPropertiesPath = serverConfig.getUserPropertiesPath();

        this.isConfigured = true;
    }

    @Override
    public UserManager createUserManager() {
        final PasswordEncryptor passwordEncryptor = new Md5PasswordEncryptor();
        final String adminName = "admin";

        final File userPropertiesFile = userPropertiesPath != null
                ? userPropertiesPath.toFile()
                : null;

        return new PropertiesUserManager(passwordEncryptor, userPropertiesFile, adminName);
    }
}
