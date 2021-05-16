package io.github.zhenbing.frpc.config;

import lombok.Data;

/**
 * ConsumerConfig
 *
 * @author fengzhenbing
 */
@Data
public class ConsumerConfig extends AbstractConfig {
    public static final String PREFIX = "consumer";

    public static final String PROXY_JDK = "jdk";

    public static final String PROXY_BYTE_BUDDY = "byteBuddy";

    /**
     * Networking framework client uses: netty, httpclient, etc.
     */
    private String client;

    /**
     * proxy: jdk is the default
     * byteBuddy
     */
    private String proxy;


    @Override
    public String getConfigPrefix() {
        return PREFIX;
    }


}
