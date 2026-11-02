package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.PixResponseDTO;
import com.doacao.apidoacao.gateway.GatewayAsaas;
import com.doacao.apidoacao.gateway.GatewayPagamento;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.service.DoacaoService;
import com.doacao.apidoacao.service.PixService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/pix")
@Tag(name = "PIX", description = "Geração de QR Code e código Pix Copia e Cola")
public class PixController {

    private static final Logger log = LoggerFactory.getLogger(PixController.class);

    private final PixService pixService;
    private final DoacaoService doacaoService;
    private final GatewayPagamento gateway;

    public PixController(PixService pixService, DoacaoService doacaoService, GatewayPagamento gateway) {
        this.pixService = pixService;
        this.doacaoService = doacaoService;
        this.gateway = gateway;
    }

    @GetMapping("/doacao/{idDoacao}")
    @Operation(summary = "Gera o PIX para uma doação financeira já registrada")
    public PixResponseDTO gerarPixParaDoacao(@PathVariable Long idDoacao) {
        return pixService.gerarPixParaDoacao(idDoacao);
    }

    @GetMapping("/doacao/{idDoacao}/status")
    @Operation(summary = "Consulta o status de pagamento de uma doação PIX (use para polling)")
    public Map<String, Object> statusPagamento(@PathVariable Long idDoacao) {
        Doacao doacao = doacaoService.buscarPorId(idDoacao);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idDoacao", idDoacao);
        response.put("status", doacao.getStatusDoacao());
        response.put("segundosRestantes", doacao.getSegundosRestantes());
        return response;
    }

    @GetMapping("/avulso")
    @Operation(summary = "Gera um PIX avulso com valor informado (sem vínculo com doação)")
    public PixResponseDTO gerarPixAvulso(
            @RequestParam BigDecimal valor,
            @RequestParam(required = false) String descricao) {
        return pixService.gerarPixAvulso(valor, descricao);
    }

    /**
     * Endpoint de webhook para receber notificações de PSPs (Asaas, Mercado Pago, etc.).
     *
     * URL a configurar no painel do PSP: POST https://<api>/pix/webhook/{provider}
     * Providers suportados: asaas
     *
     * Em modo simulado este endpoint ainda existe, mas não faz nada —
     * as confirmações chegam via GatewaySimulado (polling interno).
     */
    @PostMapping("/webhook/{provider}")
    @Operation(summary = "Recebe notificações de pagamento de PSPs externos (Asaas, etc.)")
    public ResponseEntity<Void> receberWebhook(
            @PathVariable String provider,
            @RequestBody Map<String, Object> payload) {

        log.info("[webhook] Evento recebido do provider '{}': evento={}", provider, payload.get("event"));

        if ("asaas".equalsIgnoreCase(provider) && gateway instanceof GatewayAsaas asaas) {
            asaas.processarEvento(payload);
        } else {
            log.warn("[webhook] Provider '{}' nao reconhecido ou gateway ativo e '{}' — evento ignorado",
                    provider, gateway.getNome());
        }

        return ResponseEntity.ok().build();
    }
}
