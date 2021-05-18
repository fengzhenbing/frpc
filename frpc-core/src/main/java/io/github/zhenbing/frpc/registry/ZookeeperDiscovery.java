package io.github.zhenbing.frpc.registry;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author fzb
 * @date 2021.05.18 00:30
 */
@Slf4j
public class ZookeeperDiscovery implements ServiceDiscovery {
    private final ZookeeperClient zookeeperClient;

    private Map<String, List<ServiceDesc>> serviceListMap = new ConcurrentHashMap<>(256);

    public ZookeeperDiscovery(final ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
        initServiceMap();//TODO 考滤下
        subscribeChange();
    }


    @SneakyThrows
    private void initServiceMap() {
        CuratorFramework client = zookeeperClient.getClient();
        List<String> interfaceNameList = client.getChildren().forPath("/");
        for (String interfaceName : interfaceNameList) {
            List<ServiceDesc> serviceDescList = new ArrayList<>();
            String rootPath = "/" + interfaceName;
            List<String> serviceList;
            try {
                serviceList = client.getChildren().forPath(rootPath);
                if (!CollectionUtils.isEmpty(serviceList)) {
                    serviceList.forEach(e -> {
                        byte[] data = new byte[0];
                        try {
                            data = client.getData().forPath(rootPath + "/" + e);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        ServiceDesc desc = buildServiceDesc(data, e);

                        serviceDescList.add(desc);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            serviceListMap.put(interfaceName, serviceDescList);
        }
    }


    private void subscribeChange() {
        CuratorFramework curatorFramework = zookeeperClient.getClient();

        CuratorCache curatorCache = CuratorCache.builder(curatorFramework, "/").build();
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder().forPathChildrenCache("/", curatorFramework, new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) {
                PathChildrenCacheEvent.Type eventType = pathChildrenCacheEvent.getType();

                switch (eventType) {
                    case CONNECTION_RECONNECTED:
                        log.info("Connection reconnect");
                        break;
                    case CONNECTION_SUSPENDED:
                        log.info("Connection suspended");
                        break;
                    case CONNECTION_LOST:
                        log.info("Connection lost, path:{}", pathChildrenCacheEvent.getData().getPath());
                        break;
                    case CHILD_ADDED:
                        log.info("added, path:{}", pathChildrenCacheEvent.getData().getPath());
                        handleAddOrUpdateNode(pathChildrenCacheEvent);
                        break;
                    case CHILD_UPDATED:
                        log.info("updated, path:{}", pathChildrenCacheEvent.getData().getPath());
                        handleAddOrUpdateNode(pathChildrenCacheEvent);
                        break;
                    case CHILD_REMOVED:
                        String path = pathChildrenCacheEvent.getData().getPath();
                        log.info("removed, path:{}", pathChildrenCacheEvent.getData().getPath());
                        handleRemoveNode(path);
                        break;
                    default:
                }
            }
        }).build();
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();
    }

    private void handleRemoveNode(String path) {
        String[] pathItems = path.split("/");
        int level = pathItems.length - 1;
        String leafPath = pathItems[level];
        if (leafPath.contains("_")) {
            String[] hostInfos = leafPath.split("_");
            String host = hostInfos[0];
            Integer port = Integer.parseInt(hostInfos[1]);
           String interfaceName =  pathItems[level-1];
           List<ServiceDesc> serviceDescList = serviceListMap.get(interfaceName);
            serviceDescList = serviceDescList.stream().filter(e->!(e.getPort().equals(port)&&e.getHost().equals(host))).collect(Collectors.toList());
            serviceListMap.put(interfaceName, serviceDescList);
        }
    }

    private void handleAddOrUpdateNode(PathChildrenCacheEvent pathChildrenCacheEvent) {
        String path = pathChildrenCacheEvent.getData().getPath();
        byte[] data = pathChildrenCacheEvent.getData().getData();
        String[] pathItems = path.split("/");
        int level = pathItems.length - 1;
        String leafPath = pathItems[level];
        if (leafPath.contains("_")) {
           ServiceDesc serviceDesc = buildServiceDesc(data, leafPath);
           String interfaceName = serviceDesc.getServiceInterfaceClass();
            List<ServiceDesc> serviceDescList = serviceListMap.get(interfaceName);
            if(CollectionUtils.isEmpty(serviceDescList)){
                serviceListMap.put(interfaceName, Lists.newArrayList(serviceDesc));
            }else {
                serviceDescList = serviceDescList.stream().filter(e->!(e.getHost().equals(serviceDesc.getHost())&&e.getPort().equals(serviceDesc.getPort()))).collect(Collectors.toList());
                serviceDescList.add(serviceDesc);
                serviceListMap.put(interfaceName,serviceDescList);
            }
        }
    }

    private ServiceDesc buildServiceDesc(byte[] data, String leafPath) {
        String[] hostInfos = leafPath.split("_");
        String host = hostInfos[0];
        Integer port = Integer.parseInt(hostInfos[1]);
        ServiceDesc desc = ServiceDesc.builder().host(host).port(port).build();
        try {
            ServiceDesc serviceInfo = JSON.parseObject(data, ServiceDesc.class);
            desc.setServiceInterfaceClass(serviceInfo.getServiceInterfaceClass());
            desc.setServiceImplClass(serviceInfo.getServiceImplClass());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return desc;
    }

    @Override
    public List<ServiceDesc> loadServiceDescList(String interfaceName) {
        return serviceListMap.get(interfaceName);
    }
}
