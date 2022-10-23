package jp.unaguna.mappedftp.filesystem.tree;

import org.apache.ftpserver.ftplet.FtpFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileTreeItem {
    boolean isDirectory();

    default boolean isRegularFile() {
        return !isDirectory();
    }

    /**
     * Create output stream for writing.
     *
     * @param offset The number of bytes at where to start writing.
     *               If the file is not random accessible,
     *               any offset other than zero will throw an exception.
     * @return An {@link OutputStream} used to write to the {@link FtpFile}
     * @throws IOException when some IO error occurred
     */
    OutputStream createOutputStream(long offset) throws IOException;

    /**
     * Create input stream for reading.
     *
     * @param offset The number of bytes of where to start reading.
     *               If the file is not random accessible,
     *               any offset other than zero will throw an exception.
     * @return An {@link InputStream} used to read the {@link FtpFile}
     * @throws IOException when some IO error occurred
     */
    InputStream createInputStream(long offset) throws IOException;

    /**
     * Returns the owner name
     *
     * @return The owner name of this file, or null if not specified and left to the file system.
     */
    default String getOwnerName() {
        return null;
    }

    /**
     * Returns the group name
     *
     * @return The group name of this file, or null if not specified and left to the file system.
     */
    default String getGroupName() {
        return null;
    }

    /**
     * Returns the last modified time (in UTC)
     *
     * @return The last modified time of this file, or null if not specified and left to the file system.
     */
    default Long getLastModified() {
        return null;
    }
}
