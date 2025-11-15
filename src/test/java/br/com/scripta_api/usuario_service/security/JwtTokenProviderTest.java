package br.com.scripta_api.usuario_service.security;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.domain.UsuarioBuilder;
import br.com.scripta_api.usuario_service.application.gateways.CustomUsuarioDetails;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private CustomUsuarioDetails userDetails;

    // Use valores fixos para teste
    private final String secret = "minhaChaveSecretaSuperLongaParaTestesUnitarios123456";
    private final long validExpiration = 3600000; // 1 hora
    private final long expiredExpiration = -1000; // Já expirado

    @BeforeEach
    void setUp() {
        // Configura um usuário padrão para os testes
        Usuario usuario = UsuarioBuilder.builder()
                .id(1L).nome("Usuario Teste").matricula("123456")
                .senha("senha123456789").tipoDeConta(TipoDeConta.BIBLIOTECARIO)
                .build();
        userDetails = new CustomUsuarioDetails(usuario);
    }

    @Test
    @DisplayName("Deve gerar um token válido e extrair a matrícula")
    void deveGerarTokenEExtrairMatricula() {
        // Arrange
        tokenProvider = new JwtTokenProvider(secret, validExpiration);

        // Act
        String token = tokenProvider.gerarToken(userDetails);
        String matriculaExtraida = tokenProvider.extrairMatricula(token);

        // Assert
        assertNotNull(token);
        assertEquals("123456", matriculaExtraida);
    }

    @Test
    @DisplayName("Deve validar um token com sucesso")
    void deveValidarTokenComSucesso() {
        // Arrange
        tokenProvider = new JwtTokenProvider(secret, validExpiration);
        String token = tokenProvider.gerarToken(userDetails);

        // Act
        boolean isValido = tokenProvider.isTokenValido(token, userDetails);

        // Assert
        assertTrue(isValido);
    }

    @Test
    @DisplayName("Não deve validar um token expirado")
    void naoDeveValidarTokenExpirado() {
        // Arrange
        tokenProvider = new JwtTokenProvider(secret, expiredExpiration);
        String tokenExpirado = tokenProvider.gerarToken(userDetails);

        // Act & Assert
        // A validação de expiração acontece ao tentar extrair qualquer claim
        assertThrows(ExpiredJwtException.class, () -> {
            tokenProvider.isTokenValido(tokenExpirado, userDetails);
        });
    }

    @Test
    @DisplayName("Não deve validar um token de outro usuário")
    void naoDeveValidarTokenDeOutroUsuario() {
        // Arrange
        tokenProvider = new JwtTokenProvider(secret, validExpiration);
        String token = tokenProvider.gerarToken(userDetails); // Token para '123456'

        // Cria um UserDetails diferente
        Usuario outroUsuario = UsuarioBuilder.builder()
                .id(2L).nome("Outro").matricula("987654")
                .senha("senha123456789").tipoDeConta(TipoDeConta.ALUNO)
                .build();
        UserDetails outroUserDetails = new CustomUsuarioDetails(outroUsuario);

        // Act
        boolean isValido = tokenProvider.isTokenValido(token, outroUserDetails);

        // Assert
        assertFalse(isValido); // Username não bate (123456 != 987654)
    }

    @Test
    @DisplayName("Não deve validar um token com assinatura inválida")
    void naoDeveValidarTokenComAssinaturaInvalida() {
        // Arrange
        tokenProvider = new JwtTokenProvider(secret, validExpiration);
        String token = tokenProvider.gerarToken(userDetails);

        // Cria um token provider com outra chave secreta
        JwtTokenProvider providerInvalido = new JwtTokenProvider("outraChaveSecretaMuitoDiferente123456", validExpiration);

        // Act & Assert
        // Ao tentar decodificar com a chave errada, ele falha
        assertThrows(io.jsonwebtoken.security.SignatureException.class, () -> {
            providerInvalido.isTokenValido(token, userDetails);
        });
    }
}