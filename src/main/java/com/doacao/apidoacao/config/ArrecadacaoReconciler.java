package com.doacao.apidoacao.config;

import com.doacao.apidoacao.model.Campanha;
import com.doacao.apidoacao.repository.CampanhaRepository;
import com.doacao.apidoacao.service.CampanhaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Na subida da API, reescreve campanhas.valor_arrecadado e quantidade_doadores
 * a partir da tabela de doacoes.
 *
 * Serve de rede de seguranca para qualquer desalinhamento herdado — total
 * escrito na mao, doacao apagada direto no banco, importacao antiga. Depois
 * disso, o proprio DoacaoService mantem os campos recalculados a cada evento,
 * entao em condicoes normais este reconciliador nao muda nada.
 */
@Component
@Order(4)
public class ArrecadacaoReconciler implements CommandLineRunner {

    private final CampanhaRepository campanhaRepository;
    private final CampanhaService campanhaService;

    public ArrecadacaoReconciler(CampanhaRepository campanhaRepository, CampanhaService campanhaService) {
        this.campanhaRepository = campanhaRepository;
        this.campanhaService = campanhaService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Campanha> campanhas = campanhaRepository.findAll();
        int ajustadas = 0;

        for (Campanha campanha : campanhas) {
            BigDecimal antes = campanha.getValorArrecadado() != null ? campanha.getValorArrecadado() : BigDecimal.ZERO;
            Integer doadoresAntes = campanha.getQuantidadeDoadores();

            campanhaService.recalcularArrecadacao(campanha);

            BigDecimal depois = campanha.getValorArrecadado() != null ? campanha.getValorArrecadado() : BigDecimal.ZERO;
            boolean mudouValor = antes.compareTo(depois) != 0;
            boolean mudouDoadores = doadoresAntes == null
                    || !doadoresAntes.equals(campanha.getQuantidadeDoadores());

            if (mudouValor || mudouDoadores) {
                ajustadas++;
                System.out.println("[ArrecadacaoReconciler] Campanha " + campanha.getIdCampanha()
                        + " — valor: " + antes + " → " + depois
                        + " | doadores: " + doadoresAntes + " → " + campanha.getQuantidadeDoadores());
            }
        }

        System.out.println("[ArrecadacaoReconciler] " + campanhas.size()
                + " campanha(s) verificadas, " + ajustadas + " ajustada(s).");
    }
}
