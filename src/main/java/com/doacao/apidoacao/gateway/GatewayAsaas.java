package com.doacao.apidoacao.gateway;

import com.doacao.apidoacao.service.DoacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Gateway Asaas — confirmação de PIX via webhook.
 *
 * Ativo quando: pix.gateway=asaas (setar PIX_GATEWAY=asaas em produção).
 *
 * Integração pendente:
 *   1. Criar conta Asaas e gerar chave de API em app.asaas.com
 *   2. Configurar webhook no painel: URL = https://<sua-api>/pix/webhook/asaas
 *   3. Implementar {@link #processarEvento} validando a assinatura (header asaas-access-token)
 *   4. Setar ASAAS_API_KEY, PIX_GATEWAY=asaas nas variáveis de ambiente
 *
 * Referência: https://docs.asaas.com/reference/webhook-pagamentos
 */
@Component
@ConditionalOnProperty(name = "pix.gateway", havingValue = "asaas")
public class GatewayAsaas implements GatewayPagamento {

    private static final Logger log = LoggerFactory.getLogger(GatewayAsaas.class);

    private final DoacaoService doacaoService;

    public GatewayAsaas(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    @Override
    public String getNome() {
        return "asaas";
    }

    @Override
    public boolean usaProcessamentoPeriodico() {
        // Asaas notifica via webhook — não há necessidade de polling
        return false;
    }

    @Override
    public void processarConfirmacoesPendentes() {
        // No-op: confirmações chegam via POST /pix/webhook/asaas
    }

    /**
     * Processa um evento de webhook recebido do Asaas.
     *
     * Eventos relevantes:
     *   PAYMENT_RECEIVED / PAYMENT_CONFIRMED → confirmar doação
     *   PAYMENT_OVERDUE                       → expirar doação
     *
     * @param payload corpo do webhook (JSON deserializado)
     */
    public void processarEvento(Map<String, Object> payload) {
        // TODO: implementar quando integração Asaas for ativada
        //
        // String evento = (String) payload.get("event");
        // Map<?, ?> payment = (Map<?, ?>) payload.get("payment");
        // String externalReference = (String) payment.get("externalReference"); // = idDoacao
        //
        // switch (evento) {
        //     case "PAYMENT_RECEIVED", "PAYMENT_CONFIRMED" ->
        //         doacaoService.confirmarPagamento(Long.parseLong(externalReference));
        //     case "PAYMENT_OVERDUE" ->
        //         doacaoService.expirar(Long.parseLong(externalReference));
        // }
        log.info("[asaas] Evento recebido (integracao pendente): {}", payload.get("event"));
    }
}
