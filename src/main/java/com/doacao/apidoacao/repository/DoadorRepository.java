package com.doacao.apidoacao.repository;

import com.doacao.apidoacao.model.Doador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoadorRepository extends JpaRepository<Doador, Long> {
    Optional<Doador> findByCpfCnpj(String cpfCnpj);
}