package jp.unaguna.mappedftp.stub;

import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.user.ConfigurableUserManagerFactory;
import org.apache.ftpserver.ftplet.UserManager;

public class UserManagerFactoryStub implements ConfigurableUserManagerFactory {
    private final UserManager userManager;
    private ServerConfig appliedConfig = null;

    public UserManagerFactoryStub() {
        this.userManager = new UserManagerStub();
    }

    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public UserManager createUserManager() {
        return userManager;
    }

    @Override
    public boolean isConfigured() {
        return appliedConfig != null;
    }

    @Override
    public void applyConfig(ServerConfig serverConfig) {
        appliedConfig = serverConfig;
    }
}
