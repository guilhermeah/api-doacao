package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.NotificacaoResponseDTO;
import com.doacao.apidoacao.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificacoes")
@Tag(name = "Notificações", description = "Gerenciamento de notificações das ONGs")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping("/ong/{idOng}")
    @Operation(summary = "Lista todas as notificações de uma ONG")
    public List<NotificacaoResponseDTO> listar(@PathVariable Long idOng) {
        return notificacaoService.listarPorOng(idOng).stream()
                .map(NotificacaoResponseDTO::new)
                .toList();
    }

    @GetMapping("/ong/{idOng}/nao-lidas/count")
    @Operation(summary = "Retorna a quantidade de notificações não lidas de uma ONG")
    public ResponseEntity<Map<String, Long>> contarNaoLidas(@PathVariable Long idOng) {
        long total = notificacaoService.contarNaoLidas(idOng);
        return ResponseEntity.ok(Map.of("naoLidas", total));
    }

    @PutMapping("/{id}/lida")
    @Operation(summary = "Marca uma notificação como lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        notificacaoService.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/ong/{idOng}/lidas")
    @Operation(summary = "Marca todas as notificações de uma ONG como lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(@PathVariable Long idOng) {
        notificacaoService.marcarTodasComoLidas(idOng);
        return ResponseEntity.noContent().build();
    }
}
