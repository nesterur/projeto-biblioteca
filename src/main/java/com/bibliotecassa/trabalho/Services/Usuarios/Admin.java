
package com.bibliotecassa.trabalho.Services.Usuarios;
// arquivo Admin.java
// finalidade classe Admin comentarios automatizados

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
// definicao de class nome Admin
public class Admin extends ModelUsuarios {
    
    @Column(name = "login_admin", unique = true, length = 5)
    private String loginAdmin;

    public Admin() {} 

    public Admin(String nome, String email, String senha) { 
        super(nome, email, senha);
        this.loginAdmin = gerarLoginAdmin();
    }

    public String getLoginAdmin() { return loginAdmin; }

    public void setLoginAdmin(String loginAdmin) { this.loginAdmin = loginAdmin; }
    @Override

    public String getTipoUsuario() { return "ADMIN"; }
    @Override

    public String getIdentificadorLogin() { return this.loginAdmin; }
    
    private String gerarLoginAdmin() {
        return String.format("%05d", (int)(Math.random() * 99999) + 1);
    }
}




