package jp.unaguna.mappedftp.encrypt;

import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;

import java.lang.reflect.Constructor;

public enum PasswordEncryptorType {
    CLEAR(ClearTextPasswordEncryptor.class),
    MD5(Md5PasswordEncryptor.class),
    SALTED(SaltedPasswordEncryptor.class),
    ;

    private final Class<? extends PasswordEncryptor> passwordEncryptor;
    PasswordEncryptorType(Class<? extends PasswordEncryptor> cls) {
        passwordEncryptor = cls;
    }

    public PasswordEncryptor constructPasswordEncryptor() {
        try {
            Constructor<? extends PasswordEncryptor> constructor = passwordEncryptor.getConstructor();
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the enum constant of this type with the specified name.
     * The string must match exactly an identifier used to declare an enum constant in this type.
     * (Extraneous whitespace characters are not permitted.)
     *
     * @return the enum constant with the specified name
     * @throws IllegalArgumentException if this enum type has no constant with the specified name
     */
    public static PasswordEncryptorType of(String value) {
        return PasswordEncryptorType.valueOf(value.toUpperCase());
    }
}
