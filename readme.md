### a rpc framework to learn rpc

#### test
- curl http://localhost:8081/api/order/1
- curl http://localhost:8081/api/user/save/test
- curl http://localhost:8081/api/user/1
 
 

1. 自定义RPC程序：
- 尝试使用压测并分析优化RPC性能
- 尝试使用Netty+TCP作为两端传输方式
- 尝试自定义二进制序列化或者使用kyro/fst等
- 尝试压测改进后的RPC并分析优化，有问题欢迎群里讨论
- 尝试将fastjson改成xstream
- 尝试使用字节码生成方式代替服务端反射

2. 尝试扩展Dubbo
- 基于自定义序列化，实现Dubbo的序列化扩展；
- 基于自定义RPC，实现Dubbo的RPC扩展；
- 在Dubbo的filter机制上，实现REST权限控制，可参考dubbox；
- 实现自定义Dubbo的Cluster/Loadbalance扩展，如果一分钟内调用某个服务/提供者超过10次，则拒绝提供服务直到下一分钟；
- 整合Dubbo+Sentinel，实现限流功能；
- 整合Dubbo与Skywalking，实现全链路性能监控。

###   自定义RPC

1. frpc1.1: 给自定义RPC实现简单的分组(group)和版本(version)。

2. frpc2.0: 给自定义RPC实现：
- 基于zookeeper的注册中心，消费者和生产者可以根据注册中心查找可用服务进行调用(直接选择列表里的最后一个)。
- 当有生产者启动或者下线时，通过zookeeper通知并更新各个消费者，使得各个消费者可以调用新生产者或者不调用下线生产者。

3. 在2.0的基础上继续增强frpc实现：
- 3.0: 实现基于zookeeper的配置中心，消费者和生产者可以根据配置中心配置参数（分组，版本，线程池大小等）。
- 3.1：实现基于zookeeper的元数据中心，将服务描述元数据保存到元数据中心。
- 3.2：实现基于etcd/nacos/apollo等基座的配置/注册/元数据中心。

4. 在3.2的基础上继续增强frpc实现：
- 4.0：实现基于tag的简单路由；
- 4.1：实现基于Weight/ConsistentHash的负载均衡;
- 4.2：实现基于IP黑名单的简单流控；
- 4.3：完善RPC框架里的超时处理，增加重试参数；

5. 在4.3的基础上继续增强frpc实现：
- 5.0：实现利用HTTP头跨进程传递Context参数（隐式传参）；
- 5.1：实现消费端mock一个指定对象的功能（Mock功能）；
- 5.2：实现消费端可以通过一个泛化接口调用不同服务（泛化调用）；
- 5.3：实现基于Weight/ConsistentHash的负载均衡;
- 5.4：实现基于单位时间调用次数的流控，可以基于令牌桶等算法；

6. 实现最终版本6.0：压测并分析调优5.4版本。