package com.bibliotecassa.trabalho.Services.Livros;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// faz as requisições pra api do google books e monta os objetos livro
@Service
public class ServiceLivro {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLivro.class);
    // (no extra cache fields)
    // objetivo
    // service responsavel por conversar com google books montar modellivro e atribuir preco
    /**
     * Busca uma lista de livros a partir de uma lista de Google Books IDs.
     */
    // busca uma lista de livros a partir de uma lista de ids do google books
    public List<ModelLivro> buscarLivrosPorIds(List<String> ids) {
        List<ModelLivro> livros = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        for (String id : ids) {
            String url = "https://www.googleapis.com/books/v1/volumes/" + id + "?key=" + googleBooksApiKey;
            try {
                ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
                if (response.getBody() != null && response.getBody().containsKey("volumeInfo")) {
                    Map<String, Object> volumeInfo = (Map<String, Object>) response.getBody().get("volumeInfo");
                    String titulo = (String) volumeInfo.getOrDefault("title", "Sem título");
                    List<String> autores = (List<String>) volumeInfo.get("authors");
                    String autor = (autores != null && !autores.isEmpty()) ? autores.get(0) : "Autor desconhecido";
                    List<String> categorias = (List<String>) volumeInfo.get("categories");
                    String categoria = (categorias != null && !categorias.isEmpty()) ? categorias.get(0) : "Sem categoria";
                    String descricao = (String) volumeInfo.getOrDefault("description", "Sem descrição");
                    Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.get("imageLinks");
                    String urlCapa = (imageLinks != null) ? (String) imageLinks.getOrDefault("thumbnail", "") : "";
                    logger.debug("buscarLivrosPorIds - id='{}' imageLinks={} urlCapa={}", id, imageLinks, urlCapa);
                    ModelLivro ml = new ModelLivro(id, titulo, autor, categoria, descricao, urlCapa);
                    // attempt to extract saleInfo (price) from response
                    Map<String, Object> saleInfo = (Map<String, Object>) response.getBody().get("saleInfo");
                    applyPriceFromSaleInfo(saleInfo, ml, id);
                    livros.add(ml);
                }
            } catch (Exception e) {
                // Se algum livro não for encontrado, registra e continua
                logger.warn("buscarLivrosPorIds - erro ao buscar id={}: {}", id, e.toString());
            }
        }
        return livros;
    }

    // Helper: extract price info from saleInfo map or generate deterministic price
    @SuppressWarnings("unchecked")
    private void applyPriceFromSaleInfo(Map<String, Object> saleInfo, ModelLivro ml, String id) {
        try {
            if (saleInfo != null) {
                // prefer retailPrice over listPrice
                Map<String, Object> priceMap = (Map<String, Object>) saleInfo.get("retailPrice");
                if (priceMap == null) priceMap = (Map<String, Object>) saleInfo.get("listPrice");
                String saleability = (String) saleInfo.get("saleability");
                if (priceMap != null && priceMap.get("amount") != null) {
                    try {
                        Double amount = Double.valueOf(priceMap.get("amount").toString());
                        String currency = (String) priceMap.getOrDefault("currencyCode", "BRL");
                        ml.setPreco(amount);
                        ml.setMoeda(currency);
                        return;
                    } catch (Exception e) {
                        logger.debug("Erro ao parsear priceMap amount: {}", e.toString());
                    }
                }
                if (saleability != null && saleability.equalsIgnoreCase("FREE")) {
                    ml.setPreco(0.0);
                    ml.setMoeda("BRL");
                    return;
                }
            }
        } catch (Exception ex) {
            logger.debug("applyPriceFromSaleInfo falhou para id={}: {}", id, ex.toString());
        }

        // deterministic fallback: map id hash into range 40.99 .. 149.99 (in cents: 4099..14999)
        int minCents = 4099;
        int maxCents = 14999;
        int range = maxCents - minCents + 1;
        int hash = (id != null) ? Math.abs(id.hashCode()) : (int) (System.currentTimeMillis() & 0x7fffffff);
        int cents = minCents + (hash % range);
        double price = cents / 100.0;
        ml.setPreco(price);
        ml.setMoeda("BRL");
    }
    @Value("${google.books.api.url}")
    private String googleBooksApiUrl;

    @Value("${google.books.api.key}")
    private String googleBooksApiKey;

    // busca livros na api do google books usando uma query
    public List<ModelLivro> listarLivros(String query) {
        // Adiciona maxResults=40 para trazer até 40 livros
    String url = googleBooksApiUrl + "?q=" + query + "&key=" + googleBooksApiKey + "&maxResults=40&orderBy=relevance";

        RestTemplate restTemplate = new RestTemplate();

        // GE e resposta como um map
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Lista para armazenar os livros convertidos
        List<ModelLivro> livros = new ArrayList<>();

        // Verifica se a resposta contém itens
        if (response.getBody() != null && response.getBody().containsKey("items")) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
            for (Map<String, Object> item : items) {
                Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");
                String titulo = (String) volumeInfo.getOrDefault("title", "Sem título");
                // Pega o primeiro autor, se existir
                List<String> autores = (List<String>) volumeInfo.get("authors");
                String autor = (autores != null && !autores.isEmpty()) ? autores.get(0) : "Autor desconhecido";
                // Pega a primeira categoria, se existir
                List<String> categorias = (List<String>) volumeInfo.get("categories");
                String categoria = (categorias != null && !categorias.isEmpty()) ? categorias.get(0) : "Sem categoria";
                String descricao = (String) volumeInfo.getOrDefault("description", "Sem descrição");
                //URL da capa, se existir
                Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.get("imageLinks");
                String urlCapa = (imageLinks != null) ? (String) imageLinks.getOrDefault("thumbnail", "") : "";
                logger.debug("listarLivros - titulo='{}' imageLinks={} urlCapa={}", titulo, imageLinks, urlCapa);
                // Cria o DTO ModelLivro e adiciona à lista
                Double averageRating = null;
                if (volumeInfo.get("averageRating") != null) {
                    try {
                        averageRating = Double.valueOf(volumeInfo.get("averageRating").toString());
                    } catch (Exception e) { /* ignora erro de conversão */ }
                }
                String idLivro = (String) item.get("id");
                logger.debug("listarLivros(paginado) - titulo='{}' id='{}' imageLinks={} urlCapa={}", titulo, idLivro, imageLinks, urlCapa);
                ModelLivro ml = new ModelLivro(idLivro, titulo, autor, categoria, descricao, urlCapa);
                // try to extract saleInfo from this item entry
                Map<String, Object> saleInfoItem = (Map<String, Object>) item.get("saleInfo");
                applyPriceFromSaleInfo(saleInfoItem, ml, idLivro);
                livros.add(ml);
            }
        }
        // filtra livros incompletos
            livros.removeIf(livro -> 
                livro.getUrlCapa() == null || livro.getUrlCapa().isEmpty() ||
                livro.getTitulo() == null || livro.getAutor() == null
            );
        return livros;
    }

    public class ResultadoPaginacao{
        private List<ModelLivro> livros;
        private int paginaAtual;
        private int totalPaginas;

        public ResultadoPaginacao(List<ModelLivro> livros, int paginaAtual, int totalPaginas) {
            this.livros = livros;
            this.paginaAtual = paginaAtual;
            this.totalPaginas = totalPaginas;
        }

        public List<ModelLivro> getLivros() { return livros; }
        public int getPaginaAtual() { return paginaAtual; }
        public int getTotalPaginas() { return totalPaginas; }
    }

    // busca livros paginados na api do google books
    public ResultadoPaginacao listarLivros(String query, int pagina, int tamanhoPagina) {
        // Sempre garantir 12 livros por página
        tamanhoPagina = 12;
        int startIndex = (pagina - 1) * tamanhoPagina;
        String url = googleBooksApiUrl + "?q=" + query + "&key=" + googleBooksApiKey +
            "&maxResults=" + tamanhoPagina + "&startIndex=" + startIndex + "&orderBy=relevance";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        List<ModelLivro> livros = new ArrayList<>();
        int totalItems = 0;

        if (response.getBody() != null) {
            // Pega a lista de livros
            if (response.getBody().containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
                for (Map<String, Object> item : items) {
                    Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");
                    String titulo = (String) volumeInfo.getOrDefault("title", "Sem título");
                    List<String> autores = (List<String>) volumeInfo.get("authors");
                    String autor = (autores != null && !autores.isEmpty()) ? autores.get(0) : "Autor desconhecido";
                    List<String> categorias = (List<String>) volumeInfo.get("categories");
                    String categoria = (categorias != null && !categorias.isEmpty()) ? categorias.get(0) : "Sem categoria";
                    String descricao = (String) volumeInfo.getOrDefault("description", "Sem descrição");
                    Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.get("imageLinks");
                    String urlCapa = (imageLinks != null) ? (String) imageLinks.getOrDefault("thumbnail", "") : "";
                    String idLivro = (String) item.get("id");
                    ModelLivro ml = new ModelLivro(idLivro, titulo, autor, categoria, descricao, urlCapa);
                    Map<String, Object> saleInfoItem = (Map<String, Object>) item.get("saleInfo");
                    applyPriceFromSaleInfo(saleInfoItem, ml, idLivro);
                    livros.add(ml);
                }
            }
            // Pega o total de livros encontrados
            if (response.getBody().containsKey("totalItems")) {
                totalItems = ((Number) response.getBody().get("totalItems")).intValue();
            }
        }

        int totalPaginas = (int) Math.ceil((double) totalItems / tamanhoPagina);
        // Não limitar o número de páginas
        // filtra livros incompletos
        livros.removeIf(livro -> 
            livro.getUrlCapa() == null || livro.getUrlCapa().isEmpty() ||
            livro.getTitulo() == null || livro.getAutor() == null
        );
        logger.debug("listarLivros(paginado) - total apos filtro={}", livros.size());

        return new ResultadoPaginacao(livros, pagina, totalPaginas);
    }

    // no getAllCategories - revert to original behavior
}
