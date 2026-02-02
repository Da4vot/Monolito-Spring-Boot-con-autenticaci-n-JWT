package dev.jlopez.monolitojwt.config;

import dev.jlopez.monolitojwt.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSecurityTest {

    @InjectMocks
    private UserSecurity userSecurity; //clase que vamos a testear

    @Mock
    private Authentication authentication; //impostor

    private User user;
    private final Integer VALID_ID = 1;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(VALID_ID);
        when(authentication.isAuthenticated()).thenReturn(true);
    }
    //tests
    //caso exitoso:
    @Test
    void isOwner_shouldReturnTrue_whenIdsMatch(){
        when(authentication.getPrincipal()).thenReturn(user);
        //2. ACT (actuar)
        //Llama al mét0do de tu clase userSecurity pasando el mock de authentication y el REQUESTId
        boolean result = userSecurity.isOwner(authentication, VALID_ID);
        //3. Assert
        assertTrue(result, "EL test falló porque el ID del usuario no coincidio con el targetId");
    }

    //IDs no coinciden
    @Test
    void isOwner_shouldReturnFalse_whenIdsDoNotMatch(){
        Integer INVALID_ID = 99;
        when(authentication.getPrincipal()).thenReturn(user);
        //act
        boolean result = userSecurity.isOwner(authentication, INVALID_ID);
        //assert
        assertFalse(result, "Los Ids no coinciden");
    }

    //request user is not authenticated
    @Test
    void isOwner_shouldReturnFalse_whenNotAuthenticated(){

        when(authentication.isAuthenticated()).thenReturn(false);
        //when(authentication.getPrincipal()).thenReturn(user);

        boolean result = userSecurity.isOwner(authentication,VALID_ID);

        assertFalse(result);
    }

    // principal type is not User
    @Test
    void isOwner_shouldReturnFalse_whenPrincipalIsNotUser(){
        Object[] numeros = {1,2,3,4}; //no importa el array pero se suele usar String

        when(authentication.getPrincipal()).thenReturn(numeros);

        boolean result = userSecurity.isOwner(authentication, VALID_ID);

        assertFalse(result);
    }
}
