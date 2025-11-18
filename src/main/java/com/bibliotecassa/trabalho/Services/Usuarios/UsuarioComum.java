/* ANOTAÇÕES - UsuarioComum.java
 * O QUE: Entidade que modela um usuário cliente da biblioteca.
 * POR QUE: armazena dados de contato e identificação (nome, CPF, email).
 * ENTRADAS: campos mapeados para persistência.
 * SAÍDAS: objetos utilizados por controllers e serviços.
 * NOTAS: proteger dados sensíveis e validar formato de CPF/email.
 */
package com.bibliotecassa.trabalho.Services.Usuarios;

import java.time.LocalDateTime;
import jakarta.persistence.*;

// representa um usuario comum (cliente)
@Entity
@Table(name = "usuario_comum")   // cria a tabela usuario_comum no banco de dados
public class UsuarioComum extends ModelUsuarios {
    // cpf do usuario
    @Column(unique = true, length = 11)
    private String cpf;
    // se o usuario ta bloqueado ou nao
    @Column(nullable = false)
    private boolean bloqueado;
    // datas de bloqueio
    private LocalDateTime dataBloqueio;
    private LocalDateTime fimBloqueio;

    public UsuarioComum() {} // construtor vazio pro jpa
    public UsuarioComum(String nome, String email, String senha, String cpf) {
        super(nome, email, senha); 
        this.cpf = cpf;
        this.bloqueado = false;
    }
    public String getCpf() { return cpf; } // getters e setters
    public void setCpf(String cpf) { this.cpf = cpf; }
    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }
    public LocalDateTime getDataBloqueio() { return dataBloqueio; }
    public void setDataBloqueio(LocalDateTime dataBloqueio) { this.dataBloqueio = dataBloqueio; }
    public LocalDateTime getFimBloqueio() { return fimBloqueio; }
    public void setFimBloqueio(LocalDateTime fimBloqueio) { this.fimBloqueio = fimBloqueio; }
    @Override
    public String getTipoUsuario() { return "USUARIO_COMUM"; }
    @Override
    public String getIdentificadorLogin() { return this.cpf; }
    // bloqueia o usuario ate uma data
    public void bloquearUsuario(LocalDateTime fimBloqueio) {
        this.bloqueado = true;
        this.dataBloqueio = LocalDateTime.now();
        this.fimBloqueio = fimBloqueio;
    }
    // desbloqueia o usuario
    public void desbloquearUsuario() {
        this.bloqueado = false;
        this.dataBloqueio = null;
        this.fimBloqueio = null;
    }
}
