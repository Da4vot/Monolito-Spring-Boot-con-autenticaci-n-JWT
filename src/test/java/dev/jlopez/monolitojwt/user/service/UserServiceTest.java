package dev.jlopez.monolitojwt.user.service;

import dev.jlopez.monolitojwt.exception.BadRequestException;
import dev.jlopez.monolitojwt.user.dto.requestDTO.UserRequestDTO;
import dev.jlopez.monolitojwt.user.dto.responseDTO.UserResponseDTO;
import dev.jlopez.monolitojwt.user.model.Role;
import dev.jlopez.monolitojwt.user.model.User;
import dev.jlopez.monolitojwt.user.repository.UserRepository;
import dev.jlopez.monolitojwt.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;
    private UserRequestDTO userRequestDTO;
    private User mockUser;
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @BeforeEach
    void setup() {
        userRequestDTO = new UserRequestDTO(
                "Juanjo",
                "123456789",
                "Juan",
                "Burbano",
                "Colombia"
        );
        mockUser = new User();
        mockUser.setId(1); // Simula registro guardado
        mockUser.setUsername("Juanjo");
        mockUser.setPassword("encoded_password"); // Simula encriptación
        mockUser.setFirstname("Juan");
        mockUser.setLastname("Burbano");
        mockUser.setCountry("Colombia");
        mockUser.setRole(Role.USER);
    }

    @Test
    void createUser_shouldReturnUserResponseDTO_whenRequestIsValid() {
        // 1. ARRANGE (El Abogado del Diablo prepara las mentiras/promesas)

        // PROMESA A: Convence al repositorio de que el nombre "Juanjo" NO existe en la BD.
        // Pista: when(userRepository.existsByUsername(???)).thenReturn(???);
        when(userRepository.existsByUsername(userRequestDTO.username())).thenReturn(false);
        // PROMESA B: Cuando el servicio quiera encriptar, dile que el resultado es "encoded_password".
        // Pista: when(passwordEncoder.encode(???)).thenReturn("encoded_password");
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        // PROMESA C: La más importante. Cuando el servicio intente guardar un User,
        // devuélvele el 'mockUser' que creaste en el setup (el que ya tiene ID=1).
        // Pista: when(userRepository.save(???)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // 2. ACT (La hora de la verdad)
        // Llama al metodo createUser de tu service usando el 'requestDTO'
        // UserResponseDTO result = ...
        UserResponseDTO responseDTO = userService.createUser(userRequestDTO);

        // 3. ASSERT (Verifica que no te hayan estafado)
        // - Asegúrate de que el resultado no sea nulo.
        // - Asegúrate de que el username del resultado sea el mismo del requestDTO.
        // - Asegúrate de que el ID del resultado sea 1.
        assertNotNull(responseDTO);
        assertEquals(responseDTO.username(), userRequestDTO.username());
        assertEquals(1, responseDTO.id());
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenUserAlreadyExists() {

        when(userRepository.existsByUsername(userRequestDTO.username())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.createUser(userRequestDTO));
    }

    @Test
    void updateUser_shouldUpdateFields_whenUserExists() {
        UserRequestDTO newUserRequestDTO = new UserRequestDTO(
                "Juanjito Actu",
                "123456789",
                "Juan",
                "Burbano",
                "Colombia"
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponseDTO resultado = userService.updateUser(1, newUserRequestDTO);

        assertNotNull(resultado);

        // 1. Capturamos lo que se envió al repositorio por medio del save
        verify(userRepository).save(userCaptor.capture());
        // 2. Obtenemos el valor capturado
        User userCapturado = userCaptor.getValue();

        // 3. Verificamos que los datos dentro del objeto capturado sean los del DTO
        assertEquals("Juanjito Actu", userCapturado.getUsername());
        assertEquals("Juan", userCapturado.getFirstname());
    }
    @Test
    void updateUser_shouldNotUpdatePassword_whenPasswordIsEmpty() {
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Juanjito Actualizado",
                "",
                "Juano",
                "Burbano",
                "Colombia"
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponseDTO resultado = userService.updateUser(1, userRequestDTO);

        assertNotNull(resultado);
        verify(userRepository).save(userCaptor.capture());
        User capturado = userCaptor.getValue();

        verify(passwordEncoder, never()).encode(anyString());
        assertEquals(mockUser.getPassword(), capturado.getPassword());
    }
}


