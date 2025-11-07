package br.com.scripta_api.usuario_service.repository;

import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.service.UsuarioService;
import br.com.scripta_api.usuario_service.infra.data.UsuarioEntity;
import br.com.scripta_api.usuario_service.infra.gateways.UsuarioEntityRepository;
import br.com.scripta_api.usuario_service.repository.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UsuarioRepository implements UsuarioService {
    private final UsuarioMapper mapper;
    private final UsuarioEntityRepository repository;

    @Override
    @Transactional
    public Usuario criarUsuario(Usuario usuarioDomain) {
        UsuarioEntity entity = mapper.toEntity(usuarioDomain);
        UsuarioEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return repository.findAll()
                .stream().map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorMatricula(String matricula) {
        return repository.findByMatricula(matricula)
                .map(mapper::toDomain);
    }

}
