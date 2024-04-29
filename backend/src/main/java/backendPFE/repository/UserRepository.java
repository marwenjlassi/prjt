package backendPFE.repository;

import backendPFE.models.Role;
import backendPFE.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

Optional<User> findByEmail(String email);
    List<User> findByRole(Role role); // New method to find users by role

}