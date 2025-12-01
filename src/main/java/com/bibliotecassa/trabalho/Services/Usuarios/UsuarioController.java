
package com.bibliotecassa.trabalho.Services.Usuarios;
// arquivo UsuarioController.java
// finalidade classe UsuarioController comentarios automatizados

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
// definicao de class nome UsuarioController
public class UsuarioController {

    @Autowired
    private UsuarioComumRepository usuarioComumRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private FuncionarioRepository funcionarioRepository;

    
    @GetMapping("/login")

    public String mostrarLogin() {
        return "login";
    }

    
    @GetMapping("/")

    public String paginaInicial() {
        return "redirect:/login";
    }

    
    @PostMapping("/login")
    public String processarLogin(
        @RequestParam String identificador,
        @RequestParam String senha,
        Model model,
        HttpSession session) {
        
        UsuarioComum usuario = usuarioComumRepository.findByCpf(identificador);
        if (usuario != null) {
            
            if (usuario.isBloqueado() || !usuario.isAtivo()) {
                model.addAttribute("erro", "Aviso: seu login foi bloqueado por um funcionário.");
                return "login";
            }
            if (usuario.getSenha().equals(senha)) {
                session.setAttribute("idUsuario", usuario.getCpf()); 
                
                String primeiroNome = usuario.getNome() != null ? usuario.getNome().split(" ")[0] : usuario.getCpf();
                session.setAttribute("nomeUsuario", primeiroNome);
                return "redirect:/dashboard";
            }
        }
        
        Admin admin = adminRepository.findByLoginAdmin(identificador);
        if (admin != null) {
            
            if (!admin.isAtivo()) {
                model.addAttribute("erro", "Aviso: seu login foi bloqueado por um funcionário.");
                return "login";
            }
            if (admin.getSenha().equals(senha)) {
                session.setAttribute("idUsuario", admin.getLoginAdmin()); 
                session.setAttribute("tipoUsuario", "ADMIN"); 
                String primeiroNome = admin.getNome() != null ? admin.getNome().split(" ")[0] : admin.getLoginAdmin();
                session.setAttribute("nomeUsuario", primeiroNome);
                return "redirect:/dashboard-admin";
            }
        }
        
        Funcionario funcionario = funcionarioRepository.findByIdFuncionario(identificador);
        if (funcionario != null) {
            
            if (!funcionario.isAtivo()) {
                model.addAttribute("erro", "Aviso: seu login foi bloqueado por um funcionário.");
                return "login";
            }
            if (funcionario.getSenha().equals(senha)) {
                
                session.setAttribute("idUsuario", funcionario.getIdFuncionario());
                session.setAttribute("tipoUsuario", "FUNCIONARIO");
                String primeiroNome = funcionario.getNome() != null ? funcionario.getNome().split(" ")[0] : funcionario.getIdFuncionario();
                session.setAttribute("nomeUsuario", primeiroNome);
                return "redirect:/dashboard-funcionario";
            }
        }
        
        model.addAttribute("erro", "CPF/ID ou senha inválidos!");
        return "login";
    }
    
    @GetMapping("/dashboard")

    public String dashboard() {
        return "dashboard";
    
    }

    
    @GetMapping("/logout")

    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout=true";
    }

    
    @GetMapping("/cadastro")

    public String cadastro() {
        return "register"; 
    }

    
    @PostMapping("/cadastro")
public String cadastrarUsuario(
        @RequestParam String nome,
        @RequestParam String cpf,
        @RequestParam String email,
        @RequestParam String senha,
        Model model) {
    try {
        UsuarioComum usuario = new UsuarioComum(nome, email, senha, cpf);
        usuarioComumRepository.save(usuario);
        model.addAttribute("mensagem", "Usuário cadastrado com sucesso!");
        return "login";
    } catch (DataIntegrityViolationException e) {
        model.addAttribute("erro", "CPF já cadastrado!");
        return "register";
    } catch (Exception e) {
        model.addAttribute("erro", "Erro ao cadastrar: " + e.getMessage());
        return "register";
    }
}

}




