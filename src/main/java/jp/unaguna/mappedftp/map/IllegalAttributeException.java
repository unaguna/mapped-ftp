package jp.unaguna.mappedftp.map;

public class IllegalAttributeException extends AttributeException {
    public IllegalAttributeException(String message) {
        super(message);
    }

    public IllegalAttributeException(String name, Throwable cause) {
        super("illegal attribute: " + name, cause);
    }
}
