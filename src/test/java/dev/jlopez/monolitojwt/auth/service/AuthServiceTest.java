package dev.jlopez.monolitojwt.auth.service;

import dev.jlopez.monolitojwt.auth.dto.AuthResponseDTO;
import dev.jlopez.monolitojwt.auth.dto.LoginRequestDTO;
import dev.jlopez.monolitojwt.auth.dto.RegisterRequestDTO;
import dev.jlopez.monolitojwt.user.model.Role;
import dev.jlopez.monolitojwt.user.model.User;
import dev.jlopez.monolitojwt.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AuthService authService;
    @Captor
    private ArgumentCaptor<User> captor;

    @Test
    void register_ShouldSaveUserAndReturnToken_WhenDataIsValid() {
        RegisterRequestDTO requestDTO = createValidRegisterRequest();
        Role role = Role.USER;
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(jwtService.buildToken(anyMap(), any(User.class))).thenReturn("token_de_prueba_de_jlopez");
        //act
        AuthResponseDTO response = authService.register(requestDTO, role);

        //Assert
        verify(userRepository, times(1)).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals(requestDTO.username(),savedUser.getUsername());
        assertEquals("encoded_password",savedUser.getPassword());
        assertEquals(role, savedUser.getRole());
        assertNotNull(savedUser);
        assertEquals("token_de_prueba_de_jlopez", response.token());
        //verificar que el token se creo con valores correctos.
        verify(jwtService).buildToken(anyMap(), eq(savedUser));
    }

    @Test
    void login_ShouldReturnAuthResponse_whenCredentialsAreValid() {
        //arrange
        LoginRequestDTO loginRequest = createValidLoginRequest();
        User userDB = User.builder()
                .username("jlopez")
                .password("contra123")
                .firstname("Jonathan")
                .lastname("Lopez")
                .country("Colombia")
                .role(Role.ADMIN)
                .build();
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDB);
        when(jwtService.buildToken(anyMap(),any(User.class)))
                .thenReturn("token_de_pracatica_123");
        //ACT
        AuthResponseDTO response = authService.login(loginRequest);
        //assert
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).buildToken(anyMap(),eq(userDB));
        assertEquals("token_de_pracatica_123", response.token());
    }

    @Test
    void login_ShouldThrowError_whenCredentialsAreInvalid(){
        LoginRequestDTO loginRequest = createValidLoginRequest();
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciales invalidas."));

        //act/assert
        BadCredentialsException ex =  assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        assertEquals("Credenciales invalidas.",ex.getMessage());
    }

    private RegisterRequestDTO createValidRegisterRequest(){
        return new RegisterRequestDTO(
                "jlopez",
                "contra123",
                "Jonathan",
                "Lopez",
                "Colombia"
        );
    }
    //builder login dto
    private LoginRequestDTO createValidLoginRequest(){
        return new LoginRequestDTO(
                "jlopez",
                "contra123"
        );
    }




}
