package dev.jlopez.monolitojwt.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "Usuario obligatorio")
    String username,
    @NotBlank(message = "Contraseña obligatoria")
    String password
) {}
