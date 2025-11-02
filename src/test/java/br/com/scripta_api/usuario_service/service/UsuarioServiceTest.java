package br.com.scripta_api.usuario_service.service;


import br.com.scripta_api.usuario_service.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.domain.Usuario;
import br.com.scripta_api.usuario_service.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UsuarioService usuarioService;

    @Test
    @DisplayName("listarUsuarios retorna lista do repositório")
    void listarUsuarios() {
        List<Usuario> dados = List.of(
                new Usuario(1L, "A", "1", "h", TipoDeConta.ALUNO),
                new Usuario(2L, "B", "2", "h", TipoDeConta.BIBLIOTECARIO)
        );
        when(usuarioRepository.findAll()).thenReturn(dados);

        List<Usuario> resultado = usuarioService.listarUsuarios();
        assertEquals(2, resultado.size());
        assertEquals("A", resultado.get(0).nome());
    }

    @Test
    @DisplayName("buscarPorMatricula retorna usuário quando existe")
    void buscarPorMatriculaOk() {
        Usuario u = new Usuario(5L, "Aluno", "2025001", "hash", TipoDeConta.ALUNO);
        when(usuarioRepository.findByMatricula("2025001")).thenReturn(Optional.of(u));

        Usuario res = usuarioService.buscarPorMatricula("2025001");
        assertEquals(5L, res.id());
        assertEquals("2025001", res.matricula());
    }

    @Test
    @DisplayName("buscarPorMatricula lança UsernameNotFoundException quando não existe")
    void buscarPorMatriculaNaoExiste() {
        when(usuarioRepository.findByMatricula("999")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> usuarioService.buscarPorMatricula("999"));
    }
}
