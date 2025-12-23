package dev.jlopez.monolitojwt.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.jlopez.monolitojwt.exception.UserNotFoundException;
import dev.jlopez.monolitojwt.user.dto.responseDTO.UserResponseDTO;
import dev.jlopez.monolitojwt.user.model.User;
import dev.jlopez.monolitojwt.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
        
        return mapTDto(user);
    }
    //pendiente en controller
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // findbyid: accede user
    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
        return mapTDto(user);
    }

    // obtiene todos los usuarios (SOLO ADMIN PUEDE ACCEDER)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapTDto) //cada user de la lista a dto
                .toList();
    }

    // eliminar usuario
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }
    //pendiente en controller
    public long countUsers() {
        return userRepository.count();
    }

    //metodo privado para mapear users a DTO
    private UserResponseDTO mapTDto(User user){
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getFirstname(),
            user.getLastname(),
            user.getCountry(),
            user.getRole()
        );
    }

}
