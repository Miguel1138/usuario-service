package br.com.scripta_api.usuario_service.usuario_service.repository;

import br.com.scripta_api.usuario_service.usuario_service.data.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioEntityRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByMatricula(String matricula);

}
