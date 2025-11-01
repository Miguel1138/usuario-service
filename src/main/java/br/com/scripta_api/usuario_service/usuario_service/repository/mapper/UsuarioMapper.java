package br.com.scripta_api.usuario_service.usuario_service.repository.mapper;


import br.com.scripta_api.usuario_service.usuario_service.data.UsuarioEntity;
import br.com.scripta_api.usuario_service.usuario_service.domain.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        return Usuario.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .matricula(entity.getMatricula())
                .senha(entity.getSenha())
                .tipoDeConta(entity.getTipoDeConta())
                .build();
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;

        return UsuarioEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .matricula(domain.getMatricula())
                .senha(domain.getSenha())
                .tipoDeConta(domain.getTipoDeConta())
                .build();
    }
}