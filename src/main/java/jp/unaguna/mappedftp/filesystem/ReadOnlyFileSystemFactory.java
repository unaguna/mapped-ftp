package jp.unaguna.mappedftp.filesystem;

import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemDirectory;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemReadOnlyFile;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeNode;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

public class ReadOnlyFileSystemFactory implements FileSystemFactory {
    @Override
    public FileSystemView createFileSystemView(User user) throws FtpException {
        FileTreeNode root = new FileTreeNode(new FileTreeItemDirectory(), null);
        root.addChild(new FileTreeItemReadOnlyFile(), TreePath.get("dir", "item.txt"));
        return new LinkedFileSystemView(root);
    }
}
