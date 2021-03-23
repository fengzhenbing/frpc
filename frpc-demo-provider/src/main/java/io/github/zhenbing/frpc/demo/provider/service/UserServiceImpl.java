package io.github.zhenbing.frpc.demo.provider.service;

import io.github.zhenbing.frpc.demo.api.User;
import io.github.zhenbing.frpc.demo.api.UserService;
import io.github.zhenbing.frpc.annotation.Service;

/**
 * UserServiceImpl
 *
 * @author fengzhenbing
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public User findById(Integer id) {
        return new User(1, "fzb");
    }
}
