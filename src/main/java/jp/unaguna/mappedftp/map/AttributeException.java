package jp.unaguna.mappedftp.map;

public abstract class AttributeException extends Exception {
    public AttributeException(String message) {
        super(message);
    }

    public AttributeException(String message, Throwable cause) {
        super(message, cause);
    }
}
