package com.bibliotecassa.trabalho.Services.Usuarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.dao.DataIntegrityViolationException;


@Controller
public class UsuarioController {

    @Autowired
    private UsuarioComumRepository usuarioComumRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private FuncionarioRepository funcionarioRepository;


    // página de login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // redireciona a pagina em branco para o login
    @GetMapping("/")
    public String paginaInicial() {
        return "redirect:/login";
    }

    // PROCESSA O LOGIN
    @PostMapping("/login")
    public String processarLogin(
        @RequestParam String identificador,
        @RequestParam String senha,
        Model model) {
        // tenta como usuário comum
        UsuarioComum usuario = usuarioComumRepository.findByCpf(identificador);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            return "redirect:/dashboard";
        }
        // tenta como admin
        Admin admin = adminRepository.findByLoginAdmin(identificador);
        if (admin != null && admin.getSenha().equals(senha)) {
            return "redirect:/dashboard";
        }
        // tenta como funcionário
        Funcionario funcionario = funcionarioRepository.findByIdFuncionario(identificador);
        if (funcionario != null && funcionario.getSenha().equals(senha)) {
            return "redirect:/dashboard";
        }
        // Se nenhum autenticou
        model.addAttribute("erro", "CPF/ID ou senha inválidos!");
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // Mostra a página de cadastro
    @GetMapping("/cadastro")
    public String cadastro() {
        return "register"; // busca register.html
    }

    // recebe o formulário de cadastro e salva no banco
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