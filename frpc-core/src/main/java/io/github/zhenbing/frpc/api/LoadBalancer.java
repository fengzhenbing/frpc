package io.github.zhenbing.frpc.api;

import java.util.List;

/**
 * LoadBalancer
 *
 * @author fengzhenbing
 */
public interface LoadBalancer {
    String select(List<String> urls);
}
