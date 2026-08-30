package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.PixResponseDTO;
import com.doacao.apidoacao.service.PixService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/pix")
@Tag(name = "PIX", description = "Geração de QR Code e código Pix Copia e Cola")
public class PixController {

    private final PixService pixService;

    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @GetMapping("/doacao/{idDoacao}")
    @Operation(summary = "Gera o PIX para uma doação financeira já registrada")
    public PixResponseDTO gerarPixParaDoacao(@PathVariable Long idDoacao) {
        return pixService.gerarPixParaDoacao(idDoacao);
    }

    @GetMapping("/avulso")
    @Operation(summary = "Gera um PIX avulso com valor informado (sem vínculo com doação)")
    public PixResponseDTO gerarPixAvulso(
            @RequestParam BigDecimal valor,
            @RequestParam(required = false) String descricao) {
        return pixService.gerarPixAvulso(valor, descricao);
    }
}
