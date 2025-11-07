package br.com.scripta_api.usuario_service.application.service;

import br.com.scripta_api.usuario_service.application.domain.Usuario;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    Usuario criarUsuario(@Valid Usuario request);

    List<Usuario> listarUsuarios();

    Optional<Usuario> buscarPorMatricula(String matricula);
}