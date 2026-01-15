package dev.jlopez.monolitojwt.auth.dto;

public record RegisterRequestDTO(
    String username,
    String password,
    String firstname,
    String lastname,
    String country
) {
}
