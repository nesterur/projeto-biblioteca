package com.bibliotecassa.trabalho.Services.Usuarios;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario_comum")   // cria a tabela usuario_comum no banco de dados
public class UsuarioComum extends ModelUsuarios {
    @Column(unique = true, length = 11)
    private String cpf;
    @Column(nullable = false)
    private boolean bloqueado;
    private LocalDateTime dataBloqueio;
    private LocalDateTime fimBloqueio;

    public UsuarioComum() {} // Construtor vazio para JPA
    public UsuarioComum(String nome, String email, String senha, String cpf) {
        super(nome, email, senha); 
        this.cpf = cpf;
        this.bloqueado = false;
    }
    public String getCpf() { return cpf; } // Getter e Setters
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
    public void bloquearUsuario(LocalDateTime fimBloqueio) {
        this.bloqueado = true;
        this.dataBloqueio = LocalDateTime.now();
        this.fimBloqueio = fimBloqueio;
    }
    public void desbloquearUsuario() {
        this.bloqueado = false;
        this.dataBloqueio = null;
        this.fimBloqueio = null;
    }
}
