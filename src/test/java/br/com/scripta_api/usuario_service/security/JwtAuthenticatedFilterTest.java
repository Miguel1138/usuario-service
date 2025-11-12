package br.com.scripta_api.usuario_service.security;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.domain.UsuarioBuilder;
import br.com.scripta_api.usuario_service.application.gateways.CustomUsuarioDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticatedFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticatedFilter jwtFilter;

    @BeforeEach
    void setUp() {
        // Limpa o contexto de segurança antes de cada teste
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve autenticar o usuário com token válido e header correto")
    void deveAutenticarComTokenValido() throws ServletException, IOException {
        // Arrange
        String token = "meu.jwt.token";
        String matricula = "123";
        String authHeader = "Bearer " + token;

        Usuario usuario = UsuarioBuilder.builder()
                .id(1L).nome("Teste").matricula(matricula)
                .senha("senha123456789").tipoDeConta(TipoDeConta.ALUNO)
                .build();
        CustomUsuarioDetails userDetails = new CustomUsuarioDetails(usuario);

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtTokenProvider.extrairMatricula(token)).thenReturn(matricula);
        when(userDetailsService.loadUserByUsername(matricula)).thenReturn(userDetails);
        when(jwtTokenProvider.isTokenValido(token, userDetails)).thenReturn(true);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Verifica se o usuário foi colocado no Contexto de Segurança
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(matricula, SecurityContextHolder.getContext().getAuthentication().getName());

        // Verifica se o filtro continuou a cadeia
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar se o header 'Authorization' estiver ausente")
    void naoDeveAutenticarSeHeaderAusente() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Contexto de segurança deve estar vazio
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar se o header não começar com 'Bearer '")
    void naoDeveAutenticarSeHeaderNaoForBearer() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz"); // Header Basic Auth

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar se o token for inválido")
    void naoDeveAutenticarSeTokenInvalido() throws ServletException, IOException {
        // Arrange
        String token = "token.invalido";
        String matricula = "123";
        String authHeader = "Bearer " + token;

        Usuario usuario = UsuarioBuilder.builder().id(1L).nome("Teste").matricula(matricula).senha("senha123456789").tipoDeConta(TipoDeConta.ALUNO).build();
        CustomUsuarioDetails userDetails = new CustomUsuarioDetails(usuario);

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtTokenProvider.extrairMatricula(token)).thenReturn(matricula);
        when(userDetailsService.loadUserByUsername(matricula)).thenReturn(userDetails);
        when(jwtTokenProvider.isTokenValido(token, userDetails)).thenReturn(false); // Token é inválido

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}