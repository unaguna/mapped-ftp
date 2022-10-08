package jp.unaguna.mappedftp.user;

import jp.unaguna.mappedftp.TestUtils;
import jp.unaguna.mappedftp.config.ConfigException;
import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.encrypt.PasswordEncryptorType;
import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurablePropertiesUserManagerFactoryTest {
    @Test
    public void testConfigured__true() {
        final ServerConfig serverConfig = new ServerConfig();

        final ConfigurablePropertiesUserManagerFactory factory = new ConfigurablePropertiesUserManagerFactory();
        try {
            factory.applyConfig(serverConfig);
        } catch (AttributeException | ConfigException e) {
            fail(e);
            return;
        }

        assertTrue(factory.isConfigured());
    }

    @Test
    public void testConfigured__false() {
        final ConfigurablePropertiesUserManagerFactory factory = new ConfigurablePropertiesUserManagerFactory();

        assertFalse(factory.isConfigured());
    }

    @Test
    public void testCreate__user_properties_path(TestInfo testInfo) {
        String userPropertiesPath = TestUtils.getInputResourceClasspath("user.properties", testInfo);

        final ServerConfig serverConfig = new ServerConfig(){{
            setUserPropertiesPath(userPropertiesPath);
        }};

        final ConfigurablePropertiesUserManagerFactory factory = new ConfigurablePropertiesUserManagerFactory();
        final PropertiesUserManager userManager;
        try {
            factory.applyConfig(serverConfig);
            assertTrue(factory.isConfigured());

            userManager = (PropertiesUserManager) factory.createUserManager();
        } catch (AttributeException | ConfigException e) {
            fail(e);
            return;
        }

        String[] userNameList = userManager.getAllUserNames();
        User adminUser = userManager.getUserByName("admin");

        // assert getAllUserNames()
        assertEquals(1, userNameList.length);
        assertEquals("admin", userNameList[0]);

        // assert getUserByName()
        assertEquals("admin", adminUser.getName());
        assertEquals("/ftproot", adminUser.getHomeDirectory());
    }

    @Test
    public void testCreate__user_properties_path_not_specified() {
        final ServerConfig serverConfig = new ServerConfig();

        final ConfigurablePropertiesUserManagerFactory factory = new ConfigurablePropertiesUserManagerFactory();
        final PropertiesUserManager userManager;
        try {
            factory.applyConfig(serverConfig);
            assertTrue(factory.isConfigured());

            userManager = (PropertiesUserManager) factory.createUserManager();
        } catch (AttributeException | ConfigException e) {
            fail(e);
            return;
        }

        String[] userNameList = userManager.getAllUserNames();

        // assert that no user exists
        assertEquals(0, userNameList.length);
    }

    /**
     * <p>
     *     Allow "clear", "md5", and "salted" to be specified as password encryptors
     *     according to the Apache FTP server specification.
     *     (<a href="https://mina.apache.org/ftpserver-project/configuration_user_manager_file.html">reference</a>)
     * </p>
     */
    @ParameterizedTest
    @MethodSource("parameters__testCreate__encryptPasswords")
    public void testCreate__encryptPasswords(Class<? extends PasswordEncryptor> encryptPasswords, Class<? extends PasswordEncryptor> expectedEncryptPasswords) {
        final ServerConfig serverConfig = new ServerConfig(){{
            setPasswordEncryptorClass(encryptPasswords);
        }};

        final ConfigurablePropertiesUserManagerFactory factory = new ConfigurablePropertiesUserManagerFactory();
        final PropertiesUserManager userManager;
        try {
            factory.applyConfig(serverConfig);
            assertTrue(factory.isConfigured());

            userManager = (PropertiesUserManager) factory.createUserManager();
        } catch (AttributeException | ConfigException e) {
            fail(e);
            return;
        }

        assertInstanceOf(expectedEncryptPasswords, userManager.getPasswordEncryptor());
    }

    private static Stream<Arguments> parameters__testCreate__encryptPasswords() {
        return Stream.of(
                Arguments.arguments(null, Md5PasswordEncryptor.class),
                Arguments.arguments(PasswordEncryptorType.MD5.getPasswordEncryptorClass(), Md5PasswordEncryptor.class),
                Arguments.arguments(PasswordEncryptorType.CLEAR.getPasswordEncryptorClass(), ClearTextPasswordEncryptor.class),
                Arguments.arguments(PasswordEncryptorType.SALTED.getPasswordEncryptorClass(), SaltedPasswordEncryptor.class)
        );
    }
}
