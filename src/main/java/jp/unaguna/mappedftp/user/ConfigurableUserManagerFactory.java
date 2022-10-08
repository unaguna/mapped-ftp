package jp.unaguna.mappedftp.user;

import jp.unaguna.mappedftp.config.ConfigException;
import jp.unaguna.mappedftp.config.ServerConfig;
import jp.unaguna.mappedftp.map.AttributeException;
import org.apache.ftpserver.usermanager.UserManagerFactory;

public interface ConfigurableUserManagerFactory extends UserManagerFactory {
    /**
     * Returns whether {@link #applyConfig(ServerConfig)} has already been called.
     */
    boolean isConfigured();
    void applyConfig(ServerConfig serverConfig) throws AttributeException, ConfigException;
}
