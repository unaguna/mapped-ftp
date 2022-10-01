package jp.unaguna.mappedftp.filesystem;

import jp.unaguna.mappedftp.config.FileSystemFactoryFactory;
import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItem;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromURL;
import jp.unaguna.mappedftp.map.AttributeException;
import jp.unaguna.mappedftp.map.AttributeHashMap;
import jp.unaguna.mappedftp.map.IllegalAttributeException;
import jp.unaguna.mappedftp.map.UnknownAttributeException;
import org.apache.ftpserver.ftplet.FileSystemFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReadOnlyFileSystemFactoryFactory extends FileSystemFactoryFactory {
    @Override
    protected FileSystemFactory createFileSystemFactory(ServerConfig config) throws AttributeException {
        final Map<String, FileTreeItem> files = new LinkedHashMap<>();

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

        return new ReadOnlyFileSystemFactory(files);
    }

    private FileTreeItem createFileTreeItem(AttributeHashMap fileAttributes) throws AttributeException {
        // TODO: このメソッドごと依存性注入できるように FileTreeItemFactory を作る

        final String type = fileAttributes.pop("type");
        switch (type) {
            case "url":
                return createFileTreeItemFromURL(fileAttributes);
            default:
                throw new IllegalAttributeException("unknown type is specified: type=\"" + type + "\"");
        }
    }

    private FileTreeItem createFileTreeItemFromURL(AttributeHashMap fileAttributes) throws AttributeException {

        // load attributes
        final URL url;
        try {
            url = new URL(fileAttributes.pop("src"));
        } catch (MalformedURLException e) {
            throw new IllegalAttributeException("src", e);
        }

        return new FileTreeItemFromURL(url);
    }
}
