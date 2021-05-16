package io.github.zhenbing.frpc.api;

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
public class ServiceProviderDesc {

    private String host;
    private Integer port;
    private String serviceInterfaceClass;
    private String serviceImplClass;

    // group
    // version

    public String httpUrl() {
        return "http://" + host + ":" + port;
    }
}
