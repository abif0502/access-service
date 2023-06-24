package id.fabiworld.accessservice.service;

import id.fabiworld.accessservice.domain.Role;
import id.fabiworld.accessservice.domain.User;
import id.fabiworld.accessservice.repo.RoleRepo;
import id.fabiworld.accessservice.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if(user == null){
            log.error("User \"{}\" not found", username);
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }else{
            log.info("User \"{}\" found", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public User saveUser(User user) {
        var userData = userRepo.findByUsername(user.getUsername());
        if(userData != null){
            throw new RuntimeException("Username is already used, try another");
        }
        log.info("Saving new user \"{}\" to database", user.getFullName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role \"{}\" to database", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Add new role \"{}\" to user \"{}\"", roleName, username);
        User user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

//    @Override
//    public User getUser(String username) {
//        log.info("Fetching user {}", username);
//        return userRepo.findByUsername(username);
//    }

    @Override
    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepo.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        var user = userRepo.findByUsername(username);
        log.info("Fetching user data with username : {}", username);
        if(user != null){
            log.info("Found user : {}", user);
            return  user;
        }
        log.warn("User with username \"{}\" doesn't exist", username);
        throw new UsernameNotFoundException(String.format("User with username %s doesn't exist", username));
    }
}
