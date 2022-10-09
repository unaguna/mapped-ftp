package jp.unaguna.mappedftp.filesystem;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;

import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;

public class LinkedFileSystemView implements FileSystemView {
    private final LinkedFileNode root;
    private LinkedFileNode workingDirectory;

    public LinkedFileSystemView(LinkedFileNode root) {
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
        final LinkedFileNode node;
        try {
            node = pathToNode(dir);
        } catch (NotDirectoryException | NoSuchFileException e) {
            throw new FtpException(e);
        }

        this.workingDirectory = node;
        return true;
    }

    @Override
    public FtpFile getFile(String file) throws FtpException {
        try {
            return pathToNode(file);
        } catch (NotDirectoryException | NoSuchFileException e) {
            throw new FtpException(e);
        }
    }

    @Override
    public boolean isRandomAccessible() throws FtpException {
        return false;
    }

    @Override
    public void dispose() {

    }

    private LinkedFileNode pathToNode(String path) throws NotDirectoryException, NoSuchFileException {
        if ("/".equals(path)) {
            return root;
        } else if (path.startsWith("/")) {
            return root.getNodeByRelativePath(TreePath.get(path.substring(1)));
        } else {
            return workingDirectory.getNodeByRelativePath(TreePath.get(path));
        }
    }
}
