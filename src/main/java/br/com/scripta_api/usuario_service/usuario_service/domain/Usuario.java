package br.com.scripta_api.usuario_service.usuario_service.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record Usuario(
        Long id,
        String nome,
        String matricula,
        String senha,
        TipoDeConta tipoDeConta
) implements UserDetails {

    public Usuario {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + tipoDeConta.name();
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.matricula;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static class builder {
        private Long id;
        private String nome;
        private String matricula;
        private String senha;
        private TipoDeConta tipoDeConta;

        public builder() {
        }

        public builder id(Long id) {
            this.id = id;
            return this;
        }

        public builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public builder matricula(String matricula) {
            this.matricula = matricula;
            return this;
        }

        public builder senha(String senha) {
            this.senha = senha;
            return this;
        }

        public builder tipoDeConta(TipoDeConta tipoDeConta) {
            this.tipoDeConta = tipoDeConta;
            return this;
        }

        public Usuario build() {
            if (senha == null || senha.isBlank()) {
                throw new IllegalArgumentException("Senha não pode ser nula");
            }
            if (senha.length() < 10) {
                throw new IllegalArgumentException("Senha deve ter no mínimo 10 caracteres");
            }
            if (nome == null || nome.isBlank()) {
                throw new IllegalArgumentException("Nome não pode ser nulo");
            }
            if (id == null) {
                throw new IllegalArgumentException("Id não pode ser nulo");
            }
            if (tipoDeConta == null) {
                throw new IllegalArgumentException("Tipo de conta não pode ser nulo");
            }
            if (matricula == null || matricula.isEmpty()) {
                throw new IllegalArgumentException("Matrícula não pode ser nula");
            }

            return new Usuario(id, nome, matricula, senha, tipoDeConta);
        }
    }

}