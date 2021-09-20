package io.github.zhenbing.frpc.demo.consumer.frpc;

import io.github.zhenbing.frpc.core.api.LoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RandomLoadbalance
 *
 * @author fengzhenbing
 */
@Slf4j
@Component
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public String select(List<String> urls) {
        int index = (int) (Math.random() * urls.size());
        String targetUrl = urls.get(index);
        log.info("select index -> {},url -> {}", index, targetUrl);
        return targetUrl;
    }
}
