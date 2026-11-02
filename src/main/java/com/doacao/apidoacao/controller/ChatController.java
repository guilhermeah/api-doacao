package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chat IA", description = "Assistente de doações powered by Gemini")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(summary = "Envia mensagem para o assistente de doações")
    public Map<String, String> chat(@RequestBody @Valid ChatRequest request) {
        String resposta = chatService.responder(request.idDoador(), request.mensagem());
        return Map.of("resposta", resposta);
    }

    public record ChatRequest(
            @NotNull Long idDoador,
            @NotBlank String mensagem
    ) {}
}
