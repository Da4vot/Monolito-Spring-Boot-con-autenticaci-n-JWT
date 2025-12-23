package dev.jlopez.monolitojwt.user.dto.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotBlank(message =  "EL nombre de usuario es obligatorio.")
    @Size(min = 4, max = 20, message = "EL usuario debe tener entre 4 y 20 caracteres")
    String username,
    @NotBlank(message = "La contraseña es obligatoria." )
    @Size(min = 8, message = "La contraseña debe tener minimo 8 caracteres")
    String password,
    @NotBlank(message = "El nombre es obligatorio")
    String firstname,
    @NotBlank(message = "El apellido es obligatorio")
    String lastname,
    @NotBlank(message = "El país es obligatorio")
    String country
) {}
