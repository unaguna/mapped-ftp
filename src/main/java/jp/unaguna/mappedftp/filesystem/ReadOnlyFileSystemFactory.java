package jp.unaguna.mappedftp.filesystem;

import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.filesystem.tree.*;
import jp.unaguna.mappedftp.map.AttributeException;
import jp.unaguna.mappedftp.map.AttributeHashMap;
import jp.unaguna.mappedftp.map.IllegalAttributeException;
import jp.unaguna.mappedftp.map.UnknownAttributeException;
import jp.unaguna.mappedftp.utils.ClasspathUtils;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReadOnlyFileSystemFactory implements ConfigurableFileSystemFactory {
    private boolean configured = false;
    private final Map<String, FileTreeItem> files = new LinkedHashMap<>();

    @Override
    public boolean isConfigured() {
        return configured;
    }

    @Override
    public FileSystemView createFileSystemView(User user) throws FtpException {
        FileTreeNode root = new FileTreeNode(new FileTreeItemDirectory(), null);

        files.forEach((path, fileTreeItem) -> root.appendSubFile(fileTreeItem, TreePath.get(path).toRelative()));

        return new LinkedFileSystemView(root);
    }

    @Override
    public void applyConfig(ServerConfig config) throws AttributeException {
        for (Iterator<AttributeHashMap> it = config.getFilesIterator(); it.hasNext(); ) {
            AttributeHashMap fileAttributes = it.next();

            // load attributes
            final String path = fileAttributes.get("path");
            final FileTreeItem fileTreeItem = createFileTreeItem(fileAttributes);
            fileAttributes.remove("path");

            // Throw an exception if there are attributes not yet used (in other word, not yet popped)
            if (fileAttributes.size() > 0) {
                throw new UnknownAttributeException(fileAttributes.keySet());
            }

            // Throw an exception if a non-directory file is set to the root path
            if (fileTreeItem.isRegularFile() && path.equals("/")) {
                throw new IllegalAttributeException("cannot append a non-directory file on the root \"/\"");
            }

            files.put(path, fileTreeItem);
        }

        this.configured = true;
    }

    private FileTreeItem createFileTreeItem(AttributeHashMap fileAttributes) throws AttributeException {
        // TODO: このメソッドごと依存性注入できるように FileTreeItemFactory を作る

        final String type = fileAttributes.pop("type");
        switch (type) {
            case "local":
                return createFileTreeItemFromLocalFile(fileAttributes);
            case "url":
                return createFileTreeItemFromURL(fileAttributes);
            case "classpath":
                return createFileTreeItemFromClasspath(fileAttributes);
            default:
                throw new IllegalAttributeException("unknown type is specified: type=\"" + type + "\"");
        }
    }

    private static FileTreeItem createFileTreeItemFromLocalFile(AttributeHashMap fileAttributes) throws AttributeException {

        // load attributes
        final Path local;
        try {
            local = Paths.get(fileAttributes.pop("src"));
        } catch (InvalidPathException e) {
            throw new IllegalAttributeException("src", e);
        }

        return new FileTreeItemFromLocalFile(local);
    }

    private static FileTreeItem createFileTreeItemFromURL(AttributeHashMap fileAttributes) throws AttributeException {

        // load attributes
        final URL url;
        try {
            url = new URL(fileAttributes.pop("src"));
        } catch (MalformedURLException e) {
            throw new IllegalAttributeException("src", e);
        }

        return new FileTreeItemFromURL(url);
    }

    private static FileTreeItem createFileTreeItemFromClasspath(AttributeHashMap fileAttributes) throws AttributeException {

        // load attributes
        final String src = fileAttributes.pop("src");
        final URL url = ClasspathUtils.getResource(src);
        if (url == null) {
            throw new IllegalAttributeException("src", new Exception("no such resource: " + src));
        }

        return new FileTreeItemFromURL(url);
    }
}
