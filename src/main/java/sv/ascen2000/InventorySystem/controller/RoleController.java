package sv.ascen2000.InventorySystem.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.ascen2000.InventorySystem.model.Role;
import sv.ascen2000.InventorySystem.repository.RoleRepository;

import java.text.Normalizer;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping()
    public List<Role> getRoles(){
        return roleRepository.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    Role createRole(@RequestBody Role role) {
        String nombre = role.getName().toUpperCase();
        if(nombre.length() < 5) {
            throw new DataIntegrityViolationException("El campo rol no puede ser vacio o ser menos de 5 caracteres.");
        }
        String extractName = null;
        String textoNormalizado = Normalizer.normalize(nombre, Normalizer.Form.NFD);
        String textoSinAcentos = textoNormalizado.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        extractName = textoSinAcentos.substring(0, 5);

        if(extractName.startsWith("ROLE")){
            throw new DataIntegrityViolationException("El campo rol no debe empezar por ROLE.");
        }
        String nameRol = "ROLE_"+extractName;
        boolean existRol = roleRepository.existsByName(nameRol);
        if(existRol){
            System.out.println("El rol ya existe");
            throw new DataIntegrityViolationException("El Rol ya fue registrado.");
        }
        role.setName(nameRol);
        return roleRepository.save(role);
    }

    @PutMapping("/{id}")
    Role actualizar(@PathVariable Long id, @RequestBody Role role){

        return null;
    }
}
