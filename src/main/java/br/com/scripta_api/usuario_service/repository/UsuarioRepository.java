package br.com.scripta_api.usuario_service.repository;

import br.com.scripta_api.usuario_service.data.UsuarioEntity;
import br.com.scripta_api.usuario_service.domain.Usuario;
import br.com.scripta_api.usuario_service.repository.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UsuarioRepository {

    private final UsuarioMapper mapper;
    private final UsuarioEntityRepository repository;

    public Optional<Usuario> findByMatricula(String matricula) {
        return repository.findByMatricula(matricula)
                .map(mapper::toDomain);
    }

    public Usuario save(Usuario usuarioDomain) {
        UsuarioEntity entity = mapper.toEntity(usuarioDomain);
        UsuarioEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    public List<Usuario> findAll() {
        return repository.findAll()
                .stream().map(mapper::toDomain)
                .collect(Collectors.toList());
    }

}
