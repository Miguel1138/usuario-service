package br.com.scripta_api.usuario_service.controller;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.domain.UsuarioBuilder;
import br.com.scripta_api.usuario_service.application.gateways.CustomUsuarioDetails;
import br.com.scripta_api.usuario_service.dto.LoginRequest;
import br.com.scripta_api.usuario_service.dto.LoginResponse;
import br.com.scripta_api.usuario_service.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("POST /auth/login - Deve autenticar e retornar token JWT")
    void deveLogarComSucesso() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMatricula("123");
        loginRequest.setSenha("senha123");

        Usuario usuario = UsuarioBuilder.builder()
                .id(1L).nome("Teste").matricula("123")
                .senha("senha12asdfasfadsfas3").tipoDeConta(TipoDeConta.ALUNO)
                .build();
        CustomUsuarioDetails userDetails = new CustomUsuarioDetails(usuario);

        Authentication authMock = mock(Authentication.class);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authMock);

        when(authMock.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.gerarToken(userDetails)).thenReturn("mocked.jwt.token");

        // Act
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mocked.jwt.token", response.getBody().getAccessToken());
    }

    @Test
    @DisplayName("POST /auth/login - Deve lançar BadCredentialsException se a senha for inválida")
    void deveFalharLoginComCredenciaisInvalidas() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMatricula("123");
        loginRequest.setSenha("senhaErrada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authController.login(loginRequest);
        });

        verify(jwtTokenProvider, never()).gerarToken(any()); // Token não deve ser gerado
    }
}