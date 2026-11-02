package com.doacao.apidoacao.gateway;

import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.repository.DoacaoRepository;
import com.doacao.apidoacao.service.DoacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Gateway de pagamento simulado — substitui o webhook de um PSP real durante demos.
 *
 * Ativo quando: pix.gateway=simulado (padrão).
 * Para desativar: setar PIX_GATEWAY=asaas (ou qualquer gateway real) em produção.
 *
 * Em produção com gateway real, esta classe não é instanciada.
 */
@Component
@ConditionalOnProperty(name = "pix.gateway", havingValue = "simulado", matchIfMissing = true)
public class GatewaySimulado implements GatewayPagamento {

    private static final Logger log = LoggerFactory.getLogger(GatewaySimulado.class);

    private final DoacaoRepository doacaoRepository;
    private final DoacaoService doacaoService;

    @Value("${pix.simulacao.segundos:20}")
    private long segundosParaConfirmar;

    public GatewaySimulado(DoacaoRepository doacaoRepository, DoacaoService doacaoService) {
        this.doacaoRepository = doacaoRepository;
        this.doacaoService = doacaoService;
    }

    @Override
    public String getNome() {
        return "simulado";
    }

    @Override
    public boolean usaProcessamentoPeriodico() {
        return true;
    }

    @Override
    public void processarConfirmacoesPendentes() {
        LocalDateTime limite = LocalDateTime.now().minusSeconds(segundosParaConfirmar);
        List<Doacao> pendentes = doacaoRepository.buscarPendentesCriadasAntesDe(limite);
        if (pendentes.isEmpty()) return;

        for (Doacao doacao : pendentes) {
            Long id = doacao.getIdDoacao();
            try {
                doacaoService.confirmarPagamento(id);
                log.info("[{}] Doacao {} confirmada apos {}s", getNome(), id, segundosParaConfirmar);
            } catch (Exception e) {
                log.warn("[{}] Nao foi possivel confirmar a doacao {}: {}", getNome(), id, e.getMessage());
            }
        }
    }
}
