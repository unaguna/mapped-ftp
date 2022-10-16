package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.TestUtils;
import jp.unaguna.mappedftp.filesystem.ReadOnlyFileSystemFactory;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class NamespaceHandlerTest {
    @Test
    public void testHandlerRegistered(TestInfo testInfo) {
        final URL configPath = TestUtils.getInputResource("config.xml", testInfo);

        final FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(configPath.toString());
        final DefaultFtpServer actualServer = (DefaultFtpServer) ctx.getBean("testServer");

        assertInstanceOf(ReadOnlyFileSystemFactory.class, actualServer.getFileSystem());
    }
}
