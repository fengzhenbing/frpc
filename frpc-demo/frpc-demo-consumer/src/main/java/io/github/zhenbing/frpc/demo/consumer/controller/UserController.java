package io.github.zhenbing.frpc.demo.consumer.controller;

import io.github.zhenbing.frpc.core.annotation.Referenced;
import io.github.zhenbing.frpc.demo.api.User;
import io.github.zhenbing.frpc.demo.api.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 *
 * @author fengzhenbing
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Referenced
    private UserService userService;

    @GetMapping("/{id}")
    public User findUser(@PathVariable("id") Integer id) {
        return userService.findById(id);
    }

    @GetMapping("/save/{name}")
    public String saveUser(@PathVariable("name") String name) {
        userService.save(name);
        return "ok";
    }
}
