/* ANOTAÇÕES - UsuarioComumRepository.java
 * O QUE: Repositório para entidade UsuarioComum (clientes comuns).
 * POR QUE: encapsula consultas JPA para usuários da biblioteca.
 * ENTRADAS: métodos de busca (ex: findByCpf, findById).
 * SAÍDAS: objetos UsuarioComum retornados pelo banco de dados.
 * NOTAS: manter índices em colunas pesquisadas como CPF para performance.
 */
package com.bibliotecassa.trabalho.Services.Usuarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// repositorio pra acessar usuario comum no banco
@Repository
public interface UsuarioComumRepository extends JpaRepository<UsuarioComum, Integer> {
    UsuarioComum findByCpf(String cpf);
}
