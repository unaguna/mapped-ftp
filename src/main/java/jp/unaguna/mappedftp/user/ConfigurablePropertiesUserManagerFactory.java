package jp.unaguna.mappedftp.user;

import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.dataclass.Either;
import jp.unaguna.mappedftp.encrypt.PasswordEncryptorType;
import jp.unaguna.mappedftp.map.AttributeException;
import jp.unaguna.mappedftp.map.IllegalAttributeException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class ConfigurablePropertiesUserManagerFactory implements ConfigurableUserManagerFactory {
    private final Logger logger = LoggerFactory.getLogger(ConfigurablePropertiesUserManagerFactory.class.getName());
    private boolean isConfigured = false;

    private FileOrUrl userPropertiesFile = null;
    private PasswordEncryptorType passwordEncryptorType = null;

    @Override
    public boolean isConfigured() {
        return isConfigured;
    }

    @Override
    public void applyConfig(ServerConfig serverConfig) throws AttributeException {
        this.userPropertiesFile = convertUserPropertiesPath(serverConfig.getUserPropertiesPath());
        this.passwordEncryptorType = serverConfig.getEncryptPasswords();

        this.isConfigured = true;
    }

    private FileOrUrl convertUserPropertiesPath(String userPropertiesPath) throws AttributeException {
        if (userPropertiesPath == null) {
            return null;
        }

        final File userPropertiesFile = new File(userPropertiesPath);
        if (userPropertiesFile.exists()) {
            return FileOrUrl.of(userPropertiesFile);

        } else {
            final URL userPropertiesURL = ConfigurablePropertiesUserManagerFactory.class.getResource(
                    userPropertiesPath);

            if (userPropertiesURL == null) {
                throw new IllegalAttributeException("no such file is found: " + userPropertiesPath);
            } else {
                return FileOrUrl.of(userPropertiesURL);
            }
        }
    }

    @Override
    public UserManager createUserManager() {
        final PasswordEncryptor passwordEncryptor = constructPasswordEncryptor(passwordEncryptorType);
        final String adminName = "admin";

        if (userPropertiesFile == null) {
            // TODO: デフォルトのユーザ設定を使用する
            return new PropertiesUserManager(passwordEncryptor, (File) null, adminName);

        } else {
            logger.info("Create a user manager from " + userPropertiesFile);
            return userPropertiesFile.map(
                    file -> new PropertiesUserManager(passwordEncryptor, file, adminName),
                    url -> new PropertiesUserManager(passwordEncryptor, url, adminName));
        }
    }

    private static PasswordEncryptor constructPasswordEncryptor(PasswordEncryptorType type) {
        if (type != null) {
            return type.constructPasswordEncryptor();

        } else {
            // default PasswordEncryptor
            return new Md5PasswordEncryptor();
        }
    }

    private static class FileOrUrl extends Either<File, URL> {
        private FileOrUrl(File file, URL url) {
            super(file, url);
        }

        public static FileOrUrl of(File file) {
            return new FileOrUrl(file, null);
        }

        public static FileOrUrl of(URL url) {
            return new FileOrUrl(null, url);
        }
    }
}
