package com.bibliotecassa.trabalho.Services.CarrinhoCompras;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.bibliotecassa.trabalho.Services.CarrinhoCompras.CarrinhoItem;
import com.bibliotecassa.trabalho.Services.Livros.ServiceLivro;
import com.bibliotecassa.trabalho.Services.Livros.ModelLivro;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ControllerCarrinho {

    private static final Logger logger = LoggerFactory.getLogger(ControllerCarrinho.class);

    private final ServiceCarrinho serviceCarrinho;
    private final ServiceLivro serviceLivro;

    public ControllerCarrinho(ServiceCarrinho serviceCarrinho, ServiceLivro serviceLivro) {
        this.serviceCarrinho = serviceCarrinho;
        this.serviceLivro = serviceLivro;
    }

    // objetivo
    // controller da pagina de carrinho mostrar itens calcular total permitir remover


    @PostMapping("/carrinho/adicionar")
    @ResponseBody
    public ResponseEntity<?> adicionarItemCarrinho(
            @RequestParam("idLivro") String idLivro,
            @RequestParam("idUsuario") String idUsuario
    ) {
        logger.info("Recebendo requisicao adicionarItemCarrinho - idUsuario='{}' idLivro='{}'", idUsuario, idLivro);
        try {
            serviceCarrinho.adicionarItemCarrinho(idLivro, idUsuario);
            // Retorna 200 OK sem redirecionar (para AJAX)
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Log and return error to AJAX caller for easier debugging
            logger.error("Erro ao adicionar ao carrinho: {}", e.toString(), e);
            return ResponseEntity.status(500).body("Erro ao adicionar ao carrinho: " + e.getMessage());
        }
    }


    @GetMapping("/carrinho")
    public String mostrarCarrinho(@RequestParam(name = "idUsuario", required = false) String idUsuario, Model model, HttpSession session) {
        // fallback: try to get idUsuario from session if not passed as query param
        if (idUsuario == null || idUsuario.isBlank()) {
            Object sessId = session.getAttribute("idUsuario");
            if (sessId != null) {
                idUsuario = sessId.toString();
            }
        }

        if (idUsuario == null || idUsuario.isBlank()) {
            // no user id available: redirect to login (or homepage) — adjust as you prefer
            return "redirect:/login";
        }

        List<CarrinhoItem> itens = serviceCarrinho.obterCarrinhoPorUsuario(idUsuario);
        model.addAttribute("itens", itens);
        model.addAttribute("idUsuario", idUsuario);

        // Build a map of livroId -> ModelLivro so the template can show prices
        Map<String, ModelLivro> livrosMap = new HashMap<>();
    double total = 0.0;
    Map<String, String> priceByLivroId = new HashMap<>();
        try {
            Set<String> ids = itens.stream().map(CarrinhoItem::getLivroId).collect(Collectors.toSet());
            if (!ids.isEmpty()) {
                // ask ServiceLivro for info about all ids
                java.util.List<ModelLivro> livros = serviceLivro.buscarLivrosPorIds(new ArrayList<>(ids));
                if (livros != null) {
                    for (ModelLivro ml : livros) {
                        if (ml != null && ml.getIdLivro() != null) {
                            livrosMap.put(ml.getIdLivro(), ml);
                        }
                    }
                    // compute total and prepare formatted price per livroId
                    java.text.DecimalFormat itemDf = new java.text.DecimalFormat("#,##0.00");
                    for (CarrinhoItem it : itens) {
                        // prefer stored price on the item (persisted) if present
                        if (it.getPreco() != null) {
                            total += it.getPreco().doubleValue();
                            priceByLivroId.put(it.getLivroId(), "R$ " + itemDf.format(it.getPreco()));
                        } else {
                            ModelLivro ml = livrosMap.get(it.getLivroId());
                            if (ml != null && ml.getPreco() != null) {
                                total += ml.getPreco();
                                priceByLivroId.put(it.getLivroId(), ml.getPrecoFormatado());
                            } else {
                                priceByLivroId.put(it.getLivroId(), "Preço não disponível");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Erro ao obter preços para itens do carrinho: {}", e.toString());
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String totalFormatado = "R$ " + df.format(total);
        model.addAttribute("livrosMap", livrosMap);
    model.addAttribute("priceByLivroId", priceByLivroId);
        model.addAttribute("totalCarrinho", total);
        model.addAttribute("totalFormatado", totalFormatado);
        return "carrinho";
    }

    @PostMapping("/carrinho/remover")
    public String removerItemCarrinho(@RequestParam("idLivro") String idLivro, @RequestParam(name = "idUsuario", required = false) String idUsuario, HttpSession session) {
        if (idUsuario == null || idUsuario.isBlank()) {
            Object sessId = session.getAttribute("idUsuario");
            if (sessId != null) {
                idUsuario = sessId.toString();
            }
        }

        if (idUsuario == null || idUsuario.isBlank()) {
            return "redirect:/login";
        }

        serviceCarrinho.removerItemCarrinho(idLivro, idUsuario);
        return "redirect:/carrinho?idUsuario=" + idUsuario;
    }
}