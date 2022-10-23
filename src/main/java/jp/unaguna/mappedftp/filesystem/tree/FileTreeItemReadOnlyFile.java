package jp.unaguna.mappedftp.filesystem.tree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileTreeItemReadOnlyFile implements FileTreeItem {
    private String ownerName = null;
    private String groupName = null;

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public OutputStream createOutputStream(long offset) {
        // TODO: throws exception
        return null;
    }

    @Override
    public abstract InputStream createInputStream(long offset) throws IOException;
}
