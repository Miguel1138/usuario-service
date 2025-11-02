package br.com.scripta_api.usuario_service.dto;

import br.com.scripta_api.usuario_service.domain.TipoDeConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CriarUsuarioRequest {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Matrícula é obrigatória")
    private String matricula;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotNull(message = "Tipo de conta é obrigatório")
    private TipoDeConta tipoDeConta;
}
