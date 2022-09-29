package jp.unaguna.mappedftp.filesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TreePath {
    private static final String SEPARATOR = "/";
    private static final TreePath ROOT = new TreePath(true, new String[0]);
    private final boolean absolute;
    private final String[] names;

    TreePath(boolean isAbsolute, String[] names) {
        this.absolute = isAbsolute;
        this.names = names;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public TreePath getRoot() {
        return ROOT;
    }

    public TreePath getFileName() {
        if (names.length == 0) {
            return new TreePath(false, names);
        } else {
            String[] newNames = new String[1];
            newNames[0] = names[names.length - 1];
            return new TreePath(false, newNames);
        }
    }

    public TreePath getParent() {
        if (this.names.length == 0) {
            return null;
        } else {
            return this.subpath(0, getNameCount()-1);
        }
    }

    public int getNameCount() {
        return this.names.length;
    }

    public TreePath getName(int index) {
        String[] newNames = new String[1];
        newNames[0] = this.names[index];
        return new TreePath(false, newNames);
    }

    public TreePath subpath(int beginIndex, int endIndex) {
        if (beginIndex > endIndex) {
            // TODO: error message
            throw new IllegalArgumentException();
        }

        String[] newNames = new String[endIndex - beginIndex];
        System.arraycopy(this.names, beginIndex, newNames, 0, endIndex - beginIndex);

        return new TreePath(beginIndex == 0 && this.absolute, newNames);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        if (this.isAbsolute()) {
            builder.append(SEPARATOR);
        }

        if (this.names.length == 0) {
            return builder.toString();
        }

        builder.append(this.names[0]);

        for (int i=1; i<names.length; i++) {
            final String name = names[i];
            builder.append(SEPARATOR);
            builder.append(name);
        }

        return builder.toString();
    }

    public static TreePath get(String ... names) {
        final boolean absolute = names.length > 0 && names[0].startsWith(SEPARATOR);
        final List<String> nameList = new ArrayList<>();

        for (String name : names) {
            List<String> nameParts = Arrays.stream(name.split(SEPARATOR))
                    .filter(s -> s.length() > 0)
                    .collect(Collectors.toList());
            nameList.addAll(nameParts);
        }

        final String[] newNames = nameList.toArray(new String[0]);

        return new TreePath(absolute, newNames);
    }
}
