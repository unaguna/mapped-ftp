package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.filesystem.LinkedFileNode;
import jp.unaguna.mappedftp.filesystem.LinkedFileSystemView;
import jp.unaguna.mappedftp.filesystem.TreePath;
import org.apache.ftpserver.ftplet.FtpFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.util.*;

/**
 * Concrete implementation of {@link LinkedFileNode}.
 * This class maintains links to a parent directory and child files.
 * It uses its tree structure to provide the functions required by {@link LinkedFileSystemView}.
 */
public class FileTreeNode implements LinkedFileNode {
    private FileTreeNode parent = null;
    private final String name;
    private final Map<String, FileTreeNode> children;
    private final FileTreeItem file;

    public FileTreeNode(FileTreeItem file, String name) {
        this.file = file;
        this.name = name;

        if (file.isDirectory()) {
            children = new HashMap<>();
        } else {
            children = null;
        }
    }

    public FileTreeItem getFile() {
        return file;
    }

    /**
     * 子孫要素を追加する。
     *
     * <p>
     * 子要素を追加する場合は、relativePath に長さが1 (ファイル名のみ) のパスを指定する。
     * 逆に長さが2以上のパスが指定された場合、this の下に指定されたパス通りにディレクトリを作成し、
     * 作成したディレクトリと fileTreeNode を追加する。
     * </p>
     *
     * @param file         追加する子孫要素
     * @param relativePath 追加する要素の this からの相対パス
     */
    public void appendSubFile(FileTreeItem file, TreePath relativePath) {
        if (relativePath.isAbsolute()) {
            throw new IllegalArgumentException("relativePath must not be absolute: " + relativePath);
        }
        if (relativePath.getNameCount() == 0) {
            throw new IllegalArgumentException("illegal child path: " + relativePath);
        }

        final String childName = relativePath.getName(0).toString();

        // このディレクトリのすぐ下にファイルを置く場合
        if (relativePath.getNameCount() == 1) {
            FileTreeNode fileTreeNode = new FileTreeNode(file, childName);
            this.addChild(fileTreeNode, childName);
        }
        // このディレクトリよりも下のディレクトリにファイルを置く場合
        else {
            FileTreeNode childNode = children.get(childName);
            if (childNode == null) {
                childNode = new FileTreeNode(new FileTreeItemDirectory(), childName);
                this.addChild(childNode, childName);
            }

            childNode.appendSubFile(file, relativePath.subpath(1));
        }
    }

    public void addChild(FileTreeNode childNode, String childName) {
        this.children.put(childName, childNode);
        childNode.parent = this;
    }

    @Override
    public FileTreeNode getNodeByRelativePath(TreePath relativePath) throws NotDirectoryException, NoSuchFileException {
        return getNodeByRelativePath(relativePath, relativePath);
    }

    public FileTreeNode getNodeByRelativePath(TreePath relativePath, TreePath originalRelativePath)
            throws NotDirectoryException, NoSuchFileException {
        if (relativePath.isAbsolute()) {
            throw new IllegalArgumentException("relativePath must not be absolute: " + relativePath);
        }
        if (relativePath.getNameCount() == 0) {
            return this;
        }
        if (relativePath.getName(0).toString().equals(".")) {
            return this.getNodeByRelativePath(relativePath.subpath(1), originalRelativePath);
        }
        if (relativePath.getName(0).toString().equals("..")) {
            if (parent != null) {
                return parent.getNodeByRelativePath(relativePath.subpath(1), originalRelativePath);
            } else {
                return this.getNodeByRelativePath(relativePath.subpath(1), originalRelativePath);
            }
        }
        if (this.children == null) {
            throw new NotDirectoryException(this.getAbsolutePath());
        }

        final String childName = relativePath.getName(0).toString();
        final FileTreeNode childNode = this.children.get(childName);

        if (childNode == null) {
            throw new NoSuchFileException(originalRelativePath.toString());
        }

        return childNode.getNodeByRelativePath(relativePath.subpath(1), originalRelativePath);
    }

    @Override
    public String getAbsolutePath() {
        if (parent == null) {
            return "/";
        } else {
            final StringBuilder pathBuilder = new StringBuilder();
            this.getAbsolutePath(pathBuilder);
            return pathBuilder.toString();
        }
    }

    private void getAbsolutePath(StringBuilder pathBuilder) {
        if (parent != null) {
            parent.getAbsolutePath(pathBuilder);
            pathBuilder.append("/");
            pathBuilder.append(name);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public boolean isFile() {
        return file.isRegularFile();
    }

    @Override
    public boolean doesExist() {
        return true;
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @Override
    public String getOwnerName() {
        return null;
    }

    @Override
    public String getGroupName() {
        return null;
    }

    @Override
    public int getLinkCount() {
        return 0;
    }

    @Override
    public long getLastModified() {
        return new Date().getTime();
    }

    @Override
    public boolean setLastModified(long time) {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public Object getPhysicalFile() {
        return null;
    }

    @Override
    public boolean mkdir() {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public boolean move(FtpFile destination) {
        return false;
    }

    @Override
    public List<? extends FtpFile> listFiles() {
        if (this.children == null) return null;

        return new ArrayList<>(this.children.values());
    }

    @Override
    public OutputStream createOutputStream(long offset) throws IOException {
        return file.createOutputStream(offset);
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        return file.createInputStream(offset);
    }
}
