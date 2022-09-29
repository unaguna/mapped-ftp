package jp.unaguna.mappedftp.filesystem;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;

import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;

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
        final FileTreeNode node;
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

    private FileTreeNode pathToNode(String path) throws NotDirectoryException, NoSuchFileException {
        if ("/".equals(path)) {
            return root;
        } else if (path.startsWith("/")) {
            return root.getNodeByRelativePath(TreePath.get(path.substring(1)));
        } else {
            return workingDirectory.getNodeByRelativePath(TreePath.get(path));
        }
    }
}
