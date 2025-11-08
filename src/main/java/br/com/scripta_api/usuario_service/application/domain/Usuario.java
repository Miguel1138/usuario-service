package br.com.scripta_api.usuario_service.application.domain;

public class Usuario {
    private Long id;
    private String nome;
    private String matricula;
    private String senha;
    private TipoDeConta tipoDeConta;

    protected Usuario() {
    }

    protected Usuario(Usuario usuario) {
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
}