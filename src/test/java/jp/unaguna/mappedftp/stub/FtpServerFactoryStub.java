package jp.unaguna.mappedftp.stub;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.mockito.Mockito;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

/**
 * A stub of FtpServerFactory for tests.
 *
 * <p>
 *     The start method of the {@link FtpServer} generated by the real {@link FtpServerFactory} blocks the thread and
 *     starts the actual FTP service,
 *     but the FtpServer generated by this Factory is a {@link Mockito#mock(Class)} object and nothing happens when
 *     start is executed.
 * </p>
 */
public class FtpServerFactoryStub extends FtpServerFactory {
    private final FtpServer ftpServer;

    public FtpServerFactoryStub() {
        this.ftpServer = mock(FtpServer.class);
        try {
            doNothing().when(ftpServer).start();
        } catch (FtpException e) {
            throw new RuntimeException(e);
        }
    }

    public FtpServer getFtpServer() {
        return ftpServer;
    }

    @Override
    public FtpServer createServer() {
        return ftpServer;
    }
}