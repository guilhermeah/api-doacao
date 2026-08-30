package com.doacao.apidoacao.repository;

import com.doacao.apidoacao.dto.CampanhaItemDoadoDTO;
import com.doacao.apidoacao.model.DoacaoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoacaoItemRepository extends JpaRepository<DoacaoItem, Long> {

    List<DoacaoItem> findByDoacaoIdDoacao(Long idDoacao);

    @Query("SELECT new com.doacao.apidoacao.dto.CampanhaItemDoadoDTO(" +
           "di.item.idItem, di.item.nome, di.item.categoria.nome, di.item.unidadeMedida, SUM(di.quantidade)) " +
           "FROM DoacaoItem di " +
           "WHERE di.doacao.campanha.idCampanha = :idCampanha " +
           "GROUP BY di.item.idItem, di.item.nome, di.item.categoria.nome, di.item.unidadeMedida " +
           "ORDER BY di.item.categoria.nome, di.item.nome")
    List<CampanhaItemDoadoDTO> somarItensPorCampanha(@Param("idCampanha") Long idCampanha);
}
