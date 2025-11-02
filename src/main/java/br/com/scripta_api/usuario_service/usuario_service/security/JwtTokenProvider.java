package br.com.scripta_api.usuario_service.usuario_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtTokenProvider {
    private final Key signingKey;
    private final long jwtExpirationMs;

    /**
     * Construtor que injeta os valores do application.properties.
     * Ele também converte a string 'jwt.secret' em uma chave criptográfica real (Key).
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretString,
            @Value("${jwt.expiration-ms}") long jwtExpirationMs
    ) {
        this.jwtExpirationMs = jwtExpirationMs;
        // Converte a string de segredo em bytes
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        // Cria uma chave HMAC-SHA segura para o JWT
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Métodos de GERAÇÃO de Token ---

    /**
     * Método principal para gerar um token para um usuário autenticado.
     */
    public String gerarToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // Adiciona os papéis (Roles) do usuário como uma "claim" extra no token
        // Isso é útil para que outros serviços possam ler os papéis sem consultar o banco
        var roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        extraClaims.put("roles", roles);

        return construirToken(extraClaims, userDetails, jwtExpirationMs);
    }

    private String construirToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims) // Adiciona os claims (ex: "roles")
                .setSubject(userDetails.getUsername()) // Define o "sujeito" como a matrícula
                .setIssuedAt(new Date(System.currentTimeMillis())) // Data de criação
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Data de expiração
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Assina com a chave
                .compact(); // Constrói o string do token
    }

    // --- Métodos de LEITURA de Token ---

    /**
     * Extrai a matrícula (o "Subject") do token.
     * Usado pelo JwtAuthenticationFilter.
     */
    public String extrairMatricula(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    /**
     * Extrai a data de expiração do token.
     */
    public Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    /**
     * Método genérico para extrair qualquer informação (claim) do token.
     */
    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * O "coração" da leitura: decodifica o token usando a chave secreta.
     * Se o token estiver inválido ou expirado, ele lançará uma exceção.
     */
    private Claims extrairTodosClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // Informa a chave para verificar a assinatura
                .build()
                .parseClaimsJws(token) // Decodifica e valida
                .getBody();
    }

    // --- Métodos de VALIDAÇÃO de Token ---

    /**
     * Verifica se um token é válido para um determinado usuário.
     * Usado pelo JwtAuthenticationFilter.
     */
    public boolean isTokenValido(String token, UserDetails userDetails) {
        final String matricula = extrairMatricula(token);
        // Verifica se a matrícula no token é a mesma do usuário
        // E verifica se o token não está expirado
        return (matricula.equals(userDetails.getUsername())) && !isTokenExpirado(token);
    }

    /**
     * Verifica se o token já expirou.
     */
    private boolean isTokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    /**
     * Retorna a chave de assinatura.
     */
    private Key getSigningKey() {
        return this.signingKey;
    }
}
