
package com.bibliotecassa.trabalho.Services.Usuarios;
// arquivo ModelUsuarios.java
// finalidade classe ModelUsuarios comentarios automatizados

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) 
public abstract class ModelUsuarios {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    
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

    

    public ModelUsuarios() {}

    public ModelUsuarios(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = LocalDateTime.now();
        this.ativo = true;
    }

    

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

    
    public abstract String getTipoUsuario();
    public abstract String getIdentificadorLogin();
}






