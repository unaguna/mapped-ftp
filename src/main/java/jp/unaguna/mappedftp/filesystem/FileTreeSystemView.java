package jp.unaguna.mappedftp.filesystem;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;

public class FileTreeSystemView implements FileSystemView {
    private final FileTreeNode root;
    private FileTreeNode workingDirectory;

    public FileTreeSystemView(FileTreeNode root) {
        this.root = root;
        this.workingDirectory = root;
    }

    @Override
    public FtpFile getHomeDirectory() throws FtpException {
        return root;
    }

    @Override
    public FtpFile getWorkingDirectory() throws FtpException {
        return workingDirectory;
    }

    @Override
    public boolean changeWorkingDirectory(String dir) throws FtpException {
        // TODO: 実装
        return false;
    }

    @Override
    public FtpFile getFile(String file) throws FtpException {
        // TODO: 実装
        if (file.equals("/")) return root;
        if (file.equals("./")) return workingDirectory;
        return null;
    }

    @Override
    public boolean isRandomAccessible() throws FtpException {
        return false;
    }

    @Override
    public void dispose() {

    }
}
