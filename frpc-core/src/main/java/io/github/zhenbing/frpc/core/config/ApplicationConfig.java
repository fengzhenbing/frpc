package io.github.zhenbing.frpc.core.config;

import io.github.zhenbing.frpc.common.config.AbstractConfig;
import lombok.Data;

/**
 * ApplicationConfig
 *
 * @author fengzhenbing
 */
@Data
public class ApplicationConfig extends AbstractConfig {
    public static final String PREFIX = "application";
    /**
     * Application name
     */
    private String name;


    @Override
    public String getConfigPrefix() {
        return PREFIX;
    }
}
