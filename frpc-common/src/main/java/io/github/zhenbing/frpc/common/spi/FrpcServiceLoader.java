package io.github.zhenbing.frpc.common.spi;

import io.github.zhenbing.frpc.common.spi.exception.ServiceLoaderException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FrpcServiceLoader
 *
 * @author fengzhenbing
 */
public class FrpcServiceLoader {
    private static final Map<Class<?>, Collection<Object>> SERVICES = new ConcurrentHashMap<>();

    private FrpcServiceLoader() {
    }

    public static void register(final Class<?> serviceInterface) {
        if (!SERVICES.containsKey(serviceInterface)) {
            SERVICES.put(serviceInterface, load(serviceInterface));
        }
    }

    private static <T> Collection<Object> load(final Class<T> serviceInterface) {
        Collection<Object> result = new LinkedList<>();
        for (T each : ServiceLoader.load(serviceInterface)) {
            result.add(each);
        }
        return result;
    }

    public static <T> Collection<T> getSingletonServiceInstances(final Class<T> service) {
        Collection collection = SERVICES.getOrDefault(service, Collections.emptyList());
        if (CollectionUtils.isEmpty(collection)) {
            throw new ServiceLoaderException(String.format("Can not load for SPI class `%s`, please check if the dependency is specified in pom.xml", service.getName()));
        }
        return collection;
    }

}
