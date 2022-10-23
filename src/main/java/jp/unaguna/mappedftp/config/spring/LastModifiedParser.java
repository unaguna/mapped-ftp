package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.filesystem.tree.date.DateFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LastModifiedParser {
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[+-]?\\d+$");

    public DateFactory parse(String value) {
        final Matcher integerMatcher = INTEGER_PATTERN.matcher(value);
        if (integerMatcher.find()) {
            return DateFactory.constance(Long.parseLong(value));
        } else {
            throw new IllegalArgumentException("cannot parse the last modified time: " + value);
        }
    }
}
