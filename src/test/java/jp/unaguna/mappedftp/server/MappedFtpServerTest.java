package jp.unaguna.mappedftp.server;

import jp.unaguna.mappedftp.MappedFtpServer;
import jp.unaguna.mappedftp.config.ConfigException;
import jp.unaguna.mappedftp.config.ServerConfig;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class MappedFtpServerTest {

    @Test
    public void testStart() throws FtpException, ConfigException {
        final FtpServerFactory ftpServerFactory = mock(FtpServerFactory.class);
        final FtpServer ftpServer = mock(FtpServer.class);
        doReturn(ftpServer).when(ftpServerFactory).createServer();
        doNothing().when(ftpServer).start();

        final MappedFtpServer mappedFtpServer = new MappedFtpServer();
        final ServerConfig config = new ServerConfig();
        mappedFtpServer.setFtpServerFactory(ftpServerFactory);
        mappedFtpServer.setConfig(config, "stub_config");
        mappedFtpServer.start();

        verify(ftpServer, times(1)).start();
    }
}
