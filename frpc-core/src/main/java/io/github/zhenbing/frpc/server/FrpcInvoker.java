package io.github.zhenbing.frpc.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.github.zhenbing.frpc.api.FrpcException;
import io.github.zhenbing.frpc.api.FrpcRequest;
import io.github.zhenbing.frpc.api.FrpcResolver;
import io.github.zhenbing.frpc.api.FrpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * FrpcInvoker
 *
 * @author fengzhenbing
 */
public class FrpcInvoker {
    private FrpcResolver resolver;

    public FrpcInvoker(FrpcResolver resolver) {
        this.resolver = resolver;
    }

    public FrpcResponse invoke(FrpcRequest request) {
        FrpcResponse response = new FrpcResponse();
        String serviceClass = request.getServiceImplClass();

        // this.applicationContext.getBean(serviceClass);
        Object service = resolver.resolve(serviceClass);

        try {
            Method method = resolveMethodFromClass(service.getClass(), request.getMethod());
            // dubbo, fastjson,
            Object result = method.invoke(service, request.getParams());
            // 两次json序列化能否合并成一个
            // response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            response.setResult(JSON.toJSONString(result));
            response.setStatus(true);
            return response;
        } catch (IllegalAccessException | InvocationTargetException e) {

            // 3.Xstream

            // 2.封装一个统一的FrpcException
            // 客户端也需要判断异常
            e.printStackTrace();
            response.setException(new FrpcException(e));
            response.setStatus(false);
            return response;
        }
    }

    private Method resolveMethodFromClass(Class<?> klass, String methodName) {
        return Arrays.stream(klass.getMethods()).filter(m -> methodName.equals(m.getName())).findFirst().get();
    }
}
