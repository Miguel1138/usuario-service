package br.com.scripta_api.usuario_service.usuario_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Matrícula é obrigatória")
    private String matricula;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}