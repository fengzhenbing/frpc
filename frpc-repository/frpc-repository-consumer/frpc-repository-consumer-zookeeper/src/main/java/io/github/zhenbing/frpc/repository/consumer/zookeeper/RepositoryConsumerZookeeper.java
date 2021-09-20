package io.github.zhenbing.frpc.repository.consumer.zookeeper;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.github.zhenbing.frpc.common.config.AbstractConfig;
import io.github.zhenbing.frpc.repository.common.RepositoryConfig;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;
import io.github.zhenbing.frpc.repository.consumer.api.RepositoryConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

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
public class RepositoryConsumerZookeeper implements RepositoryConsumer {
    private final String type = "zookeeper";

    private CuratorFramework zookeeperClient;

    private Map<String, List<ServiceDesc>> serviceListMap = new ConcurrentHashMap<>(256);


    @Override
    public void init(RepositoryConfig repositoryConfig) {
        // start zk client
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zookeeperClient = CuratorFrameworkFactory.builder()
                .connectString(repositoryConfig.getHost() + ":" + repositoryConfig.getPort())
                .namespace(AbstractConfig.FRPC)
                // 超时时间： 服务断了后， 超过这个时间，临时节点被删除。 限制在 2 到20 个ticktime
                .sessionTimeoutMs(4000)
                .connectionTimeoutMs(4000)
                .retryPolicy(retryPolicy).build();
        zookeeperClient.start();

        initServiceMap();//TODO 考滤下
        subscribeChange();
    }

    @SneakyThrows
    private void initServiceMap() {
        List<String> interfaceNameList = zookeeperClient.getChildren().forPath("/");
        for (String interfaceName : interfaceNameList) {
            List<ServiceDesc> serviceDescList = new ArrayList<>();
            String rootPath = "/" + interfaceName;
            List<String> serviceList;
            try {
                serviceList = zookeeperClient.getChildren().forPath(rootPath);
                if (!CollectionUtils.isEmpty(serviceList)) {
                    serviceList.forEach(e -> {
                        byte[] data = new byte[0];
                        try {
                            data = zookeeperClient.getData().forPath(rootPath + "/" + e);
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
        CuratorCache curatorCache = CuratorCache.builder(zookeeperClient, "/").build();
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder().forPathChildrenCache("/", zookeeperClient, new PathChildrenCacheListener() {
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
            String interfaceName = pathItems[level - 1];
            List<ServiceDesc> serviceDescList = serviceListMap.get(interfaceName);
            serviceDescList = serviceDescList.stream().filter(e -> !(e.getPort().equals(port) && e.getHost().equals(host))).collect(Collectors.toList());
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
            if (CollectionUtils.isEmpty(serviceDescList)) {
                serviceListMap.put(interfaceName, Lists.newArrayList(serviceDesc));
            } else {
                serviceDescList = serviceDescList.stream().filter(e -> !(e.getHost().equals(serviceDesc.getHost()) && e.getPort().equals(serviceDesc.getPort()))).collect(Collectors.toList());
                serviceDescList.add(serviceDesc);
                serviceListMap.put(interfaceName, serviceDescList);
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

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void close() {
        zookeeperClient.close();
    }
}
