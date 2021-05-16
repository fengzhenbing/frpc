package io.github.zhenbing.frpc.api;

import lombok.Data;

import java.util.*;

/**
 * FrpcRequest
 *
 * @author fengzhenbing
 */
@Data
public class FrpcRequest {
    private String serviceInterfaceClass;
    private String serviceImplClass;
    private String method;
    private Object[] params;
    private String url;
    private Map<String, String> headers = new LinkedHashMap();
}
