package jp.unaguna.mappedftp.filesystem.tree.date;

public class ConstanceDateFactory implements DateFactory {
    private final long value;

    public ConstanceDateFactory(long value) {
        this.value = value;
    }

    @Override
    public long getLong() {
        return value;
    }
}
