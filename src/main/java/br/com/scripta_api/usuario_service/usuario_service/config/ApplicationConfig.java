package br.com.scripta_api.usuario_service.usuario_service.config;

import br.com.scripta_api.usuario_service.usuario_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UsuarioRepository usuarioRepository;

    /**
     * Bean 1: UserDetailsService
     * Define COMO o Spring carrega um usuário.
     * Ele usa nosso adapter (UsuarioRepository) que retorna o modelo de domínio (Usuario).
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return matricula -> usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + matricula));
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

    /**
     * Bean 3: AuthenticationProvider
     * Junta o UserDetailsService (Bean 1) e o PasswordEncoder (Bean 2).
     * É este Bean que o SecurityConfig injetará.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean 4: AuthenticationManager
     * O gerenciador que o AuthController usará para processar o login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}