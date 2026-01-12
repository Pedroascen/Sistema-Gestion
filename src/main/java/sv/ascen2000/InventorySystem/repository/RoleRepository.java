package sv.ascen2000.InventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.ascen2000.InventorySystem.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
