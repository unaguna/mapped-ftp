package jp.unaguna.mappedftp.stub;

import org.apache.ftpserver.usermanager.PasswordEncryptor;

public class PasswordEncryptorStub implements PasswordEncryptor {
    @Override
    public String encrypt(String password) {
        throw new UnsupportedOperationException("stub");
    }

    @Override
    public boolean matches(String passwordToCheck, String storedPassword) {
        throw new UnsupportedOperationException("stub");
    }
}
