package sv.ascen2000.InventorySystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sv.ascen2000.InventorySystem.controller.dto.JwtResponse;
import sv.ascen2000.InventorySystem.controller.dto.LoginRequest;
import sv.ascen2000.InventorySystem.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, JwtUtil jwtUtil) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public  ResponseEntity<Map<String,Object>> getCurrentUser(Authentication authentication){
        if(authentication == null){
            throw new NullPointerException("Necesita loguearse para poder consultar informacion.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username",authentication.getName());
        response.put("roles",authentication.getAuthorities());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    ResponseEntity<JwtResponse> obtenerToken(@RequestBody LoginRequest loginRequest){
        log.info("Login: {}",loginRequest);

        UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(usernamePAT);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.createToken(authentication);

        return  ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .body(new JwtResponse(token));
    }
}
