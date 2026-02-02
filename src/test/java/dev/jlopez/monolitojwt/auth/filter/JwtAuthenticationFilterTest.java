package dev.jlopez.monolitojwt.auth.filter;

import dev.jlopez.monolitojwt.auth.service.JwtService;
import dev.jlopez.monolitojwt.exception.UserNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
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
    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;
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
        when(request.getHeader("Authorization")).thenReturn("Bearer token_inventado_de_juanito_123");
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

    //token no valido
    @Test
    void doFilterInternal_shouldNotAuthenticate_whenTokenIsInvalid() throws ServletException, IOException {
        //arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer token_inventado_de_juanito_123");
        when(jwtService.getUsernameFromToken(anyString())).thenReturn("juanito");
        when(userDetailsService.loadUserByUsername("juanito")).thenReturn(userDetails);
        when(jwtService.isTokenValid(anyString(), eq(userDetails))).thenReturn(false);

        //act
        jwtAuthenticationFilter.doFilterInternal(request,response,filterChain);

        //assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request,response);
    }

    //user not found
    @Test
    void doFilterInternal_shouldNotAuthenticate_whenUserNotFound() throws ServletException, IOException{
        //arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer token_inventado_de_juanito_123");
        when(jwtService.getUsernameFromToken(anyString())).thenReturn("juanito");
        //formzamos la excepcion
        when(userDetailsService.loadUserByUsername(anyString())).thenThrow(new RuntimeException("Error de base de datos. User not found. "));

        //act
        jwtAuthenticationFilter.doFilterInternal(request,response,filterChain);

        //assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        // Verificamos que la excepción se envió al resolver
        verify(handlerExceptionResolver).resolveException(eq(request), eq(response), isNull(),any(Exception.class));
        verify(filterChain, never()).doFilter(request,response);
    }

}
