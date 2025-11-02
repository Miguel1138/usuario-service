package br.com.scripta_api.usuario_service.controller;

import br.com.scripta_api.usuario_service.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.domain.Usuario;
import br.com.scripta_api.usuario_service.dto.LoginRequest;
import br.com.scripta_api.usuario_service.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("POST /auth/login - sucesso retorna 200 e token")
    void loginSuccessReturnsToken() throws Exception {
        // Arrange
        LoginRequest req = new LoginRequest();
        req.setMatricula("12345");
        req.setSenha("senha-qualquer");

        // usuario autenticado
        UserDetails usuario = new Usuario(1L, "Fulano", "12345", "hash",
                TipoDeConta.BIBLIOTECARIO);

        Authentication authResult = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authResult);
        when(jwtTokenProvider.gerarToken(Mockito.any(UserDetails.class))).thenReturn("jwt-token");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    @DisplayName("POST /auth/login - credenciais inválidas propaga BadCredentialsException")
    void loginBadCredentialsReturns401() throws Exception {
        // Arrange
        LoginRequest req = new LoginRequest();
        req.setMatricula("12345");
        req.setSenha("invalida");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(result -> {
                    Exception ex = result.getResolvedException();
                    if (ex == null) {
                        throw new AssertionError("Esperava exceção, mas foi null");
                    }
                    if (!(ex instanceof jakarta.servlet.ServletException)) {
                        throw new AssertionError("Esperava ServletException, mas foi: " + ex.getClass());
                    }
                    Throwable cause = ex.getCause();
                    if (!(cause instanceof BadCredentialsException)) {
                        throw new AssertionError("Causa esperada BadCredentialsException, mas foi: " + (cause == null ? "null" : cause.getClass()));
                    }
                });
    }
}
