package io.github.zhenbing.frpc.repository.common;

import io.github.zhenbing.frpc.common.config.AbstractConfig;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * RegistryConfig
 *
 * @author fengzhenbing
 */
@Data
public class RepositoryConfig extends AbstractConfig {
    public static final String PREFIX = "repository";

    private Properties props = new Properties();

    String PASSWORD_KEY = "password";

    private String type = "zookeeper";
    /**
     * Register center address
     */
    private String address;

    /**
     * Username to login register center
     */
    private String username;

    /**
     * Password to login register center
     */
    private String password;

    public void setPassword(String password) {
        checkLength(PASSWORD_KEY, password);
        this.password = password;
    }

    @Override
    public String getConfigPrefix() {
        return PREFIX;
    }

    public String getHost() {
        String host = "";
        if (StringUtils.isNotEmpty(address)) {
            String url = address.split("//")[1];
            host = url.split(":")[0];
        }
        return host;
    }

    public String getPort() {
        String host = "";
        if (StringUtils.isNotEmpty(address)) {
            String url = address.split("//")[1];
            host = url.split(":")[1];
        }
        return host;
    }
}
