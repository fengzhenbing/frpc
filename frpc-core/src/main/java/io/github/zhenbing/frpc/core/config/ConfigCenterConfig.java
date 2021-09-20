package io.github.zhenbing.frpc.core.config;

import io.github.zhenbing.frpc.common.config.AbstractConfig;
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
