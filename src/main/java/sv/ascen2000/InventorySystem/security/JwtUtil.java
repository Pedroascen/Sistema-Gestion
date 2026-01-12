package sv.ascen2000.InventorySystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${app.security.jwt.secret}")
    private String tokenSecret;
    @Value("${app.security.jwt.token-validity-in-seconds}")
    private Long tokenValidityInSeconds;

    private byte[] secretBytes;
    private SecretKey key;

    @PostConstruct
    public void init(){
        secretBytes = Decoders.BASE64.decode(tokenSecret);
        key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String createToken(Authentication authentication){
        try {
            long currentDate = new Date().getTime();
            Date expirationDate = new Date(currentDate+(tokenValidityInSeconds*1_000));
            String autority = authentication
                    .getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            return Jwts
                    .builder()
                    .setSubject(authentication.getName())
                    .claim("auth",autority)
                    .setExpiration(expirationDate)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public Authentication getAuthentiocation(String token){
            try{
                JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
                Claims claims = jwtParser.parseClaimsJws(token).getBody();

                List<SimpleGrantedAuthority> authorities = Arrays
                        .stream(claims.get("auth").toString().split(","))
                        .filter(auth -> !auth.trim().isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                User principal = new User(claims.getSubject(),"",authorities);
                return new UsernamePasswordAuthenticationToken(principal, token, authorities);
            }catch (JwtException e){
                return null;
            }
    }
}
