package br.com.scripta_api.usuario_service.repository;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.domain.UsuarioBuilder;
import br.com.scripta_api.usuario_service.infra.data.UsuarioEntity;
import br.com.scripta_api.usuario_service.infra.gateways.UsuarioEntityRepository;
import br.com.scripta_api.usuario_service.repository.mapper.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryTest {

    @Mock
    private UsuarioMapper mapper;

    @Mock
    private UsuarioEntityRepository jpaRepository; // O repositório JPA real

    @InjectMocks
    private UsuarioRepository usuarioRepository; // A classe que estamos testando

    private Usuario usuarioDomain;
    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        // Preparamos objetos de exemplo que serão usados nos mocks
        usuarioDomain = UsuarioBuilder.builder()
                .id(1L)
                .nome("Teste")
                .matricula("123")
                .senha("senhaForte123456")
                .tipoDeConta(TipoDeConta.ALUNO)
                .build();

        usuarioEntity = UsuarioEntity.builder()
                .id(1L)
                .nome("Teste")
                .matricula("123")
                .senha("senhaForte123456")
                .tipoDeConta(TipoDeConta.ALUNO)
                .build();
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void deveCriarUsuario() {
        // Arrange
        when(mapper.toEntity(any(Usuario.class))).thenReturn(usuarioEntity);
        when(jpaRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);
        when(mapper.toDomain(any(UsuarioEntity.class))).thenReturn(usuarioDomain);

        // Act
        Usuario novoUsuario = usuarioRepository.criarUsuario(usuarioDomain);

        // Assert
        assertNotNull(novoUsuario);
        assertEquals("123", novoUsuario.getMatricula());
        verify(mapper, times(1)).toEntity(usuarioDomain);
        verify(jpaRepository, times(1)).save(usuarioEntity);
        verify(mapper, times(1)).toDomain(usuarioEntity);
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarUsuarios() {
        // Arrange
        when(jpaRepository.findAll()).thenReturn(List.of(usuarioEntity));
        when(mapper.toDomain(any(UsuarioEntity.class))).thenReturn(usuarioDomain);

        // Act
        List<Usuario> usuarios = usuarioRepository.listarUsuarios();

        // Assert
        assertNotNull(usuarios);
        assertEquals(1, usuarios.size());
        assertEquals("123", usuarios.get(0).getMatricula());
        verify(jpaRepository, times(1)).findAll();
        verify(mapper, times(1)).toDomain(usuarioEntity);
    }

    @Test
    @DisplayName("Deve buscar por matrícula e encontrar o usuário")
    void deveBuscarPorMatriculaEEncontrar() {
        // Arrange
        String matricula = "123";
        when(jpaRepository.findByMatricula(matricula)).thenReturn(Optional.of(usuarioEntity));
        when(mapper.toDomain(any(UsuarioEntity.class))).thenReturn(usuarioDomain);

        // Act
        Optional<Usuario> resultado = usuarioRepository.buscarPorMatricula(matricula);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(matricula, resultado.get().getMatricula());
        verify(jpaRepository, times(1)).findByMatricula(matricula);
        verify(mapper, times(1)).toDomain(usuarioEntity);
    }

    @Test
    @DisplayName("Deve buscar por matrícula e não encontrar")
    void deveBuscarPorMatriculaENaoEncontrar() {
        // Arrange
        String matricula = "404";
        when(jpaRepository.findByMatricula(matricula)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioRepository.buscarPorMatricula(matricula);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(jpaRepository, times(1)).findByMatricula(matricula);
        verify(mapper, never()).toDomain(any()); // Verifica que o mapper nunca foi chamado
    }
}