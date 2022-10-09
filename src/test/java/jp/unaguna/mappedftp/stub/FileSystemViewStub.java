package jp.unaguna.mappedftp.stub;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpFile;

public class FileSystemViewStub implements FileSystemView {
    @Override
    public FtpFile getHomeDirectory() {
        return null;
    }

    @Override
    public FtpFile getWorkingDirectory() {
        return null;
    }

    @Override
    public boolean changeWorkingDirectory(String dir) {
        return false;
    }

    @Override
    public FtpFile getFile(String file) {
        return null;
    }

    @Override
    public boolean isRandomAccessible() {
        return false;
    }

    @Override
    public void dispose() {

    }
}
