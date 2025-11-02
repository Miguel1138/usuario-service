package br.com.scripta_api.usuario_service.data;

import br.com.scripta_api.usuario_service.domain.TipoDeConta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Usuarios")
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String matricula;
    @Column(nullable = false)
    private String senha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDeConta tipoDeConta;
}
