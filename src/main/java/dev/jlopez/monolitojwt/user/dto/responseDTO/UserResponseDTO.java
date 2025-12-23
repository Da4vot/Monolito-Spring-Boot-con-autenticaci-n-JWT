package dev.jlopez.monolitojwt.user.dto.responseDTO;

import dev.jlopez.monolitojwt.user.model.Role;

public record UserResponseDTO(
    Integer id,
    String username,
    String firstname,
    String lastname,
    String country,
    Role role
) {}
