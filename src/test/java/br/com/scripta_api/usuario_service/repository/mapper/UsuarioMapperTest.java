package br.com.scripta_api.usuario_service.repository.mapper;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.application.domain.Usuario;
import br.com.scripta_api.usuario_service.application.domain.UsuarioBuilder;
import br.com.scripta_api.usuario_service.infra.data.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private UsuarioMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UsuarioMapper();
    }

    @Test
    @DisplayName("Deve mapear UsuarioEntity para Usuario (domain) com sucesso")
    void deveMapearEntityParaDomain() {
        // Arrange
        UsuarioEntity entity = UsuarioEntity.builder()
                .id(1L)
                .nome("Teste")
                .matricula("1234")
                .senha("senha123345124")
                .tipoDeConta(TipoDeConta.ALUNO)
                .build();

        // Act
        Usuario domain = mapper.toDomain(entity);

        // Assert
        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getNome(), domain.getNome());
        assertEquals(entity.getMatricula(), domain.getMatricula());
        assertEquals(entity.getSenha(), domain.getSenha());
        assertEquals(entity.getTipoDeConta(), domain.getTipoDeConta());
    }

    @Test
    @DisplayName("Deve mapear Usuario (domain) para UsuarioEntity com sucesso")
    void deveMapearDomainParaEntity() {
        // Arrange
        // Usamos o builder para criar um usuário de domínio válido
        Usuario domain = UsuarioBuilder.builder()
                .id(1L)
                .nome("Teste")
                .matricula("123")
                .senha("senhaForte123456")
                .tipoDeConta(TipoDeConta.BIBLIOTECARIO)
                .build();

        // Act
        UsuarioEntity entity = mapper.toEntity(domain);

        // Assert
        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getNome(), entity.getNome());
        assertEquals(domain.getMatricula(), entity.getMatricula());
        assertEquals(domain.getSenha(), entity.getSenha());
        assertEquals(domain.getTipoDeConta(), entity.getTipoDeConta());
    }

    @Test
    @DisplayName("Deve retornar nulo ao mapear entity nula")
    void deveRetornarNuloSeEntityForNula() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("Deve retornar nulo ao mapear domain nulo")
    void deveRetornarNuloSeDomainForNulo() {
        assertNull(mapper.toEntity(null));
    }
}