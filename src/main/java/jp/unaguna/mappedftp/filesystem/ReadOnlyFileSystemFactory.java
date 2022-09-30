package jp.unaguna.mappedftp.filesystem;

import jp.unaguna.mappedftp.filesystem.tree.FileTreeItem;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemDirectory;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeNode;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

import java.util.Map;

public class ReadOnlyFileSystemFactory implements FileSystemFactory {
    private final Map<String, FileTreeItem> files;

    public ReadOnlyFileSystemFactory(Map<String, FileTreeItem> files) {
        this.files = files;
    }

    @Override
    public FileSystemView createFileSystemView(User user) throws FtpException {
        FileTreeNode root = new FileTreeNode(new FileTreeItemDirectory(), null);

        files.forEach((path, fileTreeItem) -> root.addChild(fileTreeItem, TreePath.get(path)));

        return new LinkedFileSystemView(root);
    }
}
