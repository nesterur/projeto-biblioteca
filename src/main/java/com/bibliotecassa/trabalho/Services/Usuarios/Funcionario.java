package com.bibliotecassa.trabalho.Services.Usuarios;

import jakarta.persistence.*;

@Entity
@Table(name = "funcionario")
public class Funcionario extends ModelUsuarios {
    @Column(unique = true, length = 4)
    private String idFuncionario;
    public Funcionario() {}
    public Funcionario(String nome, String email, String senha) {
        super(nome, email, senha);
        this.idFuncionario = gerarIdFuncionario();
    }
    public String getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(String idFuncionario) { this.idFuncionario = idFuncionario; }
    @Override
    public String getTipoUsuario() { return "FUNCIONARIO"; }
    @Override
    public String getIdentificadorLogin() { return this.idFuncionario; }
    private String gerarIdFuncionario() {
        return String.format("%04d", (int)(Math.random() * 9999) + 1);
    }
}
