package jp.unaguna.mappedftp.filesystem.tree.date;

import java.time.Instant;

public interface DateFactory {
    long getLong();

    /**
     * Returns a factory which create fixed time.
     *
     * @param value the fixed time
     * @return a factory whose creation method returns the fixed time
     */
    static DateFactory constance(long value) {
        return new ConstanceDateFactory(value);
    }

    /**
     * Returns a factory which create fixed time.
     *
     * @param value the fixed time
     * @return a factory whose creation method returns the fixed time
     */
    static DateFactory constance(Instant value) {
        return constance(value.toEpochMilli());
    }

    /**
     * Returns a factory which creates timestamps each time.
     *
     * @return a factory whose creation method returns the time when the creation method is called
     */
    static DateFactory eachTime() {
        return new CurrentDateFactory();
    }
}
