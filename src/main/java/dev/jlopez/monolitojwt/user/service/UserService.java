package dev.jlopez.monolitojwt.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.jlopez.monolitojwt.exception.BadRequestException;
import dev.jlopez.monolitojwt.exception.UserNotFoundException;
import dev.jlopez.monolitojwt.user.dto.requestDTO.UserRequestDTO;
import dev.jlopez.monolitojwt.user.dto.responseDTO.UserResponseDTO;
import dev.jlopez.monolitojwt.user.model.Role;
import dev.jlopez.monolitojwt.user.model.User;
import dev.jlopez.monolitojwt.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //metodo de SecurityConfig. (podemos inyectar funciones)



    //crear user
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO){
        if(userRepository.existsByUsername(requestDTO.username())){
            throw new BadRequestException("El user con username "+ requestDTO.username() + " ya existe.");
        }
        //hasheamos la contraseña antes de guardar
        String encodedPassword = passwordEncoder.encode(requestDTO.password());
        
        User user = mapToEntity(requestDTO, encodedPassword);

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    //obtener por username
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
        
        return mapToDto(user);
    }
    
    // obtener por id
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
        return mapToDto(user);
    }
    
    // obtener todos (SOLO ADMIN PUEDE ACCEDER)
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
        .stream()
        .map(this::mapToDto) //cada user de la lista a dto
        .toList();
    }

    //actualizar
    @Transactional
    public UserResponseDTO updateUser(Integer id, UserRequestDTO requestDTO){
        User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
        
        user.setFirstname(requestDTO.firstname());
        user.setLastname(requestDTO.lastname());
        user.setCountry(requestDTO.country());
        user.setUsername(requestDTO.username());

        // si viene contraseña nueva, hasheamos y seteamos
        if (requestDTO.password() != null && !requestDTO.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(requestDTO.password()));
        }
        return mapToDto(userRepository.save(user));
    }

    // eliminar usuario
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    //no es necesario por ahora
    public long countUsers() {
        return userRepository.count();
    }

    //solo para validaciones en la seguridad del futuro
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    //metodo privado para mapear users a DTO
    private UserResponseDTO mapToDto(User user){
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getFirstname(),
            user.getLastname(),
            user.getCountry(),
            user.getRole()
        );
    }
    //privado mapear dto a entidad
    private User mapToEntity(UserRequestDTO requestDTO, String encodedPassword){
        User user = User.builder()
                    .username(requestDTO.username())
                    .password(encodedPassword)
                    .firstname(requestDTO.firstname())
                    .lastname(requestDTO.lastname())
                    .country(requestDTO.country())
                    .role(Role.USER)
                    .build();
        return user;
    }

}
