/* ANOTAÇÕES - Admin.java
 * O QUE: Entidade que representa um administrador do sistema.
 * POR QUE: Permite distinguir permissões e ações administrativas.
 * ENTRADAS: campos da entidade mapeados (id, username, password, etc.).
 * SAÍDAS: instâncias persistidas no repositório de administradores.
 * NOTAS: tratar senhas com cuidado; não expor em logs.
 */
package com.bibliotecassa.trabalho.Services.Usuarios;

import jakarta.persistence.*;

// representa um admin do sistema
@Entity
@Table(name = "admin")
public class Admin extends ModelUsuarios {
    // login do admin (tipo um id)
    @Column(name = "login_admin", unique = true, length = 5)
    private String loginAdmin;
    public Admin() {} // construtor vazio
    public Admin(String nome, String email, String senha) { // construtor
        super(nome, email, senha);
        this.loginAdmin = gerarLoginAdmin();
    }
    public String getLoginAdmin() { return loginAdmin; }
    public void setLoginAdmin(String loginAdmin) { this.loginAdmin = loginAdmin; }
    @Override
    public String getTipoUsuario() { return "ADMIN"; }
    @Override
    public String getIdentificadorLogin() { return this.loginAdmin; }
    // gera um id aleatorio pro admin
    private String gerarLoginAdmin() {
        return String.format("%05d", (int)(Math.random() * 99999) + 1);
    }
}
