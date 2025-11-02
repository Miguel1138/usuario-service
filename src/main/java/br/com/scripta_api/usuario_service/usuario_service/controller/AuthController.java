package br.com.scripta_api.usuario_service.usuario_service.controller;


import br.com.scripta_api.usuario_service.usuario_service.domain.Usuario;
import br.com.scripta_api.usuario_service.usuario_service.dto.LoginRequest;
import br.com.scripta_api.usuario_service.usuario_service.dto.LoginResponse;
import br.com.scripta_api.usuario_service.usuario_service.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. O AuthenticationManager usará o UserDetailsService e o PasswordEncoder
        //    para validar a matrícula e a senha.
        //    Se falhar, o Spring lança uma 'BadCredentialsException' automática (HTTP 401).
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getMatricula(),
                        loginRequest.getSenha()
                )
        );

        // 2. Se a autenticação for bem-sucedida, pegamos o usuário (nosso modelo Usuario)
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // 3. Geramos o Token JWT
        String token = jwtTokenProvider.gerarToken(usuario);

        // 4. Retornamos o token
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
