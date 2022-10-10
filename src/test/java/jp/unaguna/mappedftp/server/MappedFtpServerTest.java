package jp.unaguna.mappedftp.server;

import jp.unaguna.mappedftp.MappedFtpServer;
import jp.unaguna.mappedftp.config.ConfigException;
import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.filesystem.ConfigurableFileSystemFactory;
import jp.unaguna.mappedftp.map.AttributeHashMap;
import jp.unaguna.mappedftp.stub.FileSystemFactoryStub;
import jp.unaguna.mappedftp.stub.FtpServerFactoryStub;
import jp.unaguna.mappedftp.stub.UserManagerFactoryStub;
import jp.unaguna.mappedftp.stub.UserManagerStub;
import jp.unaguna.mappedftp.user.ConfigurableUserManagerFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MappedFtpServerTest {

    @Test
    public void testStart() throws FtpException, ConfigException {
        final FtpServerFactoryStub ftpServerFactory = new FtpServerFactoryStub();

        final MappedFtpServer mappedFtpServer = new MappedFtpServer();
        final ServerConfig config = new ServerConfig();
        mappedFtpServer.setFtpServerFactory(ftpServerFactory);
        mappedFtpServer.setConfig(config, "stub_config");

        assertFalse(mappedFtpServer.isStarted());

        mappedFtpServer.start();

        assertTrue(mappedFtpServer.isStarted());
        verify(ftpServerFactory.getFtpServer(), times(1)).start();
    }

    @Test
    public void testStart__error_if_start_twice() throws FtpException, ConfigException {
        final FtpServerFactoryStub ftpServerFactory = new FtpServerFactoryStub();

        final MappedFtpServer mappedFtpServer = new MappedFtpServer();
        final ServerConfig config = new ServerConfig();
        mappedFtpServer.setFtpServerFactory(ftpServerFactory);
        mappedFtpServer.setConfig(config, "stub_config");
        mappedFtpServer.start();

        try {
            mappedFtpServer.start();
            fail("expected exception has not been thrown");

        } catch (IllegalStateException e) {
            // expected exception
            assertEquals("The server is already started.", e.getMessage());
        }
    }

    @Test
    public void testStart__use_specified_UserManagerFactory() throws FtpException, ConfigException {
        final FtpServerFactoryStub ftpServerFactory = new FtpServerFactoryStub();

        final MappedFtpServer mappedFtpServer = new MappedFtpServer();
        final ServerConfig config = new ServerConfig(){{
            setUserManagerFactoryClass(UserManagerFactoryStub.class);
        }};
        mappedFtpServer.setFtpServerFactory(ftpServerFactory);
        mappedFtpServer.setConfig(config, "stub_config");
        mappedFtpServer.start();

        // If UserManagerFactoryStub has worked, the user manager should be UserManagerStub.
        assertInstanceOf(UserManagerStub.class, ftpServerFactory.getUserManager());
    }

    @Test
    public void testStart__error_if_specified_UserManagerFactory_cannot_be_constructed() throws FtpException {
        final FtpServerFactoryStub ftpServerFactory = new FtpServerFactoryStub();

        final MappedFtpServer mappedFtpServer = new MappedFtpServer();
        final ServerConfig config = new ServerConfig(){{
            setUserManagerFactoryClass(UserManagerFactoryStubNonConstructed.class);
        }};
        mappedFtpServer.setFtpServerFactory(ftpServerFactory);
        mappedFtpServer.setConfig(config, "stub_config");

        try {
            mappedFtpServer.start();
            fail("expected exception has not been thrown");

        } catch (ConfigException e) {
            // expected exception
            assertEquals("failed to construct " + UserManagerFactoryStubNonConstructed.class,
                    e.getMessage());
        }
    }

    @Test
    public void testStart__error_if_the_config_is_illegal() throws FtpException {
        final FtpServerFactoryStub ftpServerFactory = new FtpServerFactoryStub();

        final MappedFtpServer mappedFtpServer = new MappedFtpServer();
        final ServerConfig config = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "classpath");
                put("path", "/file");
                put("src", "::::\0:");    // illegal
            }});
        }};
        mappedFtpServer.setFtpServerFactory(ftpServerFactory);
        mappedFtpServer.setConfig(config, "stub_config");

        try {
            mappedFtpServer.start();
            fail("expected exception has not been thrown");

        } catch (ConfigException e) {
            // expected exception
            assertEquals("loading config failed: stub_config", e.getMessage());
        }
    }

    @Test
    public void testStart__use_specified_FileSystemFactory() throws FtpException, ConfigException {
        final FtpServerFactoryStub ftpServerFactory = new FtpServerFactoryStub();

        final MappedFtpServer mappedFtpServer = new MappedFtpServer();
        final ServerConfig config = new ServerConfig(){{
            setFileSystemFactoryClass(FileSystemFactoryStub.class);
        }};
        mappedFtpServer.setFtpServerFactory(ftpServerFactory);
        mappedFtpServer.setConfig(config, "stub_config");
        mappedFtpServer.start();

        // If UserManagerFactoryStub has worked, the user manager should be UserManagerStub.
        assertInstanceOf(FileSystemFactoryStub.class, ftpServerFactory.getFileSystem());
    }

    @Test
    public void testStart__error_if_specified_FileSystemFactory_cannot_be_constructed() throws FtpException {
        final FtpServerFactoryStub ftpServerFactory = new FtpServerFactoryStub();

        final MappedFtpServer mappedFtpServer = new MappedFtpServer();
        final ServerConfig config = new ServerConfig(){{
            setFileSystemFactoryClass(FileSystemFactoryStubNonConstructed.class);
        }};
        mappedFtpServer.setFtpServerFactory(ftpServerFactory);
        mappedFtpServer.setConfig(config, "stub_config");

        try {
            mappedFtpServer.start();
            fail("expected exception has not been thrown");

        } catch (ConfigException e) {
            // expected exception
            assertEquals("failed to construct " + FileSystemFactoryStubNonConstructed.class,
                    e.getMessage());
        }
    }

    @Test
    public void testSetConfig__error_if_already_started() throws FtpException, ConfigException {
        final FtpServerFactoryStub ftpServerFactory = new FtpServerFactoryStub();

        final MappedFtpServer mappedFtpServer = new MappedFtpServer();
        final ServerConfig config = new ServerConfig();
        mappedFtpServer.setFtpServerFactory(ftpServerFactory);
        mappedFtpServer.setConfig(config, "stub_config");
        mappedFtpServer.start();

        try {
            mappedFtpServer.setConfig(config, "stub_config");
            fail("expected exception has not been thrown");

        } catch (IllegalStateException e) {
            // expected exception
            assertEquals("Configuration cannot be changed after the server has been started.", e.getMessage());
        }
    }

    @Test
    public void testSetFtpServerFactory__error_if_already_started() throws FtpException, ConfigException {
        final FtpServerFactoryStub ftpServerFactory = new FtpServerFactoryStub();

        final MappedFtpServer mappedFtpServer = new MappedFtpServer();
        final ServerConfig config = new ServerConfig();
        mappedFtpServer.setFtpServerFactory(ftpServerFactory);
        mappedFtpServer.setConfig(config, "stub_config");
        mappedFtpServer.start();

        try {
            mappedFtpServer.setFtpServerFactory(ftpServerFactory);
            fail("expected exception has not been thrown");

        } catch (IllegalStateException e) {
            // expected exception
            assertEquals("FtpServerFactory cannot be changed after the server has been started.", e.getMessage());
        }
    }

    public static class UserManagerFactoryStubNonConstructed implements ConfigurableUserManagerFactory {
        private UserManagerFactoryStubNonConstructed() {
            // cannot construct because it is private
        }

        @Override
        public UserManager createUserManager() {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public boolean isConfigured() {
            return false;
        }

        @Override
        public void applyConfig(ServerConfig serverConfig) {
            throw new UnsupportedOperationException("stub");
        }
    }

    public static class FileSystemFactoryStubNonConstructed implements ConfigurableFileSystemFactory {
        private FileSystemFactoryStubNonConstructed() {
            // cannot construct because it is private
        }

        @Override
        public boolean isConfigured() {
            return false;
        }

        @Override
        public void applyConfig(ServerConfig serverConfig) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public FileSystemView createFileSystemView(User user) {
            throw new UnsupportedOperationException("stub");
        }
    }
}
