package dev.jlopez.monolitojwt.auth.filter;

import dev.jlopez.monolitojwt.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        //1. Si no hay header o no es Bearer, no intentamos autenticar.
        //salimos y que otro filtro maneje la request
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        //2. extraer el token
        jwt = authHeader.substring(7);

        //3. obtener el username del token, usando el jwtservice
        username = jwtService.getUsernameFromToken(jwt);

        //4. Verificamos si hay autenticacion para evitar reprocesar
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){

            //buscamos usuario en la base de datos con userdetailsservice
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //5. validar si el token del usuario es valido
            if (jwtService.isTokenValid(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        //6. siempre la request continua su flujo, no debe terminar en este filtro.
        filterChain.doFilter(request,response);
    }
}
