package jp.unaguna.mappedftp.user;

import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.encrypt.PasswordEncryptorType;
import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurablePropertiesUserManagerFactory implements ConfigurableUserManagerFactory {
    private boolean isConfigured = false;

    private Path userPropertiesPath = null;
    private PasswordEncryptorType passwordEncryptorType = null;

    @Override
    public boolean isConfigured() {
        return isConfigured;
    }

    @Override
    public void applyConfig(ServerConfig serverConfig) throws AttributeException {
        this.userPropertiesPath = serverConfig.getUserPropertiesPath() != null
                ? Paths.get(serverConfig.getUserPropertiesPath())
                : null;
        this.passwordEncryptorType = serverConfig.getEncryptPasswords();

        this.isConfigured = true;
    }

    @Override
    public UserManager createUserManager() {
        final PasswordEncryptor passwordEncryptor = constructPasswordEncryptor(passwordEncryptorType);
        final String adminName = "admin";

        final File userPropertiesFile = userPropertiesPath != null
                ? userPropertiesPath.toFile()
                : null;

        return new PropertiesUserManager(passwordEncryptor, userPropertiesFile, adminName);
    }

    private static PasswordEncryptor constructPasswordEncryptor(PasswordEncryptorType type) {
        if (type != null) {
            return type.constructPasswordEncryptor();

        } else {
            // default PasswordEncryptor
            return new Md5PasswordEncryptor();
        }
    }
}
