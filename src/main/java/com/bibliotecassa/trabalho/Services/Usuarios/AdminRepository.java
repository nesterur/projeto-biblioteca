
package com.bibliotecassa.trabalho.Services.Usuarios;
// arquivo AdminRepository.java
// finalidade classe AdminRepository comentarios automatizados

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// definicao de interface nome AdminRepository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByLoginAdmin(String loginAdmin);
}


