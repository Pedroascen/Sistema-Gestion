/*package sv.ascen2000.InventorySystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sv.ascen2000.InventorySystem.controller.dto.RegisterRequest;
import sv.ascen2000.InventorySystem.model.Role;
import sv.ascen2000.InventorySystem.model.Users;
import sv.ascen2000.InventorySystem.repository.RoleRepository;
import sv.ascen2000.InventorySystem.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //verificar si existe user en base
    public boolean hasUser(){
        return userRepository.count() > 0;
    }

    //Crear usuario admin con roles especificos
    public Users registerAdmin(RegisterRequest request){
        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName("ROLE_"+roleName)
                        .orElseThrow(()-> new RuntimeException("Rol no encontrado: "+roleName)))
                .collect(Collectors.toSet());

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole((Role) roles);
        user.setEnable(true);

        return userRepository.save(user);
    }

    //Crear usuario admin con roles especificos
    public Users registerUser(RegisterRequest request){
        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName("ROLE_"+roleName)
                        .orElseThrow(()-> new RuntimeException("Rol no encontrado: "+roleName)))
                .collect(Collectors.toSet());

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole((Role) roles);
        user.setEnable(true);

        return userRepository.save(user);
    }
}
*/