package jp.unaguna.mappedftp.filesystem;

import org.apache.ftpserver.ftplet.FtpFile;

import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;

/**
 * An element of {@link LinkedFileSystemView}.
 */
public interface LinkedFileNode extends FtpFile {
    /**
     * Returns the node which is at specified relative path from this node.
     *
     * @param relativePath a relative path from this node
     * @return the node
     * @throws NotDirectoryException If an element other than the end of the path is a non-directory file
     * @throws NoSuchFileException   If the file indicated by the path does not exist
     */
    LinkedFileNode getNodeByRelativePath(TreePath relativePath) throws NotDirectoryException, NoSuchFileException;
}
