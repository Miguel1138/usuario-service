package br.com.scripta_api.usuario_service.controller;

import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.service.UsuarioService;
import br.com.scripta_api.usuario_service.dto.CriarUsuarioRequest;
import br.com.scripta_api.usuario_service.dto.UsuarioResponse;
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
        Usuario usuarioRequest = new Usuario.builder()
                .nome(request.getNome())
                .matricula(request.getMatricula())
                .senha(request.getSenha())
                .tipoDeConta(request.getTipoDeConta())
                .build();
        ;
        Usuario novoUsuario = usuarioService.criarUsuario(usuarioRequest);
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
        String matricula = authentication.getName();
        Usuario usuario = usuarioService.buscarPorMatricula(matricula).orElseThrow();
        return ResponseEntity.ok(UsuarioResponse.fromDomain(usuario));
    }
}
