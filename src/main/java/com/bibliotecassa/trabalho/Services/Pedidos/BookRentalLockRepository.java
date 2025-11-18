package com.bibliotecassa.trabalho.Services.Pedidos;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface BookRentalLockRepository extends JpaRepository<BookRentalLock, Long> {
    List<BookRentalLock> findByLivroIdAndRentedUntilAfter(String livroId, LocalDateTime now);
}
