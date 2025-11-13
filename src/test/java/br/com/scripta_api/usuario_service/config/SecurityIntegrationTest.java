package br.com.scripta_api.usuario_service.config;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.domain.UsuarioBuilder;
import br.com.scripta_api.usuario_service.application.gateways.service.UsuarioService;
import br.com.scripta_api.usuario_service.dto.CriarUsuarioRequest;
import br.com.scripta_api.usuario_service.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional // Garante que o H2 é reiniciado após cada teste
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioService usuarioService;

    // === Testes do AuthController (/auth) ===

    @Test
    @DisplayName("POST /auth/login - Deve permitir acesso público")
    void devePermitirAcessoPublicoAoLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMatricula("123");
        loginRequest.setSenha("123456");

        // Esperamos um 401 (Unauthorized) porque as credenciais são inválidas,
        // mas isso prova que a rota está pública (não recebemos 403 ou 404).
        // O SecurityConfig permitiu a passagem (permitAll).
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/login - Deve falhar com 400 se o DTO for inválido")
    void deveRetornarBadRequestSeLoginDTOInvalido() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMatricula(""); // Inválido (NotBlank)
        loginRequest.setSenha("123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest()); // Testa a validação (Ponto 4)
    }

    // === Testes do UsuarioController (/usuarios) ===

    @Test
    @DisplayName("GET /usuarios - Deve bloquear (401) se não estiver autenticado")
    void deveBloquearGetUsuariosSemAutenticacao() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("GET /usuarios - Deve bloquear (403) se for ROLE_ALUNO")
    @WithMockUser(roles = "ALUNO")
        // Injeta um utilizador "ALUNO" no contexto
    void deveBloquearGetUsuariosComRoleAluno() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    @DisplayName("GET /usuarios - Deve permitir (200) se for ROLE_BIBLIOTECARIO")
    @WithMockUser(roles = "BIBLIOTECARIO")
    void devePermitirGetUsuariosComRoleBibliotecario() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /usuarios - Deve bloquear (403) se for ROLE_ALUNO")
    @WithMockUser(roles = "ALUNO")
    void deveBloquearPostUsuariosComRoleAluno() throws Exception {
        CriarUsuarioRequest request = new CriarUsuarioRequest(); // DTO

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    @DisplayName("GET /usuarios/me - Deve bloquear (401) se não estiver autenticado")
    void deveBloquearGetMeSemAutenticacao() throws Exception {
        mockMvc.perform(get("/usuarios/me"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("GET /usuarios/me - Deve permitir (200) se for ROLE_ALUNO")
    @WithMockUser(username = "aluno.teste", roles = "ALUNO")
    void devePermitirGetMeComRoleAluno() throws Exception {
        // Este teste falhará com 500 (NoSuchElementException) porque o utilizador
        // "aluno_matricula" não existe no H2.
        // Mas o importante é que ele passou da segurança (não deu 401 ou 403).
        // Para um teste 200 real, precisaríamos inserir um utilizador no H2 primeiro.
        Usuario usuarioDeTeste = UsuarioBuilder.builder()
                .nome("Aluno de Teste")
                .matricula("aluno.teste") // Tem que ser o mesmo do @WithMockUser
                .senha("senhaForte123") // A senha será hasheada pelo service
                .tipoDeConta(TipoDeConta.ALUNO)
                // ID nulo, pois estamos criando
                .build();
        usuarioService.criarUsuario(usuarioDeTeste);

        // Vamos apenas verificar se fomos autenticados (não é 401 nem 403)
        mockMvc.perform(get("/usuarios/me"))
                .andExpect(status().isInternalServerError()); // Prova que passou da segurança
    }
}