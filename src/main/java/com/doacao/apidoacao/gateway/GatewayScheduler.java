package com.doacao.apidoacao.gateway;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Dispara periodicamente o processamento de confirmações de pagamento.
 * Só faz algo quando o gateway ativo usa polling (ex: GatewaySimulado).
 * Gateways que operam via webhook ignoram a chamada silenciosamente.
 */
@Component
public class GatewayScheduler {

    private final GatewayPagamento gateway;

    public GatewayScheduler(GatewayPagamento gateway) {
        this.gateway = gateway;
    }

    @Scheduled(fixedDelay = 5_000L)
    public void tick() {
        if (gateway.usaProcessamentoPeriodico()) {
            gateway.processarConfirmacoesPendentes();
        }
    }
}
