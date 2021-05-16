package io.github.zhenbing.frpc.demo.api;

/**
 * UserService
 *
 * @author fengzhenbing
 */
public interface UserService {
    User findById(Integer id);

    void save(String name);
}
