package io.github.zhenbing.frpc.demo.provider;


import io.github.zhenbing.frpc.api.FrpcResponse;
import io.github.zhenbing.frpc.annotation.EnableFrpc;
import io.github.zhenbing.frpc.api.FrpcRequest;
import io.github.zhenbing.frpc.api.FrpcResolver;
import io.github.zhenbing.frpc.demo.provider.frpc.SpringContextFrpcResolver;
import io.github.zhenbing.frpc.server.FrpcInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * FrpcProviderApplication
 *
 * @author fengzhenbing
 */
@SpringBootApplication
@EnableFrpc
@RestController
public class FrpcProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrpcProviderApplication.class);
    }

    @Autowired
    FrpcInvoker invoker;

    @PostMapping("/")
    public FrpcResponse invoke(@RequestBody FrpcRequest request) {
        return invoker.invoke(request);
    }

    @Bean
    public FrpcInvoker createInvoker(@Autowired FrpcResolver resolver) {
        return new FrpcInvoker(resolver);
    }

    @Bean
    public FrpcResolver createResolver() {
        return new SpringContextFrpcResolver();
    }
}
