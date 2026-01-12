package sv.ascen2000.InventorySystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class JwtAuthFilter extends GenericFilterBean {


    private JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String bearerToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            String token = bearerToken.substring(7);
            Authentication authentication = jwtUtil.getAuthentiocation(token);
            if (authentication != null){
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    /*
    *
     @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        //Info del encabezado
        final String authHeader = request.getHeader("Authorization");

        //Verificar el header valido
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);//sin autentificar
            return;
        }

        //Extraer
        final String jwt = authHeader.substring(7);
        final String username = jwtUtil.extractUsername(jwt);

        //validar si sesion es activa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //Cargar el usuario
            UserDetails userDetails = userDetailsService
                    .loadUserByUsername(username);
            //verificar token con el usuario
            if(jwtUtil.validateToken(jwt, userDetails)){
                //Crea objeto de autenticacion Spring
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                //Registrar usuario como auth  en este request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //Continuar con el resto de los filtros
        filterChain.doFilter(request, response);
    }


    *
     */
}
