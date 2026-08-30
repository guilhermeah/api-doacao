package com.doacao.apidoacao.repository;

import com.doacao.apidoacao.model.Campanha;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.Doador;
import com.doacao.apidoacao.model.Ong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {

    boolean existsByCampanhaAndDoador(Campanha campanha, Doador doador);

    List<Doacao> findByDoadorIdDoador(Long idDoador);

    List<Doacao> findByOng(Ong ong);

    long countByOng(Ong ong);

    @Query("SELECT COALESCE(SUM(d.valorTotal), 0) FROM Doacao d")
    BigDecimal somarValorArrecadado();

    @Query("SELECT COALESCE(SUM(d.valorTotal), 0) FROM Doacao d WHERE d.ong = :ong")
    BigDecimal somarValorArrecadadoPorOng(Ong ong);

    @Query("SELECT COUNT(DISTINCT d.doador.idDoador) FROM Doacao d")
    long contarDonadoresDistintos();
}