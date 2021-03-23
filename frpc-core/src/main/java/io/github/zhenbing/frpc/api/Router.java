package io.github.zhenbing.frpc.api;

import java.util.List;

/**
 * Router
 *
 * @author fengzhenbing
 */
public interface Router {
    List<String> route(List<String> urls);
}
