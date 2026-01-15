package dev.jlopez.monolitojwt.auth.dto;

public record LoginRequestDTO(
    String username,
    String password
) {}
