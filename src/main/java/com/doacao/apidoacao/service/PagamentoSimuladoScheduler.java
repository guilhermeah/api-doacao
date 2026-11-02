package com.doacao.apidoacao.service;

import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.repository.DoacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SIMULACAO DE PAGAMENTO PIX (modo demonstracao).
 *
 * O QR Code gerado pelo PixService e um BR Code estatico: o dinheiro cai direto
 * na conta da ONG e nenhum banco ou PSP avisa a API de que o PIX foi pago. Para
 * a demonstracao, esta classe faz o papel desse aviso e confirma sozinha toda
 * doacao financeira pendente que ja passou de `pix.simulacao.segundos`.
 *
 * Quando entrar uma integracao de verdade (webhook de PSP), e so desligar em
 * application.properties (pix.simulacao.ativa=false) e apagar esta classe.
 */
@Component
public class PagamentoSimuladoScheduler {

    private static final Logger log = LoggerFactory.getLogger(PagamentoSimuladoScheduler.class);

    /** De quanto em quanto tempo o agendador procura doacoes pendentes. */
    private static final long INTERVALO_MS = 5_000L;

    private final DoacaoRepository doacaoRepository;
    private final DoacaoService doacaoService;

    @Value("${pix.simulacao.ativa:true}")
    private boolean ativa;

    @Value("${pix.simulacao.segundos:40}")
    private long segundosParaConfirmar;

    public PagamentoSimuladoScheduler(DoacaoRepository doacaoRepository, DoacaoService doacaoService) {
        this.doacaoRepository = doacaoRepository;
        this.doacaoService = doacaoService;
    }

    @Scheduled(fixedDelay = INTERVALO_MS)
    public void confirmarPagamentosSimulados() {
        if (!ativa) return;

        LocalDateTime limite = LocalDateTime.now().minusSeconds(segundosParaConfirmar);
        List<Doacao> pendentes = doacaoRepository.buscarPendentesCriadasAntesDe(limite);
        if (pendentes.isEmpty()) return;

        for (Doacao doacao : pendentes) {
            Long id = doacao.getIdDoacao();
            try {
                doacaoService.confirmarPagamento(id);
                log.info("[PIX simulado] Doacao {} confirmada automaticamente apos {}s", id, segundosParaConfirmar);
            } catch (Exception e) {
                // doacao ja expirada ou em estado invalido: segue o baile
                log.warn("[PIX simulado] Nao foi possivel confirmar a doacao {}: {}", id, e.getMessage());
            }
        }
    }
}
