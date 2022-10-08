package jp.unaguna.mappedftp.stub;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;

public class UserManagerStub implements UserManager {
    @Override
    public User getUserByName(String username) {
        return null;
    }

    @Override
    public String[] getAllUserNames() {
        return new String[0];
    }

    @Override
    public void delete(String username) {

    }

    @Override
    public void save(User user) {

    }

    @Override
    public boolean doesExist(String username) {
        return false;
    }

    @Override
    public User authenticate(Authentication authentication) {
        return null;
    }

    @Override
    public String getAdminName() {
        return null;
    }

    @Override
    public boolean isAdmin(String username) {
        return false;
    }
}
