package com.doacao.apidoacao.repository;

import com.doacao.apidoacao.model.Campanha;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.Doador;
import com.doacao.apidoacao.model.Ong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {

    /**
     * Doação "contabilizada" = já entrou no total da campanha.
     * Fica de fora a financeira que ainda não foi paga (pendente), a que
     * venceu o prazo do PIX (expirada) e a cancelada.
     */
    String NAO_CONTABILIZADAS = "('pendente', 'expirada', 'cancelada')";

    boolean existsByCampanhaAndDoador(Campanha campanha, Doador doador);

    List<Doacao> findByDoadorIdDoador(Long idDoador);

    List<Doacao> findByOng(Ong ong);

    long countByOng(Ong ong);

    @Query("SELECT COUNT(d) FROM Doacao d WHERE d.campanha = :campanha AND d.doador = :doador "
            + "AND LOWER(COALESCE(d.statusDoacao, '')) NOT IN " + NAO_CONTABILIZADAS)
    long contarDoacoesContabilizadas(@Param("campanha") Campanha campanha, @Param("doador") Doador doador);

    @Query("SELECT COUNT(d) FROM Doacao d WHERE d.campanha = :campanha AND d.doador = :doador "
            + "AND d.idDoacao <> :idDoacao "
            + "AND LOWER(COALESCE(d.statusDoacao, '')) NOT IN " + NAO_CONTABILIZADAS)
    long contarOutrasDoacoesContabilizadas(@Param("campanha") Campanha campanha,
                                           @Param("doador") Doador doador,
                                           @Param("idDoacao") Long idDoacao);

    @Query("SELECT d FROM Doacao d WHERE LOWER(COALESCE(d.statusDoacao, '')) = 'pendente' "
            + "AND d.dataDoacao < :limite")
    List<Doacao> buscarPendentesVencidas(@Param("limite") LocalDateTime limite);

    /** Doações financeiras ainda pendentes criadas antes de :limite. */
    @Query("SELECT d FROM Doacao d WHERE LOWER(COALESCE(d.statusDoacao, '')) = 'pendente' "
            + "AND d.dataDoacao < :limite")
    List<Doacao> buscarPendentesCriadasAntesDe(@Param("limite") LocalDateTime limite);

    @Query("SELECT COALESCE(SUM(d.valorTotal), 0) FROM Doacao d "
            + "WHERE LOWER(COALESCE(d.statusDoacao, '')) NOT IN " + NAO_CONTABILIZADAS)
    BigDecimal somarValorArrecadado();

    @Query("SELECT COALESCE(SUM(d.valorTotal), 0) FROM Doacao d WHERE d.ong = :ong "
            + "AND LOWER(COALESCE(d.statusDoacao, '')) NOT IN " + NAO_CONTABILIZADAS)
    BigDecimal somarValorArrecadadoPorOng(@Param("ong") Ong ong);

    @Query("SELECT COUNT(DISTINCT d.doador.idDoador) FROM Doacao d "
            + "WHERE LOWER(COALESCE(d.statusDoacao, '')) NOT IN " + NAO_CONTABILIZADAS)
    long contarDonadoresDistintos();

    // ── Base do recalculo de campanhas.valor_arrecadado ──────────────────────
    // A tabela campanhas guarda o total so para leitura rapida; a verdade e a
    // soma das doacoes contabilizadas. Estas duas consultas sao o que
    // CampanhaService.recalcularArrecadacao() usa para reescrever o total.

    @Query("SELECT COALESCE(SUM(d.valorTotal), 0) FROM Doacao d WHERE d.campanha = :campanha "
            + "AND LOWER(COALESCE(d.statusDoacao, '')) NOT IN " + NAO_CONTABILIZADAS)
    BigDecimal somarValorArrecadadoPorCampanha(@Param("campanha") Campanha campanha);

    @Query("SELECT COUNT(DISTINCT d.doador.idDoador) FROM Doacao d WHERE d.campanha = :campanha "
            + "AND LOWER(COALESCE(d.statusDoacao, '')) NOT IN " + NAO_CONTABILIZADAS)
    long contarDoadoresDistintosPorCampanha(@Param("campanha") Campanha campanha);

    List<Doacao> findByCampanha(Campanha campanha);
}