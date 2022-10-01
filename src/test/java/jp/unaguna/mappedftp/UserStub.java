package jp.unaguna.mappedftp;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;

import java.util.List;

public class UserStub implements User {
    private final String name;

    public UserStub() {
        this("anonymous");
    }

    public UserStub(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public List<? extends Authority> getAuthorities() {
        return null;
    }

    @Override
    public List<? extends Authority> getAuthorities(Class<? extends Authority> clazz) {
        return null;
    }

    @Override
    public AuthorizationRequest authorize(AuthorizationRequest request) {
        return null;
    }

    @Override
    public int getMaxIdleTime() {
        return 0;
    }

    @Override
    public boolean getEnabled() {
        return true;
    }

    @Override
    public String getHomeDirectory() {
        return "/";
    }
}
