package dev.jlopez.monolitojwt.config;
import dev.jlopez.monolitojwt.user.model.User;
import dev.jlopez.monolitojwt.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    //verificacion por username
    public boolean isOwner(Authentication authentication, String username){
        String authenticationUsername = authentication.getName();
        return authenticationUsername.equals(username);
    }

    //verificaion por id
    public boolean isOwner(Authentication authentication, Integer id){
        if(authentication == null ||  !authentication.isAuthenticated() || id == null){
            return false;
        }
        Object userRequest = authentication.getPrincipal();
        if (userRequest instanceof User user && user.getId() != null){
            return user.getId().equals(id);
        }
        return false;
    }

}
