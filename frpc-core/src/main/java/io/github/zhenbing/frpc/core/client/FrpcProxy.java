package io.github.zhenbing.frpc.core.client;

import io.github.zhenbing.frpc.core.annotation.ReferencedDesc;
import org.springframework.context.ApplicationContext;

/**
 * FrpcProxy
 *
 * @author fengzhenbing
 */
public interface FrpcProxy {
    /**
     * 代理
     *
     * @param applicationContext
     * @param referencedDesc
     * @param <T>
     * @param referencedDesc
     * @return
     */
    <T> T create(ApplicationContext applicationContext, final ReferencedDesc referencedDesc);
}
