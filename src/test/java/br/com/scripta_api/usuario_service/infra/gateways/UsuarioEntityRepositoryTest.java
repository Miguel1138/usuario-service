package br.com.scripta_api.usuario_service.infra.gateways;

import br.com.scripta_api.usuario_service.application.domain.TipoDeConta;
import br.com.scripta_api.usuario_service.infra.data.UsuarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // 1. Anotação principal para testes de persistência
public class UsuarioEntityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // 2. Utilitário para inserir dados no H2

    @Autowired
    private UsuarioEntityRepository repository; // 3. O repositório real (não é um mock!)

    @Test
    @DisplayName("Deve salvar e encontrar um usuário pela matrícula")
    void deveEncontrarUsuarioPorMatricula() {
        // Arrange
        UsuarioEntity entity = UsuarioEntity.builder()
                .nome("Teste Persistencia")
                .matricula("123456")
                .senha("senha123")
                .tipoDeConta(TipoDeConta.ALUNO)
                .build();

        // Salva a entidade no H2 usando o EntityManager
        entityManager.persistAndFlush(entity);

        // Act
        // Executa a consulta real (findByMatricula) no banco H2
        Optional<UsuarioEntity> resultado = repository.findByMatricula("123456");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Teste Persistencia", resultado.get().getNome());
        assertEquals(TipoDeConta.ALUNO, resultado.get().getTipoDeConta());
    }

    @Test
    @DisplayName("Deve falhar ao salvar matrícula duplicada (valida constraint 'unique')")
    void deveFalharAoSalvarMatriculaDuplicada() {
        // Arrange
        UsuarioEntity entity1 = UsuarioEntity.builder()
                .nome("Usuario 1")
                .matricula("999")
                .senha("senha123")
                .tipoDeConta(TipoDeConta.ALUNO)
                .build();

        UsuarioEntity entity2 = UsuarioEntity.builder()
                .nome("Usuario 2")
                .matricula("999") // Mesma matrícula
                .senha("senha456")
                .tipoDeConta(TipoDeConta.BIBLIOTECARIO)
                .build();

        entityManager.persistAndFlush(entity1);

        // Act & Assert
        // Verifica se o banco de dados (H2) dispara a exceção de violação de constraint
        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(entity2);
        });
    }

    @Test
    @DisplayName("Deve retornar vazio se a matrícula não existir")
    void deveRetornarVazioSeMatriculaNaoExiste() {
        // Act
        Optional<UsuarioEntity> resultado = repository.findByMatricula("NAO_EXISTE");

        // Assert
        assertTrue(resultado.isEmpty());
    }
}