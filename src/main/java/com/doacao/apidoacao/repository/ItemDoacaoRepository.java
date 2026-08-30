package com.doacao.apidoacao.repository;

import com.doacao.apidoacao.model.CategoriaDoacao;
import com.doacao.apidoacao.model.ItemDoacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemDoacaoRepository extends JpaRepository<ItemDoacao, Long> {
    List<ItemDoacao> findByCategoria(CategoriaDoacao categoria);
}
