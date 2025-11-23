package br.com.scripta_api.usuario_service.config;

import br.com.scripta_api.usuario_service.security.JwtAuthenticatedFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticatedFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                // 2. Definir a política de sessão como STATELESS (API não guarda sessão)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Informar ao Spring qual provedor de autenticação usar
                .authenticationProvider(authenticationProvider)

                // 4. Definir as regras de autorização (o firewall)
                .authorizeHttpRequests(authz -> authz

                        // Rotas Públicas (Permitir acesso sem token)
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        // (Adicione aqui rotas de Swagger/OpenAPI se estiver usando)
                        // .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Rotas Protegidas (Exigem Papel Específico)
                        // (RF-B01) Apenas bibliotecários podem criar usuários
                        .requestMatchers(HttpMethod.POST, "/usuarios").hasRole("BIBLIOTECARIO")
                        // Apenas bibliotecários podem listar todos os usuários
                        .requestMatchers(HttpMethod.GET, "/usuarios").hasRole("BIBLIOTECARIO")
                        // o resto (ex: GET /usuarios/me) exige apenas autenticação
                        .anyRequest().authenticated()
                )

                // 5. Adicionar nosso filtro JWT
                // Ele deve rodar ANTES do filtro padrão do Spring,
                // para que possamos validar o token e configurar o contexto de segurança.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint((
                        (request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autorizado")))
                );

        return http.build();
    }

    /**
     * Efetua liberação para qualquer tipo de origem, não é legal para prod, mas para fins acadêmicos é ok.
     *
     * @author miguel.silva
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
