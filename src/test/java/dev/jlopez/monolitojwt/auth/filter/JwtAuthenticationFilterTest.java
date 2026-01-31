package dev.jlopez.monolitojwt.auth.filter;

import dev.jlopez.monolitojwt.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setup(){
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldStopEarly_whenNoToken() throws ServletException, IOException {
        //arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        //ACT
        jwtAuthenticationFilter.doFilterInternal(request,response,filterChain);

        //assert
        verify(jwtService, never()).getUsernameFromToken(anyString());
    }

    //autenticacion exitosa.
    @Test
    void doFilterInternal_shouldAuthenticateUser_whenJwtIsValid() throws ServletException, IOException {
        //arrange
        String jwt = "Bearer token_inventado_de_juanito_123";
        when(request.getHeader("Authorization")).thenReturn(jwt);
        when(jwtService.getUsernameFromToken(anyString())).thenReturn("juanito");
        when(userDetailsService.loadUserByUsername("juanito")).thenReturn(userDetails);
        when(jwtService.isTokenValid(anyString(), eq(userDetails))).thenReturn(true);
        when(userDetails.getUsername()).thenReturn("juanito");

        //ACT
        jwtAuthenticationFilter.doFilterInternal(request,response,filterChain);

        //assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("juanito", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request,response);
    }
}
