package br.com.scripta_api.usuario_service.usuario_service.repository;

import br.com.scripta_api.usuario_service.usuario_service.data.UsuarioEntity;
import br.com.scripta_api.usuario_service.usuario_service.domain.Usuario;
import br.com.scripta_api.usuario_service.usuario_service.repository.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

}
