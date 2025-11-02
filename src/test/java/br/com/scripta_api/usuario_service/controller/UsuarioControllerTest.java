package br.com.scripta_api.usuario_service.controller;

import br.com.scripta_api.usuario_service.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.domain.Usuario;
import br.com.scripta_api.usuario_service.dto.CriarUsuarioRequest;
import br.com.scripta_api.usuario_service.security.JwtTokenProvider;
import br.com.scripta_api.usuario_service.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private Authentication authentication;

    @Test
    @DisplayName("POST /usuarios - 201 quando Bibliotecário cria usuário")
    @WithMockUser(username = "lib", roles = {"BIBLIOTECARIO"})
    @AutoConfigureMockMvc(addFilters = false)
    void criarUsuarioComoBibliotecario() throws Exception {
        CriarUsuarioRequest req = new CriarUsuarioRequest();
        req.setNome("Aluno A");
        req.setMatricula("2025001");
        req.setSenha("senha-super-segura");
        req.setTipoDeConta(TipoDeConta.ALUNO);

        Usuario salvo = new Usuario(10L, req.getNome(), req.getMatricula(), "hash",
                req.getTipoDeConta());

        when(usuarioService.criarUsuario(any(CriarUsuarioRequest.class))).thenReturn(salvo);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nome").value("Aluno A"))
                .andExpect(jsonPath("$.matricula").value("2025001"))
                .andExpect(jsonPath("$.tipoDeConta").value("ALUNO"));
    }

    @Test
    @DisplayName("POST /usuarios - 403 quando não é Bibliotecário")
    @WithMockUser(username = "aluno", roles = {"ALUNO"})
    void criarUsuarioSemPermissao() throws Exception {
        CriarUsuarioRequest req = new CriarUsuarioRequest();
        req.setNome("Aluno A");
        req.setMatricula("2025001");
        req.setSenha("senha-super-segura");
        req.setTipoDeConta(TipoDeConta.ALUNO);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /usuarios - 200 quando Bibliotecário lista usuários")
    @WithMockUser(username = "lib", roles = {"BIBLIOTECARIO"})
    void listarUsuariosComoBibliotecario() throws Exception {
        List<Usuario> lista = List.of(
                new Usuario(1L, "A", "1", "h", TipoDeConta.ALUNO),
                new Usuario(2L, "B", "2", "h", TipoDeConta.BIBLIOTECARIO)
        );
        when(usuarioService.listarUsuarios()).thenReturn(lista);

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].tipoDeConta").value("BIBLIOTECARIO"));
    }

    @Test
    @DisplayName("GET /usuarios/me - 200 retorna perfil do usuário autenticado")
    @WithMockUser(username = "2025001", roles = {"ALUNO"})
    void getMeuPerfil() throws Exception {
        Usuario usuario = new Usuario(5L, "Aluno", "2025001", "h", TipoDeConta.ALUNO);
        when(usuarioService.buscarPorMatricula("2025001")).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.matricula").value("2025001"));
    }
}
