package br.com.scripta_api.usuario_service.config;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.domain.UsuarioBuilder;
import br.com.scripta_api.usuario_service.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ApplicationConfig applicationConfig; // A classe que contém o bean

    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        // Instancia o bean manualmente para testar a sua lógica
        userDetailsService = applicationConfig.userDetailsService();
    }

    @Test
    @DisplayName("userDetailsService deve carregar utilizador com sucesso")
    void deveCarregarUsuarioPelaMatricula() {
        // Arrange
        String matricula = "123456";
        Usuario usuarioMock = UsuarioBuilder.builder()
                .id(1L).nome("Teste").matricula(matricula)
                .senha("senha123456").tipoDeConta(TipoDeConta.ALUNO)
                .build();

        when(usuarioRepository.buscarPorMatricula(matricula))
                .thenReturn(Optional.of(usuarioMock));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(matricula);

        // Assert
        assertNotNull(userDetails);
        assertEquals(matricula, userDetails.getUsername());
        verify(usuarioRepository, times(1)).buscarPorMatricula(matricula);
    }

    @Test
    @DisplayName("userDetailsService deve lançar UsernameNotFoundException se não encontrar")
    void deveLancarExcecaoSeUsuarioNaoEncontrado() {
        // Arrange
        String matricula = "404";
        when(usuarioRepository.buscarPorMatricula(matricula))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(matricula);
        });

        verify(usuarioRepository, times(1)).buscarPorMatricula(matricula);
    }
}