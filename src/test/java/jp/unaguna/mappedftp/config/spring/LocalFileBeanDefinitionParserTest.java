package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.TestUtils;
import jp.unaguna.mappedftp.UserStub;
import jp.unaguna.mappedftp.filesystem.LinkedFileSystemView;
import jp.unaguna.mappedftp.filesystem.MappingFileSystemFactory;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromLocalFile;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeNode;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LocalFileBeanDefinitionParserTest {
    @Test
    public void testParse(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource("config.xml", testInfo);

        // TODO: できれば Parser を直接テストして、他のタグやファイルシステムクラスの仕様変更の影響を受けないようにしたい
        final FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(configPath.toString());
        final DefaultFtpServer actualServer = (DefaultFtpServer) ctx.getBean("testServer");
        final MappingFileSystemFactory fileSystemFactory = (MappingFileSystemFactory) actualServer.getFileSystem();

        try {
            final LinkedFileSystemView fileSystemView = fileSystemFactory.createFileSystemView(new UserStub());
            final FileTreeNode fileTreeNode = (FileTreeNode) fileSystemView.getFile("/file1");
            final FileTreeItemFromLocalFile file = (FileTreeItemFromLocalFile) fileTreeNode.getFile();
            assertEquals(Paths.get("dir1/dummy.txt"), file.getSource());
            assertNull(file.getLastModified());
            assertNull(file.getOwnerName());
            assertNull(file.getGroupName());

        } catch (FtpException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "config__last_modified__long.xml, 1234",
            "config__last_modified__iso8601_extended.xml, 1666469078000",
            "config__last_modified__iso8601_extended_z.xml, 1666501478000",
            "config__last_modified__iso8601_extended_offset.xml, 1666505078000",
    })
    public void testParse__with_last_modified__const(
            String inputResourceName, long expectedLastModified, TestInfo testInfo
    ) {
        final URL configPath = TestUtils.getInputResource(inputResourceName, testInfo);

        final FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(configPath.toString());
        final DefaultFtpServer actualServer = (DefaultFtpServer) ctx.getBean("testServer");
        final MappingFileSystemFactory fileSystemFactory = (MappingFileSystemFactory) actualServer.getFileSystem();

        try {
            final LinkedFileSystemView fileSystemView = fileSystemFactory.createFileSystemView(new UserStub());
            final FileTreeNode fileTreeNode = (FileTreeNode) fileSystemView.getFile("/file1");
            final FileTreeItemFromLocalFile file = (FileTreeItemFromLocalFile) fileTreeNode.getFile();
            assertEquals(Paths.get("dir1/dummy.txt"), file.getSource());
            assertEquals(expectedLastModified, file.getLastModified());

        } catch (FtpException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "config__owner.xml, test_owner, ",
            "config__group.xml, , test_group",
            "config__owner_and_group.xml, test_owner, test_group",
    })
    public void testParse__with_owner_and_group(
            String inputResourceName, String expectedOwnerName, String expectedGroupName, TestInfo testInfo
    ) {
        final URL configPath = TestUtils.getInputResource(inputResourceName, testInfo);

        final FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(configPath.toString());
        final DefaultFtpServer actualServer = (DefaultFtpServer) ctx.getBean("testServer");
        final MappingFileSystemFactory fileSystemFactory = (MappingFileSystemFactory) actualServer.getFileSystem();

        try {
            final LinkedFileSystemView fileSystemView = fileSystemFactory.createFileSystemView(new UserStub());
            final FileTreeNode fileTreeNode = (FileTreeNode) fileSystemView.getFile("/file1");
            final FileTreeItemFromLocalFile file = (FileTreeItemFromLocalFile) fileTreeNode.getFile();
            assertEquals(Paths.get("dir1/dummy.txt"), file.getSource());
            assertEquals(expectedOwnerName, file.getOwnerName());
            assertEquals(expectedGroupName, file.getGroupName());

        } catch (FtpException e) {
            fail(e);
        }
    }
}
