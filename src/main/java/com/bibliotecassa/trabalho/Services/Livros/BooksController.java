package com.bibliotecassa.trabalho.Services.Livros;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Controller
public class BooksController {

    @Autowired
    private BlockedBookRepository blockedBookRepository;

    // use Open Library search API for public book search
    private final String externalApiBase = "https://openlibrary.org/search.json";

    private RestTemplate rest = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/admin/books")
    public String booksMain(HttpSession session, Model model, @RequestParam(required = false) String q, @RequestParam(required = false) String view) {
        // simple admin guard (re-use existing session check pattern)
        Object id = session != null ? session.getAttribute("idUsuario") : null;
        if (id == null) return "redirect:/login";

        // fetch blocked ids
        List<BlockedBook> blocked = blockedBookRepository.findAll();
        Set<String> blockedIds = new HashSet<>();
        for (BlockedBook b : blocked) blockedIds.add(b.getBookId());
        model.addAttribute("blockedIds", blockedIds);

        List<Map<String,Object>> results = new ArrayList<>();
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("view", view == null ? "all" : view);

        if (q != null && !q.isBlank()) {
            try {
                String url = externalApiBase + "?q=" + java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8);
                String resp = rest.getForObject(url, String.class);
                if (resp != null && !resp.isBlank()) {
                    // OpenLibrary returns an object with 'docs' array
                    Map<?,?> top = mapper.readValue(resp, Map.class);
                    Object docsObj = top.get("docs");
                    if (docsObj instanceof List) {
                        List<?> docs = (List<?>) docsObj;
                        for (Object o : docs) {
                            if (o instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String,Object> doc = (Map<String,Object>) o;
                                Map<String,Object> m = new HashMap<>();
                                // prefer cover_edition_key or key as id
                                Object cover = doc.get("cover_edition_key");
                                Object key = doc.get("key");
                                String idVal = cover != null ? cover.toString() : (key != null ? key.toString().replaceAll("^/works/", "") : UUID.randomUUID().toString());
                                m.put("id", idVal);
                                m.put("title", doc.getOrDefault("title", "(sem título)"));
                                Object author = doc.get("author_name");

                                // build cover URL if available (OpenLibrary)
                                String coverUrl = null;
                                Object cover_i = doc.get("cover_i");
                                if (cover != null) {
                                    coverUrl = "https://covers.openlibrary.org/b/olid/" + cover.toString() + "-M.jpg";
                                } else if (cover_i != null) {
                                    coverUrl = "https://covers.openlibrary.org/b/id/" + cover_i.toString() + "-M.jpg";
                                } else if (doc.get("isbn") instanceof List) {
                                    @SuppressWarnings("unchecked")
                                    List<String> isbns = (List<String>) doc.get("isbn");
                                    if (!isbns.isEmpty()) coverUrl = "https://covers.openlibrary.org/b/isbn/" + isbns.get(0) + "-M.jpg";
                                }
                                if (coverUrl != null) m.put("cover", coverUrl);
                                if (author instanceof List) {
                                    @SuppressWarnings("unchecked")
                                    List<String> authors = (List<String>) author;
                                    m.put("author", String.join(", ", authors));
                                } else {
                                    m.put("author", "");
                                }
                                results.add(m);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                model.addAttribute("mensagemErro", "Erro ao consultar a API de livros: " + e.getMessage());
            }
        }

        // depending on view, prepare blocked-only list
        if ("blocked".equalsIgnoreCase(view)) {
            // show blocked books (we don't have full book info locally) — present only ids
            List<Map<String,Object>> blockedList = new ArrayList<>();
            for (BlockedBook b : blocked) {
                Map<String,Object> m = new HashMap<>();
                m.put("id", b.getBookId());
                m.put("title", "(bloqueado) " + b.getBookId());
                blockedList.add(m);
            }
            model.addAttribute("books", blockedList);
        } else {
            model.addAttribute("books", results);
        }

        return "admin/books";
    }

    @PostMapping("/admin/books/block")
    public String blockBook(@RequestParam String bookId, RedirectAttributes ra, HttpSession session) {
        Object id = session != null ? session.getAttribute("idUsuario") : null;
        if (id == null) return "redirect:/login";
        if (bookId == null || bookId.isBlank()) {
            ra.addFlashAttribute("mensagemErro", "Book id inválido");
            return "redirect:/admin/books";
        }
        try {
            if (!blockedBookRepository.existsByBookId(bookId)) {
                blockedBookRepository.save(new BlockedBook(bookId));
            }
            ra.addFlashAttribute("mensagemSucesso", "Livro bloqueado: " + bookId);
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao bloquear livro: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/admin/books/unblock")
    public String unblockBook(@RequestParam String bookId, RedirectAttributes ra, HttpSession session) {
        Object id = session != null ? session.getAttribute("idUsuario") : null;
        if (id == null) return "redirect:/login";
        try {
            BlockedBook b = blockedBookRepository.findByBookId(bookId);
            if (b != null) blockedBookRepository.delete(b);
            ra.addFlashAttribute("mensagemSucesso", "Livro reativado: " + bookId);
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao reativar livro: " + e.getMessage());
        }
        return "redirect:/admin/books?view=blocked";
    }
}
