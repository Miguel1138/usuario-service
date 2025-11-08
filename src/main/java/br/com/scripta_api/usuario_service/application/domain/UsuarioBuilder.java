package br.com.scripta_api.usuario_service.application.domain;

public final class UsuarioBuilder {
    private final Usuario usuario;

    private UsuarioBuilder() {
        usuario = new Usuario();
    }

    public static UsuarioBuilder builder() {
        return new UsuarioBuilder();
    }

    public UsuarioBuilder id(Long id) {
        usuario.setId(id);
        return this;
    }

    public UsuarioBuilder nome(String nome) {
        usuario.setNome(nome);
        return this;
    }

    public UsuarioBuilder matricula(String matricula) {
        usuario.setMatricula(matricula);
        return this;
    }

    public UsuarioBuilder senha(String senha) {
        usuario.setSenha(senha);
        return this;
    }

    public UsuarioBuilder tipoDeConta(TipoDeConta tipoDeConta) {
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
