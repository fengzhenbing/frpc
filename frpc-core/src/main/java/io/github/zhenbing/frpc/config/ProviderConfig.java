package io.github.zhenbing.frpc.config;

import lombok.Data;

/**
 * ProviderConfig
 *
 * @author fengzhenbing
 */
@Data
public class ProviderConfig  extends AbstractConfig{
    public static final String PREFIX = "provider";
    /**
     * Service ip addresses (used when there are multiple network cards available)
     */
    private String host;

    /**
     * Service port
     */
    private Integer port;

    /**
     * Context path
     */
    private String contextPath;


    @Override
    public String getConfigPrefix() {
        return PREFIX;
    }
}
