package io.github.zhenbing.frpc.config;

import lombok.Data;

/**
 * ConfigCenterBean
 *
 * @author fengzhenbing
 */
@Data
public class ConfigCenterConfig extends AbstractConfig {
    public static final String PREFIX = "config-center";
    private String url;


    @Override
    public String getConfigPrefix() {
        return PREFIX;
    }
}
