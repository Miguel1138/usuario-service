package br.com.scripta_api.usuario_service.dto;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String matricula;
    private TipoDeConta tipoDeConta;

    public static UsuarioResponse fromDomain(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .matricula(usuario.getMatricula())
                .tipoDeConta(usuario.getTipoDeConta())
                .build();
    }

}
