package dev.jlopez.monolitojwt.auth.controller;

import dev.jlopez.monolitojwt.auth.dto.AuthResponseDTO;
import dev.jlopez.monolitojwt.auth.dto.LoginRequestDTO;
import dev.jlopez.monolitojwt.auth.dto.RegisterRequestDTO;
import dev.jlopez.monolitojwt.auth.service.AuthService;
import dev.jlopez.monolitojwt.user.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, Role.USER));
    }

    //REgistro privado de ADMINS
    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponseDTO> registerAdmin(@RequestBody RegisterRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, Role.ADMIN));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request){
        return ResponseEntity.ok(authService.login(request));
    }



}
