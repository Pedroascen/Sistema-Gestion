package sv.ascen2000.InventorySystem.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sv.ascen2000.InventorySystem.exception.BadRequestException;
import sv.ascen2000.InventorySystem.model.Role;
import sv.ascen2000.InventorySystem.model.Users;
import sv.ascen2000.InventorySystem.repository.RoleRepository;
import sv.ascen2000.InventorySystem.repository.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    List<Users> list(){
        return userRepository.findAll();
    }

    @PreAuthorize("hasAuthority('READ_USER')")
    @GetMapping("/me")
    Users getUser(Authentication authentication){
        if(authentication == null){
            throw new NullPointerException("Necesita loguearse para poder consultar informacion.");
        }
        Users user = userRepository.findByUsername(authentication.getName()).orElseThrow(EntityNotFoundException::new);
        if(!user.isEnable()){
            throw new SecurityException("Su usuario ha sido bloqueado, comuniquese con administrador del sistema.");
        }
        return user;
    }

    @PreAuthorize("hasAuthority('READ_USER')")
    @GetMapping("/{id}")
    Users getUser(@PathVariable Long id){
        Users user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return user;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/register-admin")
    Users registerAdmin(@RequestBody Users users){
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseThrow(EntityNotFoundException::new);
        boolean userNameExist = userRepository.existsByUsername(users.getUsername());

        if(userNameExist){
            throw new BadRequestException("El username ya fue registrado");
        }
        users.setRoles(Collections.singletonList(roleAdmin));
        users.setEnable(true);
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        return userRepository.save(users);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('CREATE_USER')")
    @PostMapping("/register")
    Users registerUser(@RequestBody Users users){
        Role roleUser = roleRepository.findByName("ROLE_USERS").orElseThrow(EntityNotFoundException::new);
        boolean userNameExist = userRepository.existsByUsername(users.getUsername());

        if(userNameExist){
            throw new DataIntegrityViolationException("El username ya fue registrado");
        }
        users.setRoles(Collections.singletonList(roleUser));
        users.setEnable(true);
        users.setFecha(LocalDate.now());
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        return userRepository.save(users);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('CREATE_USER')")
    @PutMapping("/{id}")
    Users updateUser(@PathVariable Long id, @RequestBody Users userform){
        if(id==1){
            throw new SecurityException("Se requiere permisos de administrador para realizar esta modificacion.");
        }

        boolean emailExist = userRepository.existsByUsernameAndIdNot(userform.getUsername(),id);
        if (emailExist){
            throw new DataIntegrityViolationException("El username ya fue registrado");
        }

        Users users = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        users.setUsername(userform.getUsername());
        users.setPassword(passwordEncoder.encode(userform.getPassword()));

        if(userform.getRoles() != null){
            boolean isAdmin = userform.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
            Long roleId = 0L;
            List<Role> roles = userform.getRoles();
            for (Role role : roles){
                roleId = role.getId();
            }
            //System.out.println("El Rol es: "+roleId);
            if (isAdmin || roleId == 1){
                throw new SecurityException("Se requiere permisos de administrador para realizar esta modificacion.");
            }
            users.setRoles(userform.getRoles());
        }
        return userRepository.save(users);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('UPDATE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/deactivate/{id}")
    void inactiveUser(@PathVariable Long id){
        if(id==1){
            throw new SecurityException("Se requiere permisos de administrador para realizar esta modificacion.");
        }
        Users user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(user.isEnable()){
            user.setEnable(false);
        }
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('UPDATE_USER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/active/{id}")
    void activeUser(@PathVariable Long id){
        if(id==1){
            throw new SecurityException("Se requiere permisos de administrador para realizar esta modificacion.");
        }
        Users user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!user.isEnable()){
            user.setEnable(true);
        }
        userRepository.save(user);
    }
}
