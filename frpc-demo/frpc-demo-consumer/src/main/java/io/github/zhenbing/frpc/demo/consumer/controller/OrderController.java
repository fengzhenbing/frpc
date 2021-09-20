package io.github.zhenbing.frpc.demo.consumer.controller;

import io.github.zhenbing.frpc.core.annotation.Referenced;
import io.github.zhenbing.frpc.demo.api.Order;
import io.github.zhenbing.frpc.demo.api.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OrderController
 *
 * @author fengzhenbing
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Referenced
    private OrderService orderService;

    @GetMapping("/{id}")
    public Order findOrder(@PathVariable("id") Integer id) {
        return orderService.findOrderById(id);
    }
}
