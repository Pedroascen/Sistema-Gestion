package sv.ascen2000.InventorySystem.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.ascen2000.InventorySystem.exception.BadRequestException;
import sv.ascen2000.InventorySystem.model.Permission;
import sv.ascen2000.InventorySystem.model.Role;
import sv.ascen2000.InventorySystem.repository.PermissionRepository;
import sv.ascen2000.InventorySystem.repository.RoleRepository;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private static final String ROLE_PREFIX = "ROLE_";
    private static final int MIN_LENGTH = 5;
    private static final Long ADMIN_ROLE_ID = 1L;

    public RoleController(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping()
    public List<Role> getRoles(){
        return roleRepository.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    Role createRole(@RequestBody Role role) {
        //valida el campo vacio
        validateRole(role);
        String normalizedName = normalize(role.getName());
        String shortName = normalizedName.substring(0,MIN_LENGTH);
        validatePrefix(shortName);
        String finalRoleName = ROLE_PREFIX + shortName;

        if(roleRepository.existsByName(finalRoleName)){
            throw new DataIntegrityViolationException("El Rol ya fue registrado.");
        }
        role.setName(finalRoleName);
        return roleRepository.save(role);
    }

    @PutMapping("/{id}")
    Role actualizar(@PathVariable Long id, @RequestBody Role roleform){
        //Si solo escribe el rol despues del prefijo
        validateProtetedRole(id);
        String cleanName = extractName(roleform.getName());

        //Validar el ingreso del nuevo nombre
        validatedRoleName(cleanName);
        String finalRoleName = buildRoleName(cleanName);

        //Comparar con los roles ya registrados
        Role role = roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        validateDuplicate(finalRoleName,role.getId());

        role.setName(finalRoleName);
        return roleRepository.save(role);
    }

    @PutMapping("/asignar_permisos/{id}")
    public Role asignarPermisos(@PathVariable Long id, @RequestBody List<Permission> permissionsform){
        Role role = roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(permissionsform.isEmpty()){
            throw new BadRequestException("No se encontraron permisos que agregar.");
        }
        List<Permission> permissions = new ArrayList<>();
        for (Permission savedPermission : permissionsform){
            Permission perm = permissionRepository.findById(savedPermission.getId()).orElseThrow(EntityNotFoundException::new);
            permissions.add(perm);
            System.out.println("El permiso a agregar es: "+perm.getName());
        }
        return null;
    }

    //Metodos de validacion
    private void validateRole(Role role) {
        if(role == null || role.getName() == null || role.getName().trim().isEmpty()){
            throw new DataIntegrityViolationException("El nombre del rol es obligatorio.");
        }
        if(role.getName().trim().length() < MIN_LENGTH){
            throw new DataIntegrityViolationException("El rol debe tener al menos 5 caracteres.");
        }
    }

    private String normalize(String name) {
        String upper = name.toUpperCase().trim();
        String normalized = Normalizer.normalize(upper, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private void validatePrefix(String name) {
        if(name.startsWith("ROLE")){
            throw new DataIntegrityViolationException("El rol no debe iniciar con 'ROLE'.");
        }
    }

    private void validateProtetedRole(Long id){
        if(id.equals(ADMIN_ROLE_ID)){
            throw new SecurityException("Se requiere permisos de administrador para realizar esta modificacion.");
        }
    }

    private String extractName(String name){
        if (name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("El nombre del rol es obligatorio.");
        }
        String trimmed = name.trim().toUpperCase();
        if(trimmed.startsWith(ROLE_PREFIX)){
            return trimmed.substring(ROLE_PREFIX.length());
        }
        return trimmed;
    }

    private void validatedRoleName(String name){
        if(name.length()<MIN_LENGTH){
            throw new DataIntegrityViolationException("El rol debe tener al menos 5 caracteres.");
        }
    }

    private String buildRoleName(String name){
        String normalized = normalize(name);
        String shortName = normalized.substring(0,MIN_LENGTH);

        if (shortName.startsWith("ROLE")){
            throw new DataIntegrityViolationException("El rol no debe iniciar con 'ROLE'.");
        }
        return ROLE_PREFIX + shortName;
    }

    private void validateDuplicate(String name, Long id){
        if(roleRepository.existsByNameAndIdNot(name, id)){
            throw new DataIntegrityViolationException("Ya existe otro registro con este nombre.");
        }
    }
}
