package jp.unaguna.mappedftp.filesystem;

import jp.unaguna.mappedftp.filesystem.tree.FileTreeItem;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemDirectory;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeNode;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

import java.util.LinkedHashMap;
import java.util.Map;

public class MappingFileSystemFactory implements FileSystemFactory {
    private final Map<String, FileTreeItem> files;

    public MappingFileSystemFactory() {
        this.files = new LinkedHashMap<>();
    }

    public MappingFileSystemFactory(Map<String, FileTreeItem> files) throws FileSystemDefinitionException {
        this.files = files;

        for (String path : files.keySet()) {
            if ("/".equals(path)) {
                throw new FileSystemDefinitionException("cannot append a non-directory file on the root \"/\"");
            }
        }
    }

    @Override
    public LinkedFileSystemView createFileSystemView(User user) throws FtpException {
        FileTreeNode root = new FileTreeNode(new FileTreeItemDirectory(), null);

        files.forEach((path, fileTreeItem) -> root.appendSubFile(fileTreeItem, TreePath.get(path).toRelative()));

        return new LinkedFileSystemView(root);
    }
}
