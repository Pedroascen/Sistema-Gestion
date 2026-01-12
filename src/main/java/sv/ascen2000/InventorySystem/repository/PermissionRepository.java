package sv.ascen2000.InventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.ascen2000.InventorySystem.model.Permission;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
