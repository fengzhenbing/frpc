package io.github.zhenbing.frpc.demo.provider.service;

import io.github.zhenbing.frpc.demo.api.User;
import io.github.zhenbing.frpc.demo.api.UserService;
import io.github.zhenbing.frpc.annotation.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * UserServiceImpl
 *
 * @author fengzhenbing
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Override
    public User findById(Integer id) {
        User user = new User(id, "fzb" + id);
        log.info("find user:", user);
        return user;
    }

    @Override
    public void save(String name) {
        log.info("save user: {}", new User(1, name));
    }
}
