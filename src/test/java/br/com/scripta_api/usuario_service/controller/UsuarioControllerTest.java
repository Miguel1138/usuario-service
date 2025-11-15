package br.com.scripta_api.usuario_service.controller;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.domain.UsuarioBuilder;
import br.com.scripta_api.usuario_service.application.gateways.service.UsuarioService;
import br.com.scripta_api.usuario_service.dto.CriarUsuarioRequest;
import br.com.scripta_api.usuario_service.dto.UsuarioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuarioDomain;

    @BeforeEach
    void setUp() {
        usuarioDomain = UsuarioBuilder.builder()
                .id(1L)
                .nome("Teste")
                .matricula("123")
                .senha("senhaForte123456")
                .tipoDeConta(TipoDeConta.ALUNO)
                .build();
    }

    @Test
    @DisplayName("POST /usuarios - Deve criar um usuário com sucesso")
    void deveCriarUsuario() {
        // Arrange
        CriarUsuarioRequest request = new CriarUsuarioRequest();
        request.setNome("Teste");
        request.setMatricula("123");
        request.setSenha("senhaForte123456");
        request.setTipoDeConta(TipoDeConta.ALUNO);

        when(usuarioService.criarUsuario(any(Usuario.class))).thenReturn(usuarioDomain);

        // Act
        ResponseEntity<UsuarioResponse> response = usuarioController.criarUsuario(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("123", response.getBody().getMatricula());
    }

    @Test
    @DisplayName("GET /usuarios - Deve listar usuários com sucesso")
    void deveListarUsuarios() {
        // Arrange
        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuarioDomain));

        // Act
        ResponseEntity<List<UsuarioResponse>> response = usuarioController.listarUsuarios();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("123", response.getBody().get(0).getMatricula());
    }

    @Test
    @DisplayName("GET /usuarios/me - Deve retornar o perfil do usuário logado")
    void deveRetornarMeuPerfil() {
        // Arrange
        String matriculaLogada = "123";
        when(authentication.getName()).thenReturn(matriculaLogada);
        when(usuarioService.buscarPorMatricula(matriculaLogada)).thenReturn(Optional.of(usuarioDomain));

        // Act
        ResponseEntity<UsuarioResponse> response = usuarioController.getMeuPerfil(authentication);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(matriculaLogada, response.getBody().getMatricula());
    }

    @Test
    @DisplayName("GET /usuarios/me - Deve lançar exceção se usuário não for encontrado")
    void deveLancarExcecaoSeMeuPerfilNaoEncontrado() {
        // Arrange
        String matriculaLogada = "404";
        when(authentication.getName()).thenReturn(matriculaLogada);
        when(usuarioService.buscarPorMatricula(matriculaLogada)).thenReturn(Optional.empty());

        // Act & Assert
        // O código do controller usa .orElseThrow()
        assertThrows(NoSuchElementException.class, () -> {
            usuarioController.getMeuPerfil(authentication);
        });
    }
}