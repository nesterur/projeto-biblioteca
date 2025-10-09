package com.bibliotecassa.trabalho.Services.Usuarios;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

// Classe base para todos os tipos de usuários
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // Cada subclasse vira uma tabela
public abstract class ModelUsuarios {
    // Chave primária auto-incrementada
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Dados comuns a todos os usuários
    @Column(nullable = false, length = 100)
    protected String nome;

    @Column(nullable = false, unique = true, length = 100)
    @Email(message = "Email inválido")
    protected String email;

    @Column(nullable = false)
    protected String senha;

    @Column(nullable = false)
    protected LocalDateTime dataCadastro;

    @Column(nullable = false)
    protected boolean ativo;

    // Construtor vazio exigido pelo JPA
    public ModelUsuarios() {}

    // Construtor para uso na aplicação
    public ModelUsuarios(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = LocalDateTime.now();
        this.ativo = true;
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    // Métodos abstratos para especialização nas subclasses
    public abstract String getTipoUsuario();
    public abstract String getIdentificadorLogin();
}

