package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.ChatRequestDTO;
import com.doacao.apidoacao.service.ChatBotService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatBotService chatBotService;

    public ChatController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody ChatRequestDTO dto) {
        String resposta = chatBotService.responder(dto.getIdDoador(), dto.getMensagem());
        return Map.of("resposta", resposta);
    }
}
