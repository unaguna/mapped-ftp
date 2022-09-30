package jp.unaguna.mappedftp.map;

import java.util.Collection;

public class UnknownAttributeException extends AttributeException {
    public UnknownAttributeException(Collection<String> names) {
        super("unknown attributes are found: " + String.join(", ", names));
    }
}
