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
     * @throws IOException
     */
    OutputStream createOutputStream(long offset) throws IOException;

    /**
     * Create input stream for reading.
     *
     * @param offset The number of bytes of where to start reading.
     *               If the file is not random accessible,
     *               any offset other than zero will throw an exception.
     * @return An {@link InputStream} used to read the {@link FtpFile}
     * @throws IOException
     */
    InputStream createInputStream(long offset) throws IOException;

    default String getOwnerName() {
        return null;
    }

    default String getGroupName() {
        return null;
    }
}
