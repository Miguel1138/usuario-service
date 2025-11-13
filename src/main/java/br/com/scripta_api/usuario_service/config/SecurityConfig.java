package br.com.scripta_api.usuario_service.config;

import br.com.scripta_api.usuario_service.security.JwtAuthenticatedFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticatedFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean 2: PasswordEncoder
     * Define o algoritmo para hashear senhas.
     * DEVE estar aqui para evitar dependência circular com o SecurityConfig.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
