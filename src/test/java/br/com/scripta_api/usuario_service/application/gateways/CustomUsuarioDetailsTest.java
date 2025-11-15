package br.com.scripta_api.usuario_service.application.gateways;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.domain.UsuarioBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomUsuarioDetailsTest {

    private Usuario criarUsuarioMock(String matricula, TipoDeConta tipo) {
        // Usamos o builder para criar um mock válido do domínio
        return UsuarioBuilder.builder()
                .id(1L)
                .nome("Teste")
                .matricula(matricula)
                .senha("senha123456")
                .tipoDeConta(tipo)
                .build();
    }

    @Test
    @DisplayName("Deve mapear corretamente um ALUNO")
    void deveMapearAluno() {
        // Arrange
        Usuario usuarioAluno = criarUsuarioMock("123", TipoDeConta.ALUNO);
        CustomUsuarioDetails details = new CustomUsuarioDetails(usuarioAluno);

        // Act
        String authority = details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // Assert
        assertEquals("ROLE_ALUNO", authority);
        assertEquals("123", details.getUsername());
        assertEquals("senha123456", details.getPassword());
        assertTrue(details.isEnabled());
    }

    @Test
    @DisplayName("Deve mapear corretamente um BIBLIOTECARIO")
    void deveMapearBibliotecario() {
        // Arrange
        Usuario usuarioBib = criarUsuarioMock("456", TipoDeConta.BIBLIOTECARIO);
        CustomUsuarioDetails details = new CustomUsuarioDetails(usuarioBib);

        // Act
        String authority = details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // Assert
        assertEquals("ROLE_BIBLIOTECARIO", authority);
        assertEquals("456", details.getUsername());
    }
}