package br.com.scripta_api.usuario_service.config;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.domain.UsuarioBuilder;
import br.com.scripta_api.usuario_service.application.gateways.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        // 1. Criar o Bibliotecário (Admin) se não existir
        if (usuarioService.buscarPorMatricula("admin").isEmpty()) {
            Usuario admin = UsuarioBuilder.builder()
                    .nome("Admin Bibliotecário")
                    .matricula("admin")
                    .senha("adminsenha123456") // Senha inicial
                    .tipoDeConta(TipoDeConta.BIBLIOTECARIO)
                    .build();

            usuarioService.criarUsuario(admin);
            System.out.println("✅ SEED: Usuário 'admin' (Bibliotecário) criado com sucesso.");
        }

        // 2. Criar um Aluno de teste se não existir
        if (usuarioService.buscarPorMatricula("aluno").isEmpty()) {
            Usuario aluno = UsuarioBuilder.builder()
                    .nome("Aluno Exemplo")
                    .matricula("aluno")
                    .senha("alunosenha123456") // Senha inicial
                    .tipoDeConta(TipoDeConta.ALUNO)
                    .build();

            usuarioService.criarUsuario(aluno);
            System.out.println("✅ SEED: Usuário 'aluno' (Aluno) criado com sucesso.");
        }
    }
}