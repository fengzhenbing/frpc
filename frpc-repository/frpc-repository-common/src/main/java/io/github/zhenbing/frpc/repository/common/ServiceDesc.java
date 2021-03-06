package io.github.zhenbing.frpc.repository.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * ServiceProviderDesc
 *
 * @author fengzhenbing
 */
@Data
@Builder
@AllArgsConstructor
public class ServiceDesc {

    private String host;
    private Integer port;
    private String serviceInterfaceClass;
    private String serviceImplClass;

    private String version;

    /**
     * 权重：负载时用
     */
    private Integer weight = 50;

    public String httpUrl() {
        return "http://" + host + ":" + port;
    }
}
