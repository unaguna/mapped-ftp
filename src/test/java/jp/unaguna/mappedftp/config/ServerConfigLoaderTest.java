package jp.unaguna.mappedftp.config;

import jp.unaguna.mappedftp.TestUtils;
import jp.unaguna.mappedftp.map.AttributeHashMap;
import jp.unaguna.mappedftp.map.AttributeMissingException;
import jp.unaguna.mappedftp.stub.PasswordEncryptorStub;
import jp.unaguna.mappedftp.user.ConfigurablePropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ServerConfigLoaderTest {

    @Test
    public void testLoadFiles(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource("serverConfig.xml", testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        final ServerConfig serverConfig;
        try {
            serverConfig = serverConfigLoader.load(configPath);
        } catch (ConfigException | IOException | SAXException e) {
            fail(e);
            return;
        }

        final List<AttributeHashMap> loadedFiles = new ArrayList<>(2);
        serverConfig.getFilesIterator().forEachRemaining(loadedFiles::add);

        try {
            // assert files[0]
            assertEquals("url", loadedFiles.get(0).pop("type"));
            assertEquals("/https/google.txt", loadedFiles.get(0).pop("path"));
            assertEquals("https://www.google.com/", loadedFiles.get(0).pop("src"));
            assertEquals(0, loadedFiles.get(0).size(),
                    () -> "redundant attributes: " + String.join(",", loadedFiles.get(0).keySet()));

            // assert files[1]
            assertEquals("url", loadedFiles.get(1).pop("type"));
            assertEquals("/https/yahoo.txt", loadedFiles.get(1).pop("path"));
            assertEquals("https://www.yahoo.co.jp/", loadedFiles.get(1).pop("src"));
            assertEquals(0, loadedFiles.get(1).size(),
                    () -> "redundant attributes: " + String.join(",", loadedFiles.get(1).keySet()));

            // assert fileSystemFactoryClass
            assertNull(serverConfig.getFileSystemFactoryClass());

        } catch (AttributeMissingException e) {
            fail(e);
        }
    }

    @Test
    public void testLoadFiles__miss_files(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource("serverConfig__miss_files.xml", testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        final ServerConfig serverConfig;
        try {
            serverConfig = serverConfigLoader.load(configPath);
        } catch (ConfigException | IOException | SAXException e) {
            fail(e);
            return;
        }

        final List<AttributeHashMap> loadedFiles = new ArrayList<>(2);
        serverConfig.getFilesIterator().forEachRemaining(loadedFiles::add);

        assertEquals(0, loadedFiles.size());
    }

    @Test
    public void testLoadFiles__unknown_root_tag(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource("serverConfig__unknown_root_tag.xml", testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        try {
            serverConfigLoader.load(configPath);
            fail("expected exception has not been thrown");

        } catch (ConfigException e) {
            // expected exception
            assertEquals("The root element of the configuration file must be MappedFtp", e.getMessage());

        } catch (IOException | SAXException e) {
            fail(e);
        }
    }

    @Test
    public void testLoadFiles__unknown_tag_in_root(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource("serverConfig__unknown_tag_in_root.xml", testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        try {
            serverConfigLoader.load(configPath);
            fail("expected exception has not been thrown");

        } catch (ConfigException e) {
            // expected exception
            assertEquals("Unexpected tag found: dummy", e.getMessage());

        } catch (IOException | SAXException e) {
            fail(e);
        }
    }

    @Test
    public void testLoadUserManager(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource("serverConfig__user_manager.xml", testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        final ServerConfig serverConfig;
        try {
            serverConfig = serverConfigLoader.load(configPath);
        } catch (ConfigException | IOException | SAXException e) {
            fail(e);
            return;
        }

        assertEquals("user.properties", serverConfig.getUserPropertiesPath());
        assertEquals(ConfigurablePropertiesUserManagerFactory.class, serverConfig.getUserManagerFactoryClass());
    }

    @Test
    public void testLoadUserManager__without_user_manager(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource("serverConfig__without_user_manager.xml", testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        final ServerConfig serverConfig;
        try {
            serverConfig = serverConfigLoader.load(configPath);
        } catch (ConfigException | IOException | SAXException e) {
            fail(e);
            return;
        }

        assertNull(serverConfig.getUserPropertiesPath());
        assertNull(serverConfig.getUserManagerFactoryClass());
    }

    @ParameterizedTest
    @MethodSource("parameters__testLoadUserManager__encrypt_passwords")
    public void testLoadUserManager__encrypt_passwords(
            String attrValue,
            Class<? extends PasswordEncryptor> expectedEncryptPasswords,
            TestInfo testInfo) {
        final String inputResourceName = "serverConfig__encrypt-password_" + attrValue + ".xml";
        final URL configPath = TestUtils.getInputResource(inputResourceName, testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        final ServerConfig serverConfig;
        try {
            serverConfig = serverConfigLoader.load(configPath);
        } catch (ConfigException | IOException | SAXException e) {
            fail(e);
            return;
        }

        assertEquals(expectedEncryptPasswords, serverConfig.getPasswordEncryptorClass());
    }

    @ParameterizedTest
    @CsvSource({
            "dummy, dummy",
            "String, java.lang.String"
    })
    public void testLoadUserManager__illegal_encrypt_passwords_in_file_user_manager(
            String inputResourceMarker,
            String expectedInputValue,
            TestInfo testInfo
    ) {
        final String inputResourceName = "serverConfig__encrypt-password_" + inputResourceMarker + ".xml";
        final URL configPath = TestUtils.getInputResource(inputResourceName, testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        try {
            serverConfigLoader.load(configPath);
            fail("expected exception has not been thrown");

        } catch (ConfigException e) {
            // expected exception
            assertEquals("Unexpected value is appended to the attribute \"encrypt-passwords\": " + expectedInputValue, e.getMessage());

        } catch (IOException | SAXException e) {
            fail(e);
        }
    }

    @Test
    public void testLoadUserManager__duplicate_user_manager(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource("serverConfig__duplicate_user_manager.xml", testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        try {
            serverConfigLoader.load(configPath);
            fail("expected exception has not been thrown");

        } catch (ConfigException e) {
            // expected exception
            assertEquals("Multiple user managers cannot be specified.", e.getMessage());

        } catch (IOException | SAXException e) {
            fail(e);
        }
    }

    @Test
    public void testLoadUserManager__unknown_attr_in_file_user_manager(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource(
                "serverConfig__unknown_attr_in_file-user-manager.xml", testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        try {
            serverConfigLoader.load(configPath);
            fail("expected exception has not been thrown");

        } catch (ConfigException e) {
            // expected exception
            assertEquals("Unexpected attribute found in <file-user-manager>: dummy=\"\"", e.getMessage());

        } catch (IOException | SAXException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "files",
            "file-user-manager"
    })
    public void testLoad__unknown_tag(String parentTagName, TestInfo testInfo) {
        final String inputResourceName = "serverConfig__unknown_tag_in_" + parentTagName + ".xml";
        final URL configPath = TestUtils.getInputResource(inputResourceName, testInfo);

        final ServerConfigLoader serverConfigLoader = new ServerConfigLoader();
        try {
            serverConfigLoader.load(configPath);
            fail("expected exception has not been thrown");

        } catch (ConfigException e) {
            // expected exception
            assertEquals("Unexpected tag found in <" + parentTagName + ">: dummy", e.getMessage());

        } catch (IOException | SAXException e) {
            fail(e);
        }
    }

    private static Stream<Arguments> parameters__testLoadUserManager__encrypt_passwords() {
        return Stream.of(
                Arguments.arguments("unset", null),
                Arguments.arguments("md5", Md5PasswordEncryptor.class),
                Arguments.arguments("clear", ClearTextPasswordEncryptor.class),
                Arguments.arguments("salted", SaltedPasswordEncryptor.class),
                Arguments.arguments("stub_class", PasswordEncryptorStub.class)
        );
    }
}
