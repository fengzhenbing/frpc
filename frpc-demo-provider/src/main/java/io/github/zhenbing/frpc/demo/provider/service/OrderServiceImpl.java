package io.github.zhenbing.frpc.demo.provider.service;

import io.github.zhenbing.frpc.demo.api.OrderService;
import io.github.zhenbing.frpc.demo.api.Order;
import io.github.zhenbing.frpc.annotation.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * OrderServiceImpl
 *
 * @author fengzhenbing
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findOrderById(Integer id) {
        Order order = new Order(id, "test"+id, (float) 111.0+id);
        log.info("find order: {}",order);
        return order;
    }
}
