package com.bibliotecassa.trabalho.Services.Pedidos;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	// encontrar itens por usuario atraves da relacao order.usuarioId
	List<OrderItem> findByOrderUsuarioId(String usuarioId);

	// agrega a quantidade de alugueis por livro (livroId, nomeLivro, total)
	@org.springframework.data.jpa.repository.Query("SELECT oi.livroId, oi.nomeLivro, SUM(oi.quantidade) " +
		"FROM OrderItem oi GROUP BY oi.livroId, oi.nomeLivro ORDER BY SUM(oi.quantidade) DESC")
	List<Object[]> findVolumeAlugueisPorLivro();
}
