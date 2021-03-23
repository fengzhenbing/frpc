package io.github.zhenbing.frpc.config;

import lombok.Data;

/**
 * ApplicationConfig
 *
 * @author fengzhenbing
 */
@Data
public class ApplicationConfig extends AbstractConfig{
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
