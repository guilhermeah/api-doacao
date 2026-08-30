package com.doacao.apidoacao.repository;

import com.doacao.apidoacao.model.CategoriaDoacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaDoacaoRepository extends JpaRepository<CategoriaDoacao, Long> {
}
