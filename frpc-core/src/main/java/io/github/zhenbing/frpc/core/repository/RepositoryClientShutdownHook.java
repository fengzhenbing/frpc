package io.github.zhenbing.frpc.core.repository;

import io.github.zhenbing.frpc.repository.common.RepositoryClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FrpcShutdownHook
 *
 * @author fengzhenbing
 */
@Slf4j
public class RepositoryClientShutdownHook {

    private static String hookNamePrefix = "FrpcClientShutdownHook";
    private static AtomicInteger hookId = new AtomicInteger(0);
    private static Properties props;


    public static void addHook(final RepositoryClient client, final Properties props) {
        String name = hookNamePrefix + "-" + hookId.incrementAndGet();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.close();
        }, name));
        log.info("Add hook {}", name);
        RepositoryClientShutdownHook.props = props;
    }

}
