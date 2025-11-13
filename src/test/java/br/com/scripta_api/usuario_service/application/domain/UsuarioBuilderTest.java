package br.com.scripta_api.usuario_service.application.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioBuilderTest {

    @Test
    @DisplayName("Deve construir Usuario com sucesso quando todos os dados são válidos")
    void deveConstruirUsuarioComSucesso() {
        Usuario usuario = UsuarioBuilder.builder()
                .id(1L)
                .nome("Nome Teste")
                .matricula("123456")
                .senha("senhaForte123456")
                .tipoDeConta(TipoDeConta.ALUNO)
                .build();

        assertNotNull(usuario);
        assertEquals(1L, usuario.getId());
        assertEquals("Nome Teste", usuario.getNome());
        assertEquals("senhaForte123456", usuario.getSenha());
    }

    @Test
    @DisplayName("Deve falhar ao construir se a senha for nula")
    void naoDeveConstruirSemSenha() {
        var builder = UsuarioBuilder.builder()
                .id(1L)
                .nome("Nome Teste")
                .matricula("123456")
                .tipoDeConta(TipoDeConta.ALUNO);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("Senha não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao construir se a senha for curta")
    void naoDeveConstruirComSenhaCurta() {
        var builder = UsuarioBuilder.builder()
                .id(1L)
                .nome("Nome Teste")
                .matricula("123456")
                .senha("curta")
                .tipoDeConta(TipoDeConta.ALUNO);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("Senha deve ter no mínimo 10 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao construir se o nome for nulo")
    void naoDeveConstruirSemNome() {
        var builder = UsuarioBuilder.builder()
                .id(1L)
                .matricula("123456")
                .senha("senhaForte123456")
                .tipoDeConta(TipoDeConta.ALUNO);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("Nome não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao construir se a matrícula for nula")
    void naoDeveConstruirSemMatricula() {
        var builder = UsuarioBuilder.builder()
                .id(1L)
                .nome("Nome Teste")
                .senha("senhaForte123456")
                .tipoDeConta(TipoDeConta.ALUNO);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("Matrícula não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao construir se o tipo de conta for nulo")
    void naoDeveConstruirSemTipoDeConta() {
        var builder = UsuarioBuilder.builder()
                .id(1L)
                .nome("Nome Teste")
                .matricula("123456")
                .senha("senhaForte123456");

        var exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("Tipo de conta não pode ser nulo", exception.getMessage());
    }

}