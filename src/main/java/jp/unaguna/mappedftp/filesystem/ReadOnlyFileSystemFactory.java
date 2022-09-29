package jp.unaguna.mappedftp.filesystem;

import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemDirectory;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromURL;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeNode;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

import java.net.MalformedURLException;
import java.net.URL;

public class ReadOnlyFileSystemFactory implements FileSystemFactory {
    @Override
    public FileSystemView createFileSystemView(User user) throws FtpException {
        FileTreeNode root = new FileTreeNode(new FileTreeItemDirectory(), null);

        final URL url;
        try {
            url = new URL("https://www.google.com");
        } catch (MalformedURLException e) {
            throw new FtpException(e);
        }
        root.addChild(new FileTreeItemFromURL(url), TreePath.get("dir", "item.txt"));

        return new LinkedFileSystemView(root);
    }
}
