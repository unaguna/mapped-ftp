package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.filesystem.tree.date.DateFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LastModifiedParser {
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[+-]?\\d+$");

    public DateFactory parse(String value) {
        Instant timestamp;

        final Matcher integerMatcher = INTEGER_PATTERN.matcher(value);
        if (integerMatcher.find()) {
            return DateFactory.constance(Long.parseLong(value));
        }

        timestamp = parseLocalDateTimeOrNull(value);
        if (timestamp != null) {
            return DateFactory.constance(timestamp);
        }

        timestamp = parseOffsetDateTimeOrNull(value);
        if (timestamp != null) {
            return DateFactory.constance(timestamp);
        }

        throw new IllegalArgumentException("cannot parse the last modified time: " + value);

    }

    private Instant parseLocalDateTimeOrNull(String value) {
        try {
            final LocalDateTime localDateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    .parse(value, LocalDateTime::from);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant();

        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Instant parseOffsetDateTimeOrNull(String value) {
        try {
            final OffsetDateTime offsetDateTime = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    .parse(value, OffsetDateTime::from);
            return offsetDateTime.toInstant();

        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
