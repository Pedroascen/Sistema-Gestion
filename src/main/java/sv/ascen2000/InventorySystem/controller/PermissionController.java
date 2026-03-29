package sv.ascen2000.InventorySystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.ascen2000.InventorySystem.model.Permission;
import sv.ascen2000.InventorySystem.repository.PermissionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {
    private final PermissionRepository permissionRepository;

    public PermissionController(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @GetMapping
    public List<Permission> getPermision(){
        return permissionRepository.findAll();
    }
}
