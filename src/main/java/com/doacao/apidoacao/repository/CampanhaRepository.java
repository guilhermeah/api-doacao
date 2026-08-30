package com.doacao.apidoacao.repository;

import com.doacao.apidoacao.model.Campanha;
import com.doacao.apidoacao.model.Ong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampanhaRepository extends JpaRepository<Campanha, Long> {

    List<Campanha> findByStatus(String status);

    List<Campanha> findByCategoria(String categoria);

    List<Campanha> findByOng(Ong ong);

    List<Campanha> findByTituloContainingIgnoreCase(String titulo);

    long countByStatus(String status);
}
