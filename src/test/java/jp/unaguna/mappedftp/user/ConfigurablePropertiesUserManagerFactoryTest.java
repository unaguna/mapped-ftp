package jp.unaguna.mappedftp.user;

import jp.unaguna.mappedftp.TestUtils;
import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurablePropertiesUserManagerFactoryTest {
    @Test
    public void testConfigured__true() {
        final ServerConfig serverConfig = new ServerConfig();

        final ConfigurablePropertiesUserManagerFactory factory = new ConfigurablePropertiesUserManagerFactory();
        try {
            factory.applyConfig(serverConfig);
        } catch (AttributeException e) {
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
        final Path userPropertiesPath = TestUtils.getInputResource("user.properties", testInfo);

        final ServerConfig serverConfig = new ServerConfig(){{
            setUserPropertiesPath(userPropertiesPath);
        }};

        final ConfigurablePropertiesUserManagerFactory factory = new ConfigurablePropertiesUserManagerFactory();
        final PropertiesUserManager userManager;
        try {
            factory.applyConfig(serverConfig);
            assertTrue(factory.isConfigured());

            userManager = (PropertiesUserManager) factory.createUserManager();
        } catch (AttributeException e) {
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
        } catch (AttributeException e) {
            fail(e);
            return;
        }

        String[] userNameList = userManager.getAllUserNames();

        // assert that no user exists
        assertEquals(0, userNameList.length);
    }
}
