package com.bibliotecassa.trabalho.Services.Pedidos;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
	// encontrar orders por usuario
	List<Order> findByUsuarioId(String usuarioId);
}
