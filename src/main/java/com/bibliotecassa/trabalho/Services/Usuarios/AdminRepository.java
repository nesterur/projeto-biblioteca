/* ANOTAÇÕES - AdminRepository.java
 * O QUE: Repositório Spring Data para entidade Admin.
 * POR QUE: abstrai operações CRUD sobre administradores.
 * ENTRADAS: métodos de consulta (findBy... etc.).
 * SAÍDAS: entidades Admin retornadas pelo JPA/Hibernate.
 * NOTAS: queries derivadas devem ser nomeadas claramente.
 */
package com.bibliotecassa.trabalho.Services.Usuarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// repositorio pra acessar admin no banco
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByLoginAdmin(String loginAdmin);
}
