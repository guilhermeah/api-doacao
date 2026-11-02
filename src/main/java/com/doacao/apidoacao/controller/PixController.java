package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.PixResponseDTO;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.service.DoacaoService;
import com.doacao.apidoacao.service.PixService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/pix")
@Tag(name = "PIX", description = "Geração de QR Code e código Pix Copia e Cola")
public class PixController {

    private final PixService pixService;
    private final DoacaoService doacaoService;

    public PixController(PixService pixService, DoacaoService doacaoService) {
        this.pixService = pixService;
        this.doacaoService = doacaoService;
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
}
