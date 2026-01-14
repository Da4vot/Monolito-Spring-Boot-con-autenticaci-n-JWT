package dev.jlopez.monolitojwt.auth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secretkey}")
    private String SECRET_KEY;

    //1. preparacion de la firma, convertir de texto(BASE64) a bytes (binarios)
    private SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //2. Generacion del token.
    private String generateToken(Map<String, Object> extraClaims, UserDetails user){
        return Jwts.builder()
            .claims(extraClaims)
            .subject(user.getUsername()) //a quien pertenece el token
            .issuedAt(new Date(System.currentTimeMillis())) //fecha creacion
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) //1 dia
            .signWith(getKey()) //sella el token con la firma.
            .compact(); //convierte en el string final
    }

    //builder del token
    public String buildToken(UserDetails user){
        return generateToken(new HashMap<>(), user);
    }
    
    //3. Lectura y validacion
    //3.1 extraer todos los claims del token
    private Claims getAllClaims(String token){
        return Jwts.parser()
            .verifyWith(getKey()) //verifica la autenticidad con la llave - evita modificaciones
            .build()
            .parseSignedClaims(token) //abre el token
            .getPayload(); //retorna los payloads del token
    }
    //3.2 verificar un claim especifico
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver)  {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //3.3 Obtener username
    public String getUsernameFromToken(String token){
        return getClaim(token, Claims::getSubject);
    }

    //4. Verificacion final
    //4.1 Compara el username del token con el de la base de datos y revisa la fecha.
    public boolean isTokenValid(String token, UserDetails user){
        final String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    //4.2 virificar expiracion
    private boolean isTokenExpired(String token){
        try {
            Date expiration = getClaim(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
