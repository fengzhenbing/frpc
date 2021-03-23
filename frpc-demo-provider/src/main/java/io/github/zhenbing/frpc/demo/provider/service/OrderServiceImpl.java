package io.github.zhenbing.frpc.demo.provider.service;

import io.github.zhenbing.frpc.demo.api.OrderService;
import io.github.zhenbing.frpc.demo.api.Order;
import io.github.zhenbing.frpc.annotation.Service;

/**
 * OrderServiceImpl
 *
 * @author fengzhenbing
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findOrderById(Integer id) {
        return new Order(id, "test", (float) 111.0);
    }
}
