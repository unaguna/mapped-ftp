package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.TestUtils;
import jp.unaguna.mappedftp.UserStub;
import jp.unaguna.mappedftp.filesystem.LinkedFileSystemView;
import jp.unaguna.mappedftp.filesystem.FileTreeFileSystemFactory;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromLocalFile;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeNode;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class LocalFileBeanDefinitionParserTest {
    @Test
    public void testParse(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource("config.xml", testInfo);

        // TODO: できれば Parser を直接テストして、他のタグやファイルシステムクラスの仕様変更の影響を受けないようにしたい
        final FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(configPath.toString());
        final DefaultFtpServer actualServer = (DefaultFtpServer) ctx.getBean("testServer");
        final FileTreeFileSystemFactory fileSystemFactory = (FileTreeFileSystemFactory) actualServer.getFileSystem();

        try {
            final LinkedFileSystemView fileSystemView = fileSystemFactory.createFileSystemView(new UserStub());
            final FileTreeNode fileTreeNode = (FileTreeNode) fileSystemView.getFile("/file1");
            final FileTreeItemFromLocalFile file = (FileTreeItemFromLocalFile) fileTreeNode.getFile();
            assertEquals(Paths.get("dir1/dummy.txt"), file.getSource());

        } catch (FtpException e) {
            fail(e);
        }
    }
}
