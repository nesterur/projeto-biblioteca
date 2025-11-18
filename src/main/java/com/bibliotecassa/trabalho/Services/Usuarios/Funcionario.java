/* ANOTAÇÕES - Funcionario.java
 * O QUE: Entidade que representa um funcionário (login por 4 dígitos).
 * POR QUE: fornece credenciais e dados para o fluxo de funcionários.
 * ENTRADAS: campos da entidade (id, codigoFuncionario, nome, etc.).
 * SAÍDAS: instâncias persistidas para validação de login.
 * NOTAS: garantir unicidade do código e não expor credenciais.
 */
package com.bibliotecassa.trabalho.Services.Usuarios;

import jakarta.persistence.*;

// representa um funcionario do sistema
@Entity
@Table(name = "funcionario")
public class Funcionario extends ModelUsuarios {
    // id do funcionario
    @Column(unique = true, length = 4)
    private String idFuncionario;
    public Funcionario() {} // construtor vazio
    public Funcionario(String nome, String email, String senha) { // construtor
        super(nome, email, senha);
        this.idFuncionario = gerarIdFuncionario();
    }
    public String getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(String idFuncionario) { this.idFuncionario = idFuncionario; }
    @Override
    public String getTipoUsuario() { return "FUNCIONARIO"; }
    @Override
    public String getIdentificadorLogin() { return this.idFuncionario; }
    // gera um id aleatorio pro funcionario
    private String gerarIdFuncionario() {
        return String.format("%04d", (int)(Math.random() * 9999) + 1);
    }
}
