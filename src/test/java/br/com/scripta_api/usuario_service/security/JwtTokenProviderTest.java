package br.com.scripta_api.usuario_service.security;


import br.com.scripta_api.usuario_service.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.domain.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    @Test
    @DisplayName("gerarToken inclui subject (matrícula) e roles e valida corretamente")
    void gerarTokenEValidar() {
        String secret = "minha-chave-secreta-de-teste-que-precisa-ter-um-tamanho-bom-para-hmac";
        long expirationMs = 60_000; // 1 min
        JwtTokenProvider provider = new JwtTokenProvider(secret, expirationMs);

        UserDetails usuario = new Usuario(1L, "Fulano", "12345", "hash", TipoDeConta.BIBLIOTECARIO);

        String token = provider.gerarToken(usuario);
        assertNotNull(token);

        // Subject deve ser a matrícula
        String subject = provider.extrairMatricula(token);
        assertEquals("12345", subject);

        // Token deve ser válido para o usuário
        assertTrue(provider.isTokenValido(token, usuario));

        // E não deve estar expirado
        Date exp = provider.extrairExpiracao(token);
        assertTrue(exp.after(new Date()));
    }

    @Test
    @DisplayName("token expirado não é válido")
    void tokenExpirado() throws InterruptedException {
        String secret = "minha-chave-secreta-de-teste-que-precisa-ter-um-tamanho-bom-para-hmac";
        long expirationMs = 5; // expira muito rápido
        JwtTokenProvider provider = new JwtTokenProvider(secret, expirationMs);

        UserDetails usuario = new Usuario(1L, "Fulano", "12345", "hash", TipoDeConta.BIBLIOTECARIO);
        String token = provider.gerarToken(usuario);

        Thread.sleep(10);
        assertThrows(Exception.class, () -> provider.isTokenValido(token, usuario));
    }
}
