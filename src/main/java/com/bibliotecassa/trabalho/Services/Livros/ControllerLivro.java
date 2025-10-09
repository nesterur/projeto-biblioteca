package com.bibliotecassa.trabalho.Services.Livros;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ControllerLivro {

    private final ServiceLivro serviceLivro;

    public ControllerLivro(ServiceLivro serviceLivro) {
        this.serviceLivro = serviceLivro;
    }

    @GetMapping("/livros")
    public String listarLivros(
            @RequestParam(name = "busca", required = false) String busca,
            @RequestParam(name = "pagina", required = false, defaultValue = "1") int pagina,
            @RequestParam(name = "tamanho", required = false, defaultValue = "16") int tamanho,
            Model model) {
        if (busca == null) busca = "";
        ServiceLivro.ResultadoPaginacao resultado = serviceLivro.listarLivros(busca, pagina, tamanho);
        int paginasExibir = resultado.getTotalPaginas() > 10 ? 10 : resultado.getTotalPaginas();
        model.addAttribute("livros", resultado.getLivros());
        model.addAttribute("pagina", resultado.getPaginaAtual());
        model.addAttribute("totalPaginas", resultado.getTotalPaginas());
        model.addAttribute("paginasExibir", paginasExibir);
        model.addAttribute("busca", busca);
        model.addAttribute("tamanho", tamanho);
        return "dashboard";
    }
}