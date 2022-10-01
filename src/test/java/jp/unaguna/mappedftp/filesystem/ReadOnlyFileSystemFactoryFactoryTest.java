package jp.unaguna.mappedftp.filesystem;

import jp.unaguna.mappedftp.TestUtils;
import jp.unaguna.mappedftp.UserStub;
import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromLocalFile;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromURL;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeNode;
import jp.unaguna.mappedftp.map.AttributeException;
import jp.unaguna.mappedftp.map.AttributeHashMap;
import jp.unaguna.mappedftp.map.IllegalAttributeException;
import jp.unaguna.mappedftp.map.UnknownAttributeException;
import org.apache.ftpserver.ftplet.FtpException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.net.MalformedURLException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ReadOnlyFileSystemFactoryFactoryTest {
    @Test
    public void testCreate__attribute_path() {
        final ServerConfig serverConfig = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "url");
                put("path", "/dir1/file1");
                put("src", "https://dummy1.example.com/");
            }});
            putFile(new AttributeHashMap(){{
                put("type", "url");
                put("path", "/file2");
                put("src", "https://dummy2.example.com/");
            }});
        }};

        final ReadOnlyFileSystemFactoryFactory factoryFactory = new ReadOnlyFileSystemFactoryFactory();

        final ReadOnlyFileSystemFactory factory;
        final LinkedFileSystemView fileSystemView;
        try {
            factory = (ReadOnlyFileSystemFactory) factoryFactory.createFileSystemFactory(serverConfig);
            fileSystemView = (LinkedFileSystemView) factory.createFileSystemView(new UserStub());
        } catch (AttributeException | FtpException e) {
            fail(e);
            return;
        }

        try {
            final FileTreeNode nodeDir1 = (FileTreeNode) fileSystemView.getFile("/dir1");
            assertEquals("dir1", nodeDir1.getName());
            assertTrue(nodeDir1.isDirectory());

            final FileTreeNode nodeFile1 = (FileTreeNode) fileSystemView.getFile("/dir1/file1");
            assertEquals("file1", nodeFile1.getName());
            assertFalse(nodeFile1.isDirectory());

            final FileTreeNode nodeFile2 = (FileTreeNode) fileSystemView.getFile("/file2");
            assertEquals("file2", nodeFile2.getName());
            assertFalse(nodeFile2.isDirectory());
        } catch (FtpException e) {
            fail(e);
        }
    }

    @Test
    public void testCreate__illegal_type() {
        final ServerConfig serverConfig = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "dummy");
                put("path", "/dir1/file1");
            }});
        }};

        final ReadOnlyFileSystemFactoryFactory factoryFactory = new ReadOnlyFileSystemFactoryFactory();

        final ReadOnlyFileSystemFactory factory;
        try {
            factory = (ReadOnlyFileSystemFactory) factoryFactory.createFileSystemFactory(serverConfig);
            factory.createFileSystemView(new UserStub());
            fail("expected exception has not been thrown");

        } catch (IllegalAttributeException e) {
            // expected exception
            assertEquals("unknown type is specified: type=\"dummy\"", e.getMessage());

        } catch (AttributeException|FtpException e) {
            fail(e);
        }
    }

    @Test
    public void testCreate__type_url__attribute_src() {
        final ServerConfig serverConfig = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "url");
                put("path", "/dir1/file1");
                put("src", "https://dummy1.example.com/");
            }});
            putFile(new AttributeHashMap(){{
                put("type", "url");
                put("path", "/file2");
                put("src", "https://dummy2.example.com/");
            }});
        }};

        final ReadOnlyFileSystemFactoryFactory factoryFactory = new ReadOnlyFileSystemFactoryFactory();

        final ReadOnlyFileSystemFactory factory;
        final LinkedFileSystemView fileSystemView;
        try {
            factory = (ReadOnlyFileSystemFactory) factoryFactory.createFileSystemFactory(serverConfig);
            fileSystemView = (LinkedFileSystemView) factory.createFileSystemView(new UserStub());
        } catch (AttributeException | FtpException e) {
            fail(e);
            return;
        }

        try {
            final FileTreeNode nodeFile1 = (FileTreeNode) fileSystemView.getFile("/dir1/file1");
            final FileTreeItemFromURL itemFile1 = (FileTreeItemFromURL) nodeFile1.getFile();
            assertEquals("https://dummy1.example.com/", itemFile1.getSource().toString());

            final FileTreeNode nodeFile2 = (FileTreeNode) fileSystemView.getFile("/file2");
            final FileTreeItemFromURL itemFile2 = (FileTreeItemFromURL) nodeFile2.getFile();
            assertEquals("https://dummy2.example.com/", itemFile2.getSource().toString());
        } catch (FtpException e) {
            fail(e);
        }
    }

    @Test
    public void testCreate__type_url__error_by_root_as_not_dir() {
        final ServerConfig serverConfig = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "url");
                put("path", "/");
                put("src", "https://dummy1.example.com/");
            }});
        }};

        final ReadOnlyFileSystemFactoryFactory factoryFactory = new ReadOnlyFileSystemFactoryFactory();

        final ReadOnlyFileSystemFactory factory;
        try {
            factory = (ReadOnlyFileSystemFactory) factoryFactory.createFileSystemFactory(serverConfig);
            factory.createFileSystemView(new UserStub());
            fail("expected exception has not been thrown");

        } catch (IllegalAttributeException e) {
            // expected exception
            assertEquals("cannot append a non-directory file on the root \"/\"", e.getMessage());

        } catch (AttributeException|FtpException e) {
            fail(e);
        }
    }

    @Test
    public void testCreate__type_url__error_by_illegal_url() {
        final ServerConfig serverConfig = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "url");
                put("path", "/file1");
                put("src", "dummy1.example.com");
            }});
        }};

        final ReadOnlyFileSystemFactoryFactory factoryFactory = new ReadOnlyFileSystemFactoryFactory();

        final ReadOnlyFileSystemFactory factory;
        try {
            factory = (ReadOnlyFileSystemFactory) factoryFactory.createFileSystemFactory(serverConfig);
            factory.createFileSystemView(new UserStub());
            fail("expected exception has not been thrown");

        } catch (IllegalAttributeException e) {
            // expected exception
            assertEquals("illegal attribute: src", e.getMessage());
            assertInstanceOf(MalformedURLException.class, e.getCause());
            assertTrue(e.getCause().getMessage().contains("dummy1.example.com"));

        } catch (AttributeException|FtpException e) {
            fail(e);
        }
    }

    @Test
    public void testCreate__type_url__error_by_unknown_attribute() {
        final ServerConfig serverConfig = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "url");
                put("path", "/dir1/file1");
                put("src", "https://dummy1.example.com/");
                put("dummy", "");
            }});
        }};

        final ReadOnlyFileSystemFactoryFactory factoryFactory = new ReadOnlyFileSystemFactoryFactory();

        final ReadOnlyFileSystemFactory factory;
        try {
            factory = (ReadOnlyFileSystemFactory) factoryFactory.createFileSystemFactory(serverConfig);
            factory.createFileSystemView(new UserStub());
            fail("expected exception has not been thrown");

        } catch (UnknownAttributeException e) {
            // expected exception
            assertTrue(e.getMessage().endsWith(": dummy"));

        } catch (AttributeException|FtpException e) {
            fail(e);
        }
    }

    @Test
    public void testCreate__type_local__attribute_src(TestInfo testInfo) {
        Path localPath = TestUtils.getInputResource("local.txt", testInfo);

        final ServerConfig serverConfig = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "local");
                put("path", "/dir1/file1");
                put("src", localPath.toString());
            }});
        }};

        final ReadOnlyFileSystemFactoryFactory factoryFactory = new ReadOnlyFileSystemFactoryFactory();

        final ReadOnlyFileSystemFactory factory;
        final LinkedFileSystemView fileSystemView;
        try {
            factory = (ReadOnlyFileSystemFactory) factoryFactory.createFileSystemFactory(serverConfig);
            fileSystemView = (LinkedFileSystemView) factory.createFileSystemView(new UserStub());
        } catch (AttributeException | FtpException e) {
            fail(e);
            return;
        }

        try {
            final FileTreeNode nodeFile1 = (FileTreeNode) fileSystemView.getFile("/dir1/file1");
            final FileTreeItemFromLocalFile itemFile1 = (FileTreeItemFromLocalFile) nodeFile1.getFile();
            assertEquals(localPath, itemFile1.getSource());

        } catch (FtpException e) {
            fail(e);
        }
    }

    @Test
    public void testCreate__type_local__error_by_root_as_not_dir(TestInfo testInfo) {
        Path localPath = TestUtils.getInputResource("local.txt", testInfo);

        final ServerConfig serverConfig = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "local");
                put("path", "/");
                put("src", localPath.toString());
            }});
        }};

        final ReadOnlyFileSystemFactoryFactory factoryFactory = new ReadOnlyFileSystemFactoryFactory();

        final ReadOnlyFileSystemFactory factory;
        try {
            factory = (ReadOnlyFileSystemFactory) factoryFactory.createFileSystemFactory(serverConfig);
            factory.createFileSystemView(new UserStub());
            fail("expected exception has not been thrown");

        } catch (IllegalAttributeException e) {
            // expected exception
            assertEquals("cannot append a non-directory file on the root \"/\"", e.getMessage());

        } catch (AttributeException|FtpException e) {
            fail(e);
        }
    }

    @Test
    public void testCreate__type_local__error_by_illegal_path() {
        final ServerConfig serverConfig = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "local");
                put("path", "/file1");
                put("src", ":::::");
            }});
        }};

        final ReadOnlyFileSystemFactoryFactory factoryFactory = new ReadOnlyFileSystemFactoryFactory();

        final ReadOnlyFileSystemFactory factory;
        try {
            factory = (ReadOnlyFileSystemFactory) factoryFactory.createFileSystemFactory(serverConfig);
            factory.createFileSystemView(new UserStub());
            fail("expected exception has not been thrown");

        } catch (IllegalAttributeException e) {
            // expected exception
            assertEquals("illegal attribute: src", e.getMessage());
            assertInstanceOf(InvalidPathException.class, e.getCause());
            assertTrue(e.getCause().getMessage().contains(":::::"));

        } catch (AttributeException|FtpException e) {
            fail(e);
        }
    }

    @Test
    public void testCreate__type_local__error_by_unknown_attribute(TestInfo testInfo) {
        Path localPath = TestUtils.getInputResource("local.txt", testInfo);

        final ServerConfig serverConfig = new ServerConfig(){{
            putFile(new AttributeHashMap(){{
                put("type", "local");
                put("path", "/dir1/file1");
                put("src", localPath.toString());
                put("dummy", "");
            }});
        }};

        final ReadOnlyFileSystemFactoryFactory factoryFactory = new ReadOnlyFileSystemFactoryFactory();

        final ReadOnlyFileSystemFactory factory;
        try {
            factory = (ReadOnlyFileSystemFactory) factoryFactory.createFileSystemFactory(serverConfig);
            factory.createFileSystemView(new UserStub());
            fail("expected exception has not been thrown");

        } catch (UnknownAttributeException e) {
            // expected exception
            assertTrue(e.getMessage().endsWith(": dummy"));

        } catch (AttributeException|FtpException e) {
            fail(e);
        }
    }
}
