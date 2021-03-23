package io.github.zhenbing.frpc.registry;

import com.alibaba.fastjson.JSON;
import io.github.zhenbing.frpc.api.ServiceProviderDesc;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import io.github.zhenbing.frpc.config.AbstractConfig;
import io.github.zhenbing.frpc.config.RegistryConfig;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ZookeeperRegistryClient
 *
 * @author fengzhenbing
 */
@Slf4j
public class ZookeeperRegistryClient implements RegistryClient {

    private CuratorFramework client;

    public ZookeeperRegistryClient(RegistryConfig registryConfig){
        // start zk client
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(registryConfig.getHost()+":"+registryConfig.getPort())
                .namespace(AbstractConfig.FRPC)
                .retryPolicy(retryPolicy).build();
        client.start();
    }

    @Override
    public void registerService(ServiceProviderDesc serviceProviderDesc) {
        try {
            if ( null == client.checkExists().forPath("/" + serviceProviderDesc.getServiceInterfaceClass())) {
                client.create().withMode(CreateMode.PERSISTENT).forPath("/" + serviceProviderDesc.getServiceInterfaceClass(), serviceProviderDesc.getServiceImplClass().getBytes());
            }
            client.create().withMode(CreateMode.EPHEMERAL).
                    forPath( "/" + serviceProviderDesc.getServiceInterfaceClass() + "/" + serviceProviderDesc.getHost() + "_" + serviceProviderDesc.getPort(), JSON.toJSONBytes(serviceProviderDesc));

        } catch (Exception ex) {
            log.error("ex ->",ex);
            ex.printStackTrace();
        }
    }

    @Override
    public List<ServiceProviderDesc> loadServiceProviderDescList(String className) {
        List<ServiceProviderDesc> serviceProviderDescList = new ArrayList<>();
        try {
            String rootPath = "/"+className;
           List<String> list = client.getChildren().forPath(rootPath);
           if(!CollectionUtils.isEmpty(list)){
               list.forEach(e->{
                   String[] hostInfos = e.split("_");
                   String host = hostInfos[0];
                   Integer port = Integer.parseInt(hostInfos[1]);
                   ServiceProviderDesc desc = ServiceProviderDesc.builder().serviceInterfaceClass(className).host(host).port(port).build();
                   try {
                        ServiceProviderDesc serviceInfo =   JSON.parseObject(client.getData().forPath(rootPath+"/"+e),ServiceProviderDesc.class);
                       desc.setServiceImplClass(serviceInfo.getServiceImplClass());
                   } catch (Exception ex) {
                       ex.printStackTrace();
                   }

                   serviceProviderDescList.add(desc);
               });
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceProviderDescList;
    }
}
