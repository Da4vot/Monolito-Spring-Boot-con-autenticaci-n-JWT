package dev.jlopez.monolitojwt.auth.service;

import dev.jlopez.monolitojwt.auth.dto.AuthResponseDTO;
import dev.jlopez.monolitojwt.auth.dto.LoginRequestDTO;
import dev.jlopez.monolitojwt.auth.dto.RegisterRequestDTO;
import dev.jlopez.monolitojwt.exception.UserNotFoundException;
import dev.jlopez.monolitojwt.user.model.Role;
import dev.jlopez.monolitojwt.user.model.User;
import dev.jlopez.monolitojwt.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(RegisterRequestDTO request){
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .firstname(request.firstname())
                .lastname(request.lastname())
                .country(request.country())
                .role(Role.USER)
                .build();

        //persistencia: guardamos user en db
        userRepository.save(user);
        //generamos token con datos ingresados.
        String token = jwtService.buildToken(user);

        return new AuthResponseDTO(token);
    }

    public  AuthResponseDTO login(LoginRequestDTO request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado post-autenticación. "));

        String token = jwtService.buildToken(user);

        return new AuthResponseDTO(token);
    }
}
