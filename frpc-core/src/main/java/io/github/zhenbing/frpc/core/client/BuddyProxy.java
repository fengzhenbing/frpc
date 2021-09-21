package io.github.zhenbing.frpc.core.client;

import com.alibaba.fastjson.JSON;
import io.github.zhenbing.frpc.core.annotation.ReferencedDesc;
import io.github.zhenbing.frpc.core.spi.FrpcResponse;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import io.github.zhenbing.frpc.core.spi.Filter;
import io.github.zhenbing.frpc.core.spi.FrpcRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * BuddyProxy
 *
 * @author fengzhenbing
 */
public class BuddyProxy implements FrpcProxy {

    private static final NetClient defaultNetClient = new OkHttpClient();

    @Override
    public <T> T create(final ApplicationContext applicationContext, ReferencedDesc referencedDesc) {
        try {
            return (T) new ByteBuddy().subclass(Object.class)
                    .implement(referencedDesc.getInterfaceClass())
                    .method(ElementMatchers.isDeclaredBy(referencedDesc.getInterfaceClass()))
                    .intercept(InvocationHandlerAdapter.of(new BuddyInvocationHandler(applicationContext, referencedDesc)))
                    .make()
                    .load(getClass().getClassLoader())
                    .getLoaded()
                    .newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class BuddyInvocationHandler implements InvocationHandler {

        private final ReferencedDesc referencedDesc;
        private final ApplicationContext applicationContext;

        public <T> BuddyInvocationHandler(ApplicationContext applicationContext, ReferencedDesc referencedDesc) {
            this.referencedDesc = referencedDesc;
            this.applicationContext = applicationContext;
        }

        // 可以尝试，自己去写对象序列化，二进制还是文本的，，，frpc是xml自定义序列化、反序列化，json: code.google.com/p/frpc
        // int byte char float double long bool
        // [], data class

        @Override
        public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {

            // 加filter地方之二
            // mock == true, new Student("test");
            ServiceDesc serviceDesc = Frpc.getServiceProviderDesc(applicationContext, referencedDesc);

            FrpcRequest request = Frpc.buildFrpcRequest(referencedDesc.getInterfaceClass(), method, params, serviceDesc);

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

            return JSON.parseObject(response.getResult().toString(), method.getReturnType());
        }


    }
}
