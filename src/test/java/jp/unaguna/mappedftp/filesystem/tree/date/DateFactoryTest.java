package jp.unaguna.mappedftp.filesystem.tree.date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateFactoryTest {
    @ParameterizedTest
    @ValueSource(longs = {0L, 1666501478000L})
    public void testConstance__from_long(Long value) {
        final DateFactory dateFactory = DateFactory.constance(value);

        assertEquals(value, dateFactory.getLong());
    }

    @Test
    public void testEachTime() throws InterruptedException {
        final DateFactory dateFactory = DateFactory.eachTime();

        final long actual1 = dateFactory.getLong();
        Thread.sleep(5);
        final long actual2 = dateFactory.getLong();
        Thread.sleep(5);
        final long actual3 = dateFactory.getLong();
        Thread.sleep(5);
        final long actual4 = dateFactory.getLong();
        Thread.sleep(5);
        final long actual5 = dateFactory.getLong();

        assertTrue(actual1 < actual2);
        assertTrue(actual2 < actual3);
        assertTrue(actual3 < actual4);
        assertTrue(actual4 < actual5);
    }
}
