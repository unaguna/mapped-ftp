package jp.unaguna.mappedftp.filesystem.tree.date;

public interface DateFactory {
    long getLong();

    static DateFactory constance(long value) {
        return new ConstanceDateFactory(value);
    }

    static DateFactory eachTime() {
        return new CurrentDateFactory();
    }
}
