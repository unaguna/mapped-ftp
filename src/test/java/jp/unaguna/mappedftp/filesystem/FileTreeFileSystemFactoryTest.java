package jp.unaguna.mappedftp.filesystem;

import jp.unaguna.mappedftp.TestUtils;
import jp.unaguna.mappedftp.UserStub;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItem;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromURL;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeNode;
import org.apache.ftpserver.ftplet.FtpException;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FileTreeFileSystemFactoryTest {

    @Test
    public void testCreate__path() {
        final Map<String, FileTreeItem> files = new LinkedHashMap<String, FileTreeItem>() {{
            put("/dir1/file1", new FileTreeItemFromURL(TestUtils.url("https://dummy1.example.com/")));
            put("/file2", new FileTreeItemFromURL(TestUtils.url("https://dummy2.example.com/")));
        }};

        final LinkedFileSystemView fileSystemView;
        try {
            final FileTreeFileSystemFactory factory = new FileTreeFileSystemFactory(files);
            fileSystemView = (LinkedFileSystemView) factory.createFileSystemView(new UserStub());
        } catch (FtpException | FileSystemDefinitionException e) {
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
    public void testCreate__error_by_root_as_not_dir() {
        final Map<String, FileTreeItem> files = new LinkedHashMap<String, FileTreeItem>() {{
            put("/", new FileTreeItemFromURL(TestUtils.url("https://dummy1.example.com/")));
        }};

        try {
            new FileTreeFileSystemFactory(files);
            fail("expected exception has not been thrown");

        } catch (FileSystemDefinitionException e) {
            // expected exception
            assertEquals("cannot append a non-directory file on the root \"/\"", e.getMessage());

        }
    }
}
