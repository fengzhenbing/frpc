package io.github.zhenbing.frpc.demo.consumer;

import io.github.zhenbing.frpc.annotation.EnableFrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FrpcConsumerApplication
 *
 * @author fengzhenbing
 */
@SpringBootApplication
@EnableFrpc
public class FrpcConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrpcConsumerApplication.class);
    }
}
