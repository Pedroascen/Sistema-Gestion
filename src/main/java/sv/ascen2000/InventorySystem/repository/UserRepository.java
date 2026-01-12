package sv.ascen2000.InventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.ascen2000.InventorySystem.model.Users;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long> {
    List<Users> findByIdNot(Long id);
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, Long idNot);
    Optional<Users> existsByUsernameAndId(String username, Long id);
}
