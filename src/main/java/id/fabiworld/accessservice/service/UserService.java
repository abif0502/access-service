package id.fabiworld.accessservice.service;

import id.fabiworld.accessservice.domain.Role;
import id.fabiworld.accessservice.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    //User getUser(String username);
    List<User> getUsers();

    User getUserByUsername(String username);
}
