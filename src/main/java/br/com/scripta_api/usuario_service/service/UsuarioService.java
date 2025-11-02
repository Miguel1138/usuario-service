package br.com.scripta_api.usuario_service.service;

import br.com.scripta_api.usuario_service.domain.Usuario;
import br.com.scripta_api.usuario_service.dto.CriarUsuarioRequest;
import br.com.scripta_api.usuario_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario criarUsuario(CriarUsuarioRequest request) {

        // 1. Regra de Negócio: Validar se a matrícula já existe
        usuarioRepository.findByMatricula(request.getMatricula())
                .ifPresent(usuario -> {
                    throw new RuntimeException("Matrícula já cadastrada: " + request.getMatricula());
                });

        // 2. Criar o objeto de Domínio (Usuario)
        Usuario novoUsuarioDomain = new Usuario.builder()
                .nome(request.getNome())
                .matricula(request.getMatricula())
                // 3. CRÍTICO: Hashear a senha antes de salvar!
                .senha(passwordEncoder.encode(request.getSenha()))
                .tipoDeConta(request.getTipoDeConta())
                .build();

        return usuarioRepository.save(novoUsuarioDomain);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }


    @Transactional(readOnly = true)
    public Usuario buscarPorMatricula(String matricula) {
        return usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + matricula));
    }

}