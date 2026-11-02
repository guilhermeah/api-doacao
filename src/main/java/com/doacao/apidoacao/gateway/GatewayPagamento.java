package com.doacao.apidoacao.gateway;

/**
 * Abstração de gateway de pagamento PIX.
 *
 * Implementações:
 *   - GatewaySimulado  — confirma automaticamente após N segundos (modo demo, pix.gateway=simulado)
 *   - GatewayAsaas     — confirmação via webhook do PSP (pix.gateway=asaas)
 *
 * Para trocar de gateway basta mudar a variável de ambiente PIX_GATEWAY
 * e implementar a nova classe. O restante da aplicação não muda.
 */
public interface GatewayPagamento {

    String getNome();

    /**
     * Indica se este gateway precisa de polling periódico para detectar pagamentos.
     * Gateways reais (Asaas, Mercado Pago) usam webhooks — retornam false.
     * O simulador não tem PSP para notificar — retorna true.
     */
    boolean usaProcessamentoPeriodico();

    /**
     * Processa confirmações pendentes. Chamado periodicamente pelo GatewayScheduler
     * apenas quando {@link #usaProcessamentoPeriodico()} retorna true.
     */
    void processarConfirmacoesPendentes();
}
