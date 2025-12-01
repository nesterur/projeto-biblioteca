package com.bibliotecassa.trabalho.Services.Pedidos;
// arquivo CheckoutController.java
// finalidade classe CheckoutController comentarios automatizados

import com.bibliotecassa.trabalho.Services.CarrinhoCompras.CarrinhoItem;
import com.bibliotecassa.trabalho.Services.CarrinhoCompras.ServiceCarrinho;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
// definicao de class nome CheckoutController
public class CheckoutController {

    
    

    private final ServiceCarrinho serviceCarrinho;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    
    private static final BigDecimal PCT_7 = new BigDecimal("0.10");
    private static final BigDecimal PCT_14 = new BigDecimal("0.18");
    private static final BigDecimal PCT_30 = new BigDecimal("0.30");

    public CheckoutController(ServiceCarrinho serviceCarrinho,
                              OrderRepository orderRepository,
                              OrderItemRepository orderItemRepository) {
        this.serviceCarrinho = serviceCarrinho;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/checkout")

    public String showCheckout(@RequestParam(name = "idUsuario") String usuarioId, Model model) {
        List<CarrinhoItem> itens = serviceCarrinho.obterCarrinhoPorUsuario(usuarioId);
        model.addAttribute("itens", itens);
        model.addAttribute("idUsuario", usuarioId);
        return "checkout";
    }

    @PostMapping("/checkout/process")
    public String processCheckout(@RequestParam("idUsuario") String usuarioId,
                                  @RequestParam("rentalDays") Integer rentalDays,
                                  @RequestParam("metodoPagamento") String metodoPagamento,
                                  Model model) {

        List<CarrinhoItem> itens = serviceCarrinho.obterCarrinhoPorUsuario(usuarioId);
        if (itens == null || itens.isEmpty()) {
            model.addAttribute("message", "Seu carrinho está vazio.");
            return "checkout_success";
        }

        LocalDateTime now = LocalDateTime.now();
        List<OrderItem> savedItems = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        BigDecimal pct;
        if (rentalDays == 7) pct = PCT_7;
        else if (rentalDays == 14) pct = PCT_14;
        else pct = PCT_30;

        Order order = new Order();
        order.setUsuarioId(usuarioId);
        order.setCurrency(itens.get(0).getMoeda() != null ? itens.get(0).getMoeda() : "BRL");
        order.setMetodoPagamento(metodoPagamento);
        order.setStatus("RENTED");
        order.setCreatedAt(now);

        
        for (CarrinhoItem ci : itens) {
            BigDecimal preco = ci.getPreco() != null ? ci.getPreco() : BigDecimal.ZERO;
            
            BigDecimal rentalPrice = preco.multiply(pct).setScale(2, java.math.RoundingMode.HALF_UP);
            total = total.add(rentalPrice);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setLivroId(ci.getLivroId());
            oi.setNomeLivro(ci.getNomeLivro());
            oi.setPreco(preco);
            oi.setRentalDays(rentalDays);
            oi.setRentalStart(now);
            oi.setRentalEnd(now.plusDays(rentalDays));
            oi.setRentalPrice(rentalPrice);
            oi.setQuantidade(1);

            savedItems.add(oi);
        }

        order.setTotal(total);
        orderRepository.save(order);

        
        for (OrderItem oi : savedItems) {
            oi.setOrder(order);
            orderItemRepository.save(oi);
        }

        
        model.addAttribute("orderItems", savedItems);

        
        serviceCarrinho.limparCarrinhoDoUsuario(usuarioId);

        model.addAttribute("orderId", order.getId());
        model.addAttribute("totalFormatado", String.format("R$ %.2f", total));
        return "checkout_success";
    }
}





