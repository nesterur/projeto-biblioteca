/* ANOTAÇÕES - ModelUsuarios.java
 * O QUE: Classe base/abstrata para tipos de usuário (comum, funcionário, admin).
 * POR QUE: define contrato/atributos comuns a todos os usuários.
 * ENTRADAS: propriedades compartilhadas (id, nome, email, etc.).
 * SAÍDAS: subclasses que estendem o comportamento base.
 * NOTAS: manter métodos abstratos documentados para implementações.
 */
package com.bibliotecassa.trabalho.Services.Usuarios;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

// classe base pra todo mundo que é usuario
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // cada subclasse vira uma tabela
public abstract class ModelUsuarios {
    // id do usuario
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // dados comuns de usuario
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

    // construtor vazio pro jpa
    public ModelUsuarios() {}

    // construtor pra usar no codigo
    public ModelUsuarios(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = LocalDateTime.now();
        this.ativo = true;
    }

    // getters e setters
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

    // metodos que cada tipo de usuario tem que implementar
    public abstract String getTipoUsuario();
    public abstract String getIdentificadorLogin();
}

