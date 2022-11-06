package jp.unaguna.mappedftp.filesystem.tree.date;

import java.util.Date;

public class CurrentDateFactory implements DateFactory {
    @Override
    public long getLong() {
        return new Date().getTime();
    }
}
