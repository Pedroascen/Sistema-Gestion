package sv.ascen2000.InventorySystem.config;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sv.ascen2000.InventorySystem.model.Permission;
import sv.ascen2000.InventorySystem.model.Role;
import sv.ascen2000.InventorySystem.model.Users;
import sv.ascen2000.InventorySystem.repository.PermissionRepository;
import sv.ascen2000.InventorySystem.repository.RoleRepository;
import sv.ascen2000.InventorySystem.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        boolean hasChangesAdmin = false;

        //Lista de permisos a insertar
        List<String> permissions = Arrays.asList(
                "CREATE_USER","READ_USER","UPDATE_USER","DELETE_USER",
                "CREATE_ROL","READ_ROL","UPDATE_ROL","DELETE_ROL"
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
                    //boolean hasChanges = false;

                    for (Permission perm : savedPermission){
                        boolean alredyExists = existingPermission.stream()
                                .anyMatch(p -> p.getName().equals(perm.getName()));
                        if(!alredyExists){
                            existingPermission.add(perm);
                            hasChangesAdmin = true;
                        }
                    }

                    if (hasChangesAdmin){
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

        //Crear user root
        Users users = new Users();
        users.setUsername("master");
        users.setPassword("$2a$10$VsUyNYIwJucDDRJhHbkdKuSp6o5Ja4ydObDvXrg8nPp.ot8F3GWZC");
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseThrow(EntityNotFoundException::new);
        users.setRoles(Collections.singletonList(roleAdmin));
        users.setEnable(true);
        users.setFecha(LocalDate.now());
        if(hasChangesAdmin){
            userRepository.save(users);
            System.out.println("- Admin actualizado exitosamente.");
        }
        boolean userNameExist = userRepository.existsByUsername(users.getUsername());
        if (!userNameExist){
            userRepository.save(users);
            System.out.println("- Admin agregado exitosamente.");
        }else{
            System.out.println("- El Admin ya existe!");
        }
    }

}