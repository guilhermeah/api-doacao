package com.doacao.apidoacao.repository;

import com.doacao.apidoacao.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByOngIdOngOrderByDataCriacaoDesc(Long idOng);

    long countByOngIdOngAndLidaFalse(Long idOng);
}
