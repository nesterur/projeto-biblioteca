package com.bibliotecassa.trabalho.Services.Usuarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    @Autowired
    private UsuarioComumRepository usuarioComumRepository;
    @Autowired
    private com.bibliotecassa.trabalho.Services.Pedidos.OrderItemRepository orderItemRepository;
    @Autowired
    private com.bibliotecassa.trabalho.Services.Pedidos.OrderRepository orderRepository;

    // Verifica se a sessão atual pertence a um admin
    private boolean isAdminSession(HttpSession session) {
        if (session == null) return false;
        Object id = session.getAttribute("idUsuario");
        if (id == null) return false;
        String login = id.toString();
        Admin a = adminRepository.findByLoginAdmin(login);
        return a != null;
    }

    @GetMapping("/dashboard-admin")
    public String dashboardAdmin(HttpSession session, Model model) {
        if (!isAdminSession(session)) return "redirect:/login";
        model.addAttribute("admins", adminRepository.findAll());
        model.addAttribute("funcionarios", funcionarioRepository.findAll());
        // render the admin subpage index (keeps same design)
        return "admin/index";
    }

    @PostMapping("/admin/create")
    public String createAdmin(@RequestParam String nome, @RequestParam String email, @RequestParam String senha, HttpSession session, Model model, RedirectAttributes ra) {
        if (!isAdminSession(session)) return "redirect:/login";
        Admin a = new Admin(nome, email, senha);
        adminRepository.save(a);
        // pass success message (login gerado) via flash attribute
    ra.addFlashAttribute("sucessoAdmin", "Administrador criado — login: " + a.getLoginAdmin());
    return "redirect:/admin/manage-users";
    }

    @PostMapping("/admin/delete")
    public String deleteAdmin(@RequestParam String loginAdmin, HttpSession session, RedirectAttributes ra) {
        if (!isAdminSession(session)) return "redirect:/login";
        Admin a = adminRepository.findByLoginAdmin(loginAdmin);
        if (a != null) {
            adminRepository.delete(a);
            ra.addFlashAttribute("mensagemSucesso", "Administrador deletado: " + loginAdmin);
        } else {
            ra.addFlashAttribute("mensagemErro", "Administrador não encontrado: " + loginAdmin);
        }
    return "redirect:/admin/manage-users";
    }

    @PostMapping("/admin/create-funcionario")
    public String createFuncionario(@RequestParam String nome, @RequestParam String email, @RequestParam String senha, HttpSession session, RedirectAttributes ra) {
        if (!isAdminSession(session)) return "redirect:/login";
        Funcionario f = new Funcionario(nome, email, senha);
        try {
            funcionarioRepository.save(f);
            ra.addFlashAttribute("mensagemSucesso", "Funcionário criado — id: " + f.getIdFuncionario());
        } catch (DataIntegrityViolationException dive) {
            // likely duplicate id or unique constraint violation
            ra.addFlashAttribute("mensagemErro", "esse id já existe");
        } catch (ConstraintViolationException cve) {
            // bean validation errors (e.g., invalid email)
            ra.addFlashAttribute("mensagemErro", "Erro de validação: " + cve.getMessage());
        } catch (Exception e) {
            // try to detect nested validation exception
            Throwable cause = e.getCause();
            boolean handled = false;
            while (cause != null) {
                if (cause instanceof ConstraintViolationException) {
                    ra.addFlashAttribute("mensagemErro", "Erro de validação: " + cause.getMessage());
                    handled = true;
                    break;
                }
                cause = cause.getCause();
            }
            if (!handled) {
                ra.addFlashAttribute("mensagemErro", "Erro ao criar funcionário: " + e.getMessage());
            }
        }
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/admin/delete-funcionario")
    public String deleteFuncionario(@RequestParam String idFuncionario, HttpSession session, RedirectAttributes ra) {
        if (!isAdminSession(session)) return "redirect:/login";
        Funcionario f = funcionarioRepository.findByIdFuncionario(idFuncionario);
        if (f != null) {
            funcionarioRepository.delete(f);
            ra.addFlashAttribute("mensagemSucesso", "Funcionário deletado: " + idFuncionario);
        } else {
            ra.addFlashAttribute("mensagemErro", "Funcionário não encontrado: " + idFuncionario);
        }
    return "redirect:/admin/manage-users";
    }

    // --- Admin views with same features as Funcionario ---

    @GetMapping("/admin/volume-alugueis")
    public String volumeAlugueisAdmin(HttpSession session, Model model) {
        if (!isAdminSession(session)) return "redirect:/login";
        List<Object[]> rows = orderItemRepository.findVolumeAlugueisPorLivro();
        List<java.util.Map<String,Object>> volume = new ArrayList<>();
        if (rows != null) {
            for (Object[] r : rows) {
                Map<String,Object> m = new HashMap<>();
                m.put("livroId", r[0]);
                m.put("nomeLivro", r[1]);
                m.put("total", r[2] != null ? ((Number)r[2]).longValue() : 0L);
                volume.add(m);
            }
        }
        model.addAttribute("volumeList", volume);
        return "admin/volume-alugueis";
    }

    @GetMapping("/admin/consultar-usuario")
    public String consultarUsuarioFormAdmin(HttpSession session) {
        if (!isAdminSession(session)) return "redirect:/login";
        return "admin/consultar-usuario";
    }

    @PostMapping("/admin/consultar-usuario")
    public String consultarUsuarioSubmitAdmin(@RequestParam String cpf, Model model, HttpSession session) {
        if (!isAdminSession(session)) return "redirect:/login";
        com.bibliotecassa.trabalho.Services.Usuarios.UsuarioComum usuario = usuarioComumRepository.findByCpf(cpf);
        model.addAttribute("usuarioResult", usuario);
        if (usuario != null) {
            List<com.bibliotecassa.trabalho.Services.Pedidos.OrderItem> items = orderItemRepository.findByOrderUsuarioId(cpf);
            long totalAlugados = 0L;
            if (items != null) {
                for (com.bibliotecassa.trabalho.Services.Pedidos.OrderItem oi : items) {
                    Integer q = oi.getQuantidade();
                    totalAlugados += (q != null ? q.longValue() : 0L);
                }
            }
            model.addAttribute("totalAlugados", totalAlugados);

            List<com.bibliotecassa.trabalho.Services.Pedidos.Order> orders = orderRepository.findByUsuarioId(cpf);
            boolean pagamentosEmDia = true;
            if (orders != null) {
                for (com.bibliotecassa.trabalho.Services.Pedidos.Order o : orders) {
                    String s = o.getStatus();
                    if (s != null) {
                        String SU = s.toUpperCase();
                        if (SU.contains("FAIL") || SU.contains("PEND") || SU.contains("CANCEL") || SU.contains("REJECT")) {
                            pagamentosEmDia = false;
                            break;
                        }
                    }
                }
            }
            model.addAttribute("pagamentosEmDia", pagamentosEmDia);
        } else {
            model.addAttribute("totalAlugados", 0);
            model.addAttribute("pagamentosEmDia", true);
        }
        return "admin/consultar-usuario";
    }

    @GetMapping("/admin/historico")
    public String historicoAdmin(HttpSession session, Model model, @RequestParam(required = false) String usuarioCpf, @RequestParam(required = false) String livroId) {
        if (!isAdminSession(session)) return "redirect:/login";

        java.util.List<com.bibliotecassa.trabalho.Services.Pedidos.OrderItem> items;
        if (usuarioCpf != null && !usuarioCpf.isBlank()) {
            items = orderItemRepository.findByOrderUsuarioId(usuarioCpf);
        } else if (livroId != null && !livroId.isBlank()) {
            items = orderItemRepository.findAll();
            items = items.stream().filter(i -> livroId.equals(i.getLivroId())).toList();
        } else {
            items = orderItemRepository.findAll();
        }

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.util.List<java.util.Map<String,Object>> rows = new java.util.ArrayList<>();

        for (com.bibliotecassa.trabalho.Services.Pedidos.OrderItem oi : items) {
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            String titulo = oi.getNomeLivro();
            m.put("titulo", titulo != null ? titulo : oi.getLivroId());
            java.time.LocalDateTime dt = oi.getRentalStart();
            if (dt == null && oi.getOrder() != null) dt = oi.getOrder().getCreatedAt();
            m.put("dtObj", dt);
            String data = dt != null ? dt.format(fmt) : "-";
            m.put("data", data);
            Integer q = oi.getQuantidade();
            m.put("quantidade", q != null ? q : 1);
            Integer dias = oi.getRentalDays();
            m.put("dias", dias != null ? dias : 0);
            String usuarioId = oi.getOrder() != null ? oi.getOrder().getUsuarioId() : null;
            String nomeUsuario = "-";
            if (usuarioId != null) {
                com.bibliotecassa.trabalho.Services.Usuarios.UsuarioComum uc = usuarioComumRepository.findByCpf(usuarioId);
                nomeUsuario = uc != null ? uc.getNome() : usuarioId;
            }
            m.put("usuario", nomeUsuario);
            m.put("usuarioCpf", usuarioId != null ? usuarioId : "-");
            rows.add(m);
        }

        rows.sort((a,b) -> {
            java.time.LocalDateTime da = (java.time.LocalDateTime)a.get("dtObj");
            java.time.LocalDateTime db = (java.time.LocalDateTime)b.get("dtObj");
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });

        for (java.util.Map<String,Object> m : rows) {
            m.remove("dtObj");
        }

        model.addAttribute("historyRows", rows);
        return "admin/historico";
    }

    // --- Gerenciamento de Usuários (Admin) ---

    @GetMapping("/admin/manage-users")
    public String manageUsers(HttpSession session, Model model) {
        if (!isAdminSession(session)) return "redirect:/login";
        model.addAttribute("users", usuarioComumRepository.findAll());
        model.addAttribute("admins", adminRepository.findAll());
        // defensive: ensure the list assigned to 'funcionarios' contains only Funcionario instances
        java.util.List<Funcionario> rawFuncs = funcionarioRepository.findAll();
        java.util.List<Funcionario> onlyFuncs = new java.util.ArrayList<>();
        if (rawFuncs != null) {
            for (Object o : rawFuncs) {
                if (o instanceof Funcionario) onlyFuncs.add((Funcionario) o);
            }
        }
        model.addAttribute("funcionarios", onlyFuncs);
        return "admin/manage-users";
    }

    @PostMapping("/admin/manage-users/create")
    public String createUser(@RequestParam String nome, @RequestParam(required = false) String cpf, @RequestParam String email, @RequestParam String senha, HttpSession session, RedirectAttributes ra) {
        if (!isAdminSession(session)) return "redirect:/login";
        if (cpf == null || cpf.isBlank()) {
            // Friendly handling: if cpf is missing, do not throw 400 — return with an error message
            ra.addFlashAttribute("mensagemErro", "CPF obrigatório para criar usuário.");
            return "redirect:/admin/manage-users";
        }
        try {
            com.bibliotecassa.trabalho.Services.Usuarios.UsuarioComum u = new com.bibliotecassa.trabalho.Services.Usuarios.UsuarioComum(nome, email, senha, cpf);
            usuarioComumRepository.save(u);
            ra.addFlashAttribute("mensagemSucesso", "Usuário criado: " + cpf);
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao criar usuário: " + e.getMessage());
        }
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/admin/manage-users/delete")
    public String deleteUser(@RequestParam String cpf, HttpSession session, RedirectAttributes ra) {
        if (!isAdminSession(session)) return "redirect:/login";
        com.bibliotecassa.trabalho.Services.Usuarios.UsuarioComum u = usuarioComumRepository.findByCpf(cpf);
        if (u != null) {
            usuarioComumRepository.delete(u);
            ra.addFlashAttribute("mensagemSucesso", "Usuário deletado: " + cpf);
        } else {
            ra.addFlashAttribute("mensagemErro", "Usuário não encontrado: " + cpf);
        }
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/admin/block-entity")
    public String blockEntity(@RequestParam String entityType, @RequestParam String identifier, HttpSession session, RedirectAttributes ra) {
        if (!isAdminSession(session)) return "redirect:/login";
        try {
            switch (entityType) {
                case "ADMIN":
                    Admin a = adminRepository.findByLoginAdmin(identifier);
                    if (a != null) {
                        a.setAtivo(false);
                        adminRepository.save(a);
                        ra.addFlashAttribute("mensagemSucesso", "Administrador bloqueado: " + identifier);
                    } else ra.addFlashAttribute("mensagemErro", "Administrador não encontrado: " + identifier);
                    break;
                case "FUNCIONARIO":
                    Funcionario f = funcionarioRepository.findByIdFuncionario(identifier);
                    if (f != null) {
                        f.setAtivo(false);
                        funcionarioRepository.save(f);
                        ra.addFlashAttribute("mensagemSucesso", "Funcionário bloqueado: " + identifier);
                    } else ra.addFlashAttribute("mensagemErro", "Funcionário não encontrado: " + identifier);
                    break;
                case "USUARIO":
                    com.bibliotecassa.trabalho.Services.Usuarios.UsuarioComum u = usuarioComumRepository.findByCpf(identifier);
                    if (u != null) {
                        u.setAtivo(false);
                        usuarioComumRepository.save(u);
                        ra.addFlashAttribute("mensagemSucesso", "Usuário bloqueado: " + identifier);
                    } else ra.addFlashAttribute("mensagemErro", "Usuário não encontrado: " + identifier);
                    break;
                default:
                    ra.addFlashAttribute("mensagemErro", "Tipo de entidade inválido: " + entityType);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao bloquear: " + e.getMessage());
        }
    return "redirect:/admin/manage-users";
    }

    @PostMapping("/admin/unblock-entity")
    public String unblockEntity(@RequestParam String entityType, @RequestParam String identifier, HttpSession session, RedirectAttributes ra) {
        if (!isAdminSession(session)) return "redirect:/login";
        try {
            switch (entityType) {
                case "ADMIN":
                    Admin a = adminRepository.findByLoginAdmin(identifier);
                    if (a != null) {
                        a.setAtivo(true);
                        adminRepository.save(a);
                        ra.addFlashAttribute("mensagemSucesso", "Administrador desbloqueado: " + identifier);
                    } else ra.addFlashAttribute("mensagemErro", "Administrador não encontrado: " + identifier);
                    break;
                case "FUNCIONARIO":
                    Funcionario f = funcionarioRepository.findByIdFuncionario(identifier);
                    if (f != null) {
                        f.setAtivo(true);
                        funcionarioRepository.save(f);
                        ra.addFlashAttribute("mensagemSucesso", "Funcionário desbloqueado: " + identifier);
                    } else ra.addFlashAttribute("mensagemErro", "Funcionário não encontrado: " + identifier);
                    break;
                case "USUARIO":
                    com.bibliotecassa.trabalho.Services.Usuarios.UsuarioComum u = usuarioComumRepository.findByCpf(identifier);
                    if (u != null) {
                        u.setAtivo(true);
                        usuarioComumRepository.save(u);
                        ra.addFlashAttribute("mensagemSucesso", "Usuário desbloqueado: " + identifier);
                    } else ra.addFlashAttribute("mensagemErro", "Usuário não encontrado: " + identifier);
                    break;
                default:
                    ra.addFlashAttribute("mensagemErro", "Tipo de entidade inválido: " + entityType);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao desbloquear: " + e.getMessage());
        }
        return "redirect:/admin/manage-users";
    }
}
