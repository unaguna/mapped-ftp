package jp.unaguna.mappedftp;

public class NoSuchTestResourceException extends RuntimeException {
    public NoSuchTestResourceException(String path) {
        super("The test resource is not found: " + path);
    }
}
