package at.emreeocn.vinservicecheckerserver.repository;

import at.emreeocn.vinservicecheckerserver.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);
}
