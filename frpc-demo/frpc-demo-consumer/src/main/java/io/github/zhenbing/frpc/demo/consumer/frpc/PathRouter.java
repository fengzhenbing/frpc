package io.github.zhenbing.frpc.demo.consumer.frpc;

import io.github.zhenbing.frpc.core.api.Router;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PathRouter
 *
 * @author fengzhenbing
 */
@Component
public class PathRouter implements Router {
    @Override
    public List<String> route(List<String> urls) {
        //router todo
        return urls;
    }
}
