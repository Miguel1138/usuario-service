package br.com.scripta_api.usuario_service.application.domain;

public class Usuario {
    private Long id;
    private String nome;
    private String matricula;
    private String senha;
    private TipoDeConta tipoDeConta;

    public Usuario() {
    }

    public Usuario(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.matricula = usuario.getMatricula();
        this.senha = usuario.getSenha();
        this.tipoDeConta = usuario.getTipoDeConta();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoDeConta getTipoDeConta() {
        return tipoDeConta;
    }

    public void setTipoDeConta(TipoDeConta tipoDeConta) {
        this.tipoDeConta = tipoDeConta;
    }

    public static class builder {
        private final Usuario usuario;

        public builder() {
            usuario = new Usuario();
        }

        public builder id(Long id) {
            usuario.setId(id);
            return this;
        }

        public builder nome(String nome) {
            usuario.setNome(nome);
            return this;
        }

        public builder matricula(String matricula) {
            usuario.setMatricula(matricula);
            return this;
        }

        public builder senha(String senha) {
            usuario.setSenha(senha);
            return this;
        }

        public builder tipoDeConta(TipoDeConta tipoDeConta) {
            usuario.setTipoDeConta(tipoDeConta);
            return this;
        }

        public Usuario build() {
            if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
                throw new IllegalArgumentException("Senha não pode ser nula");
            }
            if (usuario.getSenha().length() < 10) {
                throw new IllegalArgumentException("Senha deve ter no mínimo 10 caracteres");
            }
            if (usuario.getNome() == null || usuario.getNome().isBlank()) {
                throw new IllegalArgumentException("Nome não pode ser nulo");
            }
            if (usuario.getId() == null) {
                throw new IllegalArgumentException("Id não pode ser nulo");
            }
            if (usuario.getTipoDeConta() == null) {
                throw new IllegalArgumentException("Tipo de conta não pode ser nulo");
            }
            if (usuario.getMatricula() == null || usuario.getMatricula().isEmpty()) {
                throw new IllegalArgumentException("Matrícula não pode ser nula");
            }

            return usuario;
        }
    }

}