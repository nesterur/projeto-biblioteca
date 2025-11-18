package com.bibliotecassa.trabalho.Services.Pedidos;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MeusLivrosController {

    // pagina meus livros divide em ativos e expirados

    private final OrderItemRepository orderItemRepository;

    public MeusLivrosController(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/meuslivros")
    public String meusLivros(@RequestParam(name = "idUsuario", required = false) String usuarioId, Model model, jakarta.servlet.http.HttpSession session) {
        // se idUsuario nao for passado via param, tenta ler da sessao
        if (usuarioId == null || usuarioId.isBlank()) {
            Object s = session != null ? session.getAttribute("idUsuario") : null;
            if (s != null) usuarioId = s.toString();
        }

        if (usuarioId == null || usuarioId.isBlank()) {
            model.addAttribute("error", "usuario nao especificado");
            model.addAttribute("ativos", List.of());
            model.addAttribute("expirados", List.of());
            return "meuslivros";
        }

        List<OrderItem> itens = orderItemRepository.findByOrderUsuarioId(usuarioId);
        List<OrderItem> ativos = new ArrayList<>();
        List<OrderItem> expirados = new ArrayList<>();

        LocalDateTime agora = LocalDateTime.now();
        if (itens != null) {
            for (OrderItem oi : itens) {
                if (oi.getRentalEnd() != null && oi.getRentalEnd().isBefore(agora)) {
                    expirados.add(oi);
                } else {
                    ativos.add(oi);
                }
            }
        }

        model.addAttribute("ativos", ativos);
        model.addAttribute("expirados", expirados);
        model.addAttribute("idUsuario", usuarioId);
        return "meuslivros";
    }
}
