package com.doacao.apidoacao.repository;

import com.doacao.apidoacao.model.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {
}