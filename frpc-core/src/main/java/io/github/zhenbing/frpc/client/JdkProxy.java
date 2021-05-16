package io.github.zhenbing.frpc.client;

import com.alibaba.fastjson.JSON;
import io.github.zhenbing.frpc.api.Filter;
import io.github.zhenbing.frpc.api.FrpcRequest;
import io.github.zhenbing.frpc.api.FrpcResponse;
import io.github.zhenbing.frpc.api.ServiceProviderDesc;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JdkProxy
 *
 * @author fengzhenbing
 */
public class JdkProxy implements FrpcProxy {
    private static final NetClient defaultNetClient = new OkHttpClient();

    @Override
    public <T> T create(final ApplicationContext applicationContext, final Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(Frpc.class.getClassLoader(), new Class[]{serviceClass}, new FrpcInvocationHandler(applicationContext, serviceClass));
    }

    public static class FrpcInvocationHandler implements InvocationHandler {

        private final Class<?> serviceClass;
        private final ApplicationContext applicationContext;

        public <T> FrpcInvocationHandler(ApplicationContext applicationContext, Class<T> serviceClass) {
            this.serviceClass = serviceClass;
            this.applicationContext = applicationContext;
        }

        // 可以尝试，自己去写对象序列化，二进制还是文本的，，，frpc是xml自定义序列化、反序列化，json: code.google.com/p/frpc
        // int byte char float double long bool
        // [], data class

        @Override
        public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {

            // 加filter地方之二
            // mock == true, new Student("hubao");
            ServiceProviderDesc serviceProviderDesc = Frpc.getServiceProviderDesc(applicationContext, serviceClass);

            FrpcRequest request = Frpc.buildFrpcRequest(serviceClass, method, params, serviceProviderDesc);

            Filter[] filters = Frpc.getFilters(applicationContext);
            if (null != filters) {
                //sort
                OrderComparator.sort(filters);

                for (Filter filter : filters) {
                    if (!filter.filter(request)) {
                        return null;
                    }
                }
            }

            FrpcResponse response = defaultNetClient.sendRequest(request);

            // 加filter地方之三
            // Student.setTeacher("cuijing");

            // 这里判断response.status，处理异常
            // 考虑封装一个全局的FrpcException

            return JSON.parse(response.getResult().toString());
        }


    }
}
