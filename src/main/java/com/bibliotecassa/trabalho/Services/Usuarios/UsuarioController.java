/* ANOTAÇÕES - UsuarioController.java
 * O QUE: Controlador que gerencia fluxo de login/registro/usuário.
 * POR QUE: fornece endpoints públicos para autenticação e registro.
 * ENTRADAS: requisições HTTP com credenciais/usuário/formulários.
 * SAÍDAS: redirecionamentos, atributos de sessão e modelos para views.
 * NOTAS: altera sessão.tipoUsuario para diferenciar FUNCIONARIO/USUARIO.
 */
package com.bibliotecassa.trabalho.Services.Usuarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
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
        Model model,
        HttpSession session) {
        // tenta como usuário comum
        UsuarioComum usuario = usuarioComumRepository.findByCpf(identificador);
        if (usuario != null) {
            // verifique se o usuário está bloqueado
            if (usuario.isBloqueado()) {
                // Mensagem solicitada pelo usuário quando o bloqueio é feito por um funcionário
                model.addAttribute("erro", "Aviso: seu login foi bloqueado por um funcionário.");
                return "login";
            }
            if (usuario.getSenha().equals(senha)) {
                session.setAttribute("idUsuario", usuario.getCpf()); // Salva o CPF na sessão
                // salvar nome do usuario (primeiro nome) para mostrar no header
                String primeiroNome = usuario.getNome() != null ? usuario.getNome().split(" ")[0] : usuario.getCpf();
                session.setAttribute("nomeUsuario", primeiroNome);
                return "redirect:/dashboard";
            }
        }
        // tenta como admin (admins usam login de 5 dígitos) -> redireciona para dashboard-admin
        Admin admin = adminRepository.findByLoginAdmin(identificador);
        if (admin != null) {
            // do not allow login if admin was deactivated/blocked
            if (!admin.isAtivo()) {
                model.addAttribute("erro", "Aviso: seu login foi bloqueado por um funcionário.");
                return "login";
            }
            if (admin.getSenha().equals(senha)) {
                session.setAttribute("idUsuario", admin.getLoginAdmin()); // Salva o login do admin na sessão
                session.setAttribute("tipoUsuario", "ADMIN"); // Marca sessão como ADMIN
                String primeiroNome = admin.getNome() != null ? admin.getNome().split(" ")[0] : admin.getLoginAdmin();
                session.setAttribute("nomeUsuario", primeiroNome);
                return "redirect:/dashboard-admin";
            }
        }
        // tenta como funcionário
        Funcionario funcionario = funcionarioRepository.findByIdFuncionario(identificador);
        if (funcionario != null) {
            // do not allow login if funcionario was deactivated/blocked
            if (!funcionario.isAtivo()) {
                model.addAttribute("erro", "Aviso: seu login foi bloqueado por um funcionário.");
                return "login";
            }
            if (funcionario.getSenha().equals(senha)) {
                // marca que é funcionário e salva o id do funcionário na sessão
                session.setAttribute("idUsuario", funcionario.getIdFuncionario());
                session.setAttribute("tipoUsuario", "FUNCIONARIO");
                String primeiroNome = funcionario.getNome() != null ? funcionario.getNome().split(" ")[0] : funcionario.getIdFuncionario();
                session.setAttribute("nomeUsuario", primeiroNome);
                return "redirect:/dashboard-funcionario";
            }
        }
        // Se nenhum autenticou
        model.addAttribute("erro", "CPF/ID ou senha inválidos!");
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    
    }

    // logout invalida sessao e redireciona para login mostrando mensagem de sucesso
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout=true";
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