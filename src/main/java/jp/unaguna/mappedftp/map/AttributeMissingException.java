package jp.unaguna.mappedftp.map;

public class AttributeMissingException extends AttributeException {
    public final String name;

    public AttributeMissingException(String name) {
        super("attribute is missing: " + name);
        this.name = name;
    }
}
