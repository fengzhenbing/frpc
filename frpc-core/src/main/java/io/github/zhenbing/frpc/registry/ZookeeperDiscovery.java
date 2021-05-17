package io.github.zhenbing.frpc.registry;

import com.alibaba.fastjson.JSON;
import io.github.zhenbing.frpc.config.AbstractConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author fzb
 * @date 2021.05.18 00:30
 */
@Slf4j
public class ZookeeperDiscovery implements ServiceDiscovery {
    private final ZookeeperClient zookeeperClient;

    private Map<String,List<ServiceDesc>> serviceListMap = new ConcurrentHashMap<>(256);

    public ZookeeperDiscovery(final ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
        initServiceMap();
        subscribeChange();
    }


    @SneakyThrows
    private void initServiceMap() {
        CuratorFramework client = zookeeperClient.getClient();
        List<String> interfaceNameList =  client.getChildren().forPath("/");
        interfaceNameList.stream().forEach(interfaceName->{
            List<ServiceDesc> serviceDescList = new ArrayList<>();
            String rootPath = "/" + interfaceName;
            List<String> serviceList = null;
            try {
                serviceList = client.getChildren().forPath(rootPath);
                if (!CollectionUtils.isEmpty(serviceList)) {
                    serviceList.forEach(e -> {
                        String[] hostInfos = e.split("_");
                        String host = hostInfos[0];
                        Integer port = Integer.parseInt(hostInfos[1]);
                        ServiceDesc desc = ServiceDesc.builder().serviceInterfaceClass(interfaceName).host(host).port(port).build();
                        try {
                            ServiceDesc serviceInfo = JSON.parseObject(client.getData().forPath(rootPath + "/" + e), ServiceDesc.class);
                            desc.setServiceImplClass(serviceInfo.getServiceImplClass());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        serviceDescList.add(desc);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            serviceListMap.put(interfaceName,serviceDescList);
        });
    }


    private void subscribeChange() {
       /* CuratorFramework client = zookeeperClient.getClient();
        //创建PathChildrenCache
        //参数：true代表缓存数据到本地
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,"/" + testPath,true);
        //BUILD_INITIAL_CACHE 代表使用同步的方式进行缓存初始化。
        pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        pathChildrenCache.getListenable().addListener((cf, event) -> {
            PathChildrenCacheEvent.Type eventType = event.getType();
            switch (eventType) {
                case CONNECTION_RECONNECTED:
                    pathChildrenCache.rebuild();
                    break;
                case CONNECTION_SUSPENDED:
                    break;
                case CONNECTION_LOST:
                    System.out.println("Connection lost");
                    break;
                case CHILD_ADDED:
                    System.out.println("Child added");
                    break;
                case CHILD_UPDATED:
                    System.out.println("Child updated");
                    break;
                case CHILD_REMOVED:
                    System.out.println("Child removed");
                    break;
                default:
            }
        });*/
    }

    @Override
    public List<ServiceDesc> loadServiceDescList(String interfaceName) {
        return serviceListMap.get(interfaceName);
    }
}
