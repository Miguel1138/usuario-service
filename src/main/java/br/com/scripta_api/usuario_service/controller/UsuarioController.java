package br.com.scripta_api.usuario_service.controller;

import br.com.scripta_api.usuario_service.domain.Usuario;
import br.com.scripta_api.usuario_service.dto.CriarUsuarioRequest;
import br.com.scripta_api.usuario_service.dto.UsuarioResponse;
import br.com.scripta_api.usuario_service.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    /**
     * (RF-B01) Endpoint para criar usuários.
     * Protegido pelo SecurityConfig (só BIBLIOTECARIO).
     */
    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request) {
        Usuario novoUsuario = usuarioService.criarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.fromDomain(novoUsuario));
    }

    /**
     * Endpoint para listar todos os usuários.
     * Protegido pelo SecurityConfig (só BIBLIOTECARIO).
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        List<UsuarioResponse> response = usuarios.stream()
                .map(UsuarioResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para um usuário logado buscar seus próprios dados.
     * Protegido pelo SecurityConfig (qualquer um autenticado).
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> getMeuPerfil(Authentication authentication) {
        // O 'authentication' é injetado pelo Spring Security
        // 'authentication.getName()' retorna a matrícula (o 'username' que definimos)
        String matricula = authentication.getName();
        Usuario usuario = usuarioService.buscarPorMatricula(matricula);
        return ResponseEntity.ok(UsuarioResponse.fromDomain(usuario));
    }
}
