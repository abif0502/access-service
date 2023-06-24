package id.fabiworld.accessservice.repo;

import id.fabiworld.accessservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}