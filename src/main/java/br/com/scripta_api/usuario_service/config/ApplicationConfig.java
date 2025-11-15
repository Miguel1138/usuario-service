package br.com.scripta_api.usuario_service.config;

import br.com.scripta_api.usuario_service.application.gateways.CustomUsuarioDetails;
import br.com.scripta_api.usuario_service.repository.UsuarioRepository;
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
public class ApplicationConfig {

    /**
     * Define COMO o Spring carrega um usuário.
     * Ele usa nosso adapter (UsuarioRepository) que retorna o modelo de domínio (Usuario).
     * @return matricula
     */
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return matricula -> usuarioRepository.buscarPorMatricula(matricula)
                .map(CustomUsuarioDetails::new)
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
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService(usuarioRepository));
        authProvider.setPasswordEncoder(passwordEncoder);
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