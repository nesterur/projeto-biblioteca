/* ANOTAÇÕES - FuncionarioController.java
 * O QUE: Controller com rotas para funcionalidades do funcionário (dashboard, consultas).
 * POR QUE: separa a interface de funcionário da de usuários comuns e disponibiliza
 *        endpoints administrativos (consultas, relatórios de volume e histórico).
 * ENTRADAS: parâmetros HTTP (query/form) e sessão (session.tipoUsuario).
 * SAÍDAS: modelos para views Thymeleaf contendo volume, histórico e consultas.
 * NOTAS: rotas devem checar session.tipoUsuario == "FUNCIONARIO" antes de exibir dados.
 */
package com.bibliotecassa.trabalho.Services.Usuarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class FuncionarioController {

    @Autowired
    private UsuarioComumRepository usuarioComumRepository;
    @Autowired
    private com.bibliotecassa.trabalho.Services.Pedidos.OrderItemRepository orderItemRepository;
    @Autowired
    private com.bibliotecassa.trabalho.Services.Pedidos.OrderRepository orderRepository;

    // Mostrar dashboard específico para funcionários
    @GetMapping("/dashboard-funcionario")
    public String dashboardFuncionario(HttpSession session, Model model) {
        // opcional: checar se está logado como funcionário
        Object tipo = session.getAttribute("tipoUsuario");
        if (tipo == null || !"FUNCIONARIO".equals(tipo.toString())) {
            // não é funcionário — redireciona ao login
            return "redirect:/login";
        }
        // manter nome na view via sessão (fragmento navbar usa session.nomeUsuario)
        return "dashboard-funcionario";
    }

    // Página que mostra volume de aluguéis por livro (placeholder)
    @GetMapping("/funcionario/volume-alugueis")
    public String volumeAlugueis(HttpSession session, Model model) {
        Object tipo = session.getAttribute("tipoUsuario");
        if (tipo == null || !"FUNCIONARIO".equals(tipo.toString())) return "redirect:/login";
        // busca agregada: livroId, nomeLivro, total
        List<Object[]> rows = orderItemRepository.findVolumeAlugueisPorLivro();
        // converte para uma estrutura mais fácil de usar no template
        List<java.util.Map<String,Object>> volume = new java.util.ArrayList<>();
        for (Object[] r : rows) {
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            m.put("livroId", r[0]);
            m.put("nomeLivro", r[1]);
            m.put("total", r[2] != null ? ((Number)r[2]).longValue() : 0L);
            volume.add(m);
        }
        model.addAttribute("volumeList", volume);
        return "funcionario/volume-alugueis";
    }

    // Form para consultar usuário por CPF
    @GetMapping("/funcionario/consultar-usuario")
    public String consultarUsuarioForm(HttpSession session) {
        Object tipo = session.getAttribute("tipoUsuario");
        if (tipo == null || !"FUNCIONARIO".equals(tipo.toString())) return "redirect:/login";
        return "funcionario/consultar-usuario";
    }

    @PostMapping("/funcionario/consultar-usuario")
    public String consultarUsuarioSubmit(@RequestParam String cpf, Model model, HttpSession session) {
        Object tipo = session.getAttribute("tipoUsuario");
        if (tipo == null || !"FUNCIONARIO".equals(tipo.toString())) return "redirect:/login";
        // buscar usuario comum pelo CPF
        UsuarioComum usuario = usuarioComumRepository.findByCpf(cpf);
        model.addAttribute("usuarioResult", usuario);
        // se encontrou o usuário, calcular quantos livros ele alugou e checar status de pagamentos
        if (usuario != null) {
            // contar itens alugados (soma de quantidade em order_item ligados ao usuario)
            List<com.bibliotecassa.trabalho.Services.Pedidos.OrderItem> items = orderItemRepository.findByOrderUsuarioId(cpf);
            long totalAlugados = 0L;
            if (items != null) {
                for (com.bibliotecassa.trabalho.Services.Pedidos.OrderItem oi : items) {
                    Integer q = oi.getQuantidade();
                    totalAlugados += (q != null ? q.longValue() : 0L);
                }
            }
            model.addAttribute("totalAlugados", totalAlugados);

            // checar se todos os pagamentos estão em dia: assumir que status 'FAILED','PENDING','CANCELLED' indicam problemas
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
        return "funcionario/consultar-usuario";
    }

    // Histórico (ativos e alugados) — placeholder
    @GetMapping("/funcionario/historico")
    public String historicoFuncionario(HttpSession session, Model model, @RequestParam(required = false) String usuarioCpf, @RequestParam(required = false) String livroId) {
        Object tipo = session.getAttribute("tipoUsuario");
        if (tipo == null || !"FUNCIONARIO".equals(tipo.toString())) return "redirect:/login";

        // obter order items, possivelmente filtrando por usuarioCpf ou livroId
        java.util.List<com.bibliotecassa.trabalho.Services.Pedidos.OrderItem> items;
        if (usuarioCpf != null && !usuarioCpf.isBlank()) {
            items = orderItemRepository.findByOrderUsuarioId(usuarioCpf);
        } else if (livroId != null && !livroId.isBlank()) {
            // ainda não existe um método específico, usar findAll e filtrar
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
            // prefer rentalStart if present, otherwise use order.createdAt if available
            java.time.LocalDateTime dt = oi.getRentalStart();
            if (dt == null && oi.getOrder() != null) dt = oi.getOrder().getCreatedAt();
            m.put("dtObj", dt); // keep object for sorting
            String data = dt != null ? dt.format(fmt) : "-";
            m.put("data", data);
            Integer q = oi.getQuantidade();
            m.put("quantidade", q != null ? q : 1);
            Integer dias = oi.getRentalDays();
            m.put("dias", dias != null ? dias : 0);
            // usuario nome via order.usuarioId
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

        // sort by dtObj desc (newest first), nulls last
        rows.sort((a,b) -> {
            java.time.LocalDateTime da = (java.time.LocalDateTime)a.get("dtObj");
            java.time.LocalDateTime db = (java.time.LocalDateTime)b.get("dtObj");
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });

        // remove dtObj before sending to template (keep formatted data)
        for (java.util.Map<String,Object> m : rows) {
            m.remove("dtObj");
        }

        model.addAttribute("historyRows", rows);
        return "funcionario/historico";
    }
}
