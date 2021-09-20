package io.github.zhenbing.frpc.core.annotation;

import lombok.Builder;
import lombok.Data;

/**
 * ReferencedDesc
 *
 * @author fengzhenbing
 */
@Data
@Builder
public class ReferencedDesc {
    /**
     * Interface class, default value is void.class
     */
    private Class<?> interfaceClass;

    /**
     * Interface class name, default value is empty string
     */
    private String interfaceName;

    /**
     * Service version, default value is empty string
     */
    private String version;

    /**
     * Loadbalancer type
     */
    private  String loadBalancer;

    public static ReferencedDesc buildFromReferenced(Referenced referenced) {
       return ReferencedDesc.builder()
                .interfaceClass(referenced.interfaceClass())
                .interfaceName(referenced.interfaceName())
                .loadBalancer(referenced.loadBalancer())
                .version(referenced.version())
                .build();
    }
}
