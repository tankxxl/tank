package com.thinkgem.jeesite.modules.api.user;

import java.util.List;

/**
 * Created by rgz on 02/05/2017.
 */
public interface UserService {
    User findById(long id);
    User findByName(String name);
    void saveUser(User user);
    void updateUser(User user);
    void deleteUserById(long id);
    List<User> findAllUsers();
    void deleteAllUsers();
    public boolean isUserExist(User user);
}
