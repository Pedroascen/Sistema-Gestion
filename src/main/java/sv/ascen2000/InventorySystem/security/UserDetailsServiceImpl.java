package sv.ascen2000.InventorySystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sv.ascen2000.InventorySystem.model.Permission;
import sv.ascen2000.InventorySystem.model.Role;
import sv.ascen2000.InventorySystem.model.Users;
import sv.ascen2000.InventorySystem.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar el usuario
        Users user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if(!user.isEnable()){
            throw new DisabledException("El usuario esta deshabilitado.");
        }

        // Obtener autoridades: roles y permisos
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Role role : user.getRoles()) {
            // Agregar el rol como autoridad con prefijo "ROLE_"
            authorities.add(new SimpleGrantedAuthority(role.getName()));

            // Agregar los permisos del rol como autoridad normal
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnable(),
                true,
                true,
                true,
                authorities
        );
    }


    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //buscar user
        Users user = userRepository
                .findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado: "+username));

        List<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        return User.withUsername(username)
                .password(user.getPassword())
                .roles(roleNames.toArray(new String[0]))
                .build();
    }*/
}
