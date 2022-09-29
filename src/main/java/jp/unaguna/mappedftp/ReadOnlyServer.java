package jp.unaguna.mappedftp;

import jp.unaguna.mappedftp.filesystem.ReadOnlyFileSystemFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UserFactory;

public class ReadOnlyServer {
    public static void main(String[] args) throws FtpException {
        FtpServerFactory ftpServerFactory = new FtpServerFactory();

        UserFactory userFactory = new UserFactory();
        userFactory.setName("anonymous");
        User anonymous = userFactory.createUser();

        UserManager userManager = ftpServerFactory.getUserManager();
        userManager.save(anonymous);

        ftpServerFactory.setFileSystem(new ReadOnlyFileSystemFactory());

        FtpServer server = ftpServerFactory.createServer();
        server.start();
    }
}
