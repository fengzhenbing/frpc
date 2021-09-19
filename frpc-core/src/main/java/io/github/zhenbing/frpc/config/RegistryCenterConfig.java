package io.github.zhenbing.frpc.config;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * RegistryConfig
 *
 * @author fengzhenbing
 */
@Data
public class RegistryCenterConfig extends AbstractConfig {
    public static final String PREFIX = "registry";

    public static final String ZOOKEEPER = "zookeeper";

    public static final String NACOS = "nacos";

    private Properties props = new Properties();

    String PASSWORD_KEY = "password";

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
        if (!StringUtils.isEmpty(address)) {
            String url = address.split("//")[1];
            host = url.split(":")[0];
        }
        return host;
    }

    public String getPort() {
        String host = "";
        if (!StringUtils.isEmpty(address)) {
            String url = address.split("//")[1];
            host = url.split(":")[1];
        }
        return host;
    }
}
