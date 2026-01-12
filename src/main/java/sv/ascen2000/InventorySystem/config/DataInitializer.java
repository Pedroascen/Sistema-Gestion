package sv.ascen2000.InventorySystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sv.ascen2000.InventorySystem.model.Permission;
import sv.ascen2000.InventorySystem.model.Role;
import sv.ascen2000.InventorySystem.repository.PermissionRepository;
import sv.ascen2000.InventorySystem.repository.RoleRepository;

import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void run(String... args) throws Exception {

        //Lista de permisos a insertar
        List<String> permissions = Arrays.asList(
                "CREATE_USER","READ_USER","UPDATE_USER","DELETE_USER",
                "CREATE_ROL","READ_ROL","UPDATE_ROL","DELETE_ROL",
                "CREATE_COMPANY","READ_COMPANY","UPDATE_COMPANY","DELETE_COMPANY"
        );
        //Guardar los permisos sino existen
        List<Permission> savedPermission = new ArrayList<>();
        for (String permName : permissions){
            Permission perm = permissionRepository.findByName(permName)
                    .orElseGet(()-> permissionRepository.save(new Permission(permName)));
            savedPermission.add(perm);
        }

        //Crea rol ADMIN
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElse(null);
                if(adminRole == null){
                    Role newAdminRole = new Role("ROLE_ADMIN");
                    newAdminRole.setPermissions(new ArrayList<>(savedPermission));
                    roleRepository.save(newAdminRole);
                }else{
                    List<Permission> existingPermission = adminRole.getPermissions();
                    boolean hasChanges = false;

                    for (Permission perm : savedPermission){
                        boolean alredyExists = existingPermission.stream()
                                .anyMatch(p -> p.getName().equals(perm.getName()));
                        if(!alredyExists){
                            existingPermission.add(perm);
                            hasChanges = true;
                        }
                    }

                    if (hasChanges){
                        adminRole.setPermissions(existingPermission);
                        roleRepository.save(adminRole);
                        System.out.println("- Se actualizaron los permisos del rol ADMIN.");
                    }else {
                        System.out.println("- Rol ADMIN ya tenía todos los permisos.");
                    }
                }

        //Crear rol USER
        roleRepository.findByName("ROLE_USERS")
                .orElseGet(()->{
                    Role userRole = new Role("ROLE_USERS");
                    return roleRepository.save(userRole);
                });

        System.out.println("- Roles y permisos inicializados correctamente.");
        }

}