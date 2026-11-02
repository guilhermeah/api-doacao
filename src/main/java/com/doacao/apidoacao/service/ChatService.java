package com.doacao.apidoacao.service;

import com.doacao.apidoacao.model.Campanha;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.repository.CampanhaRepository;
import com.doacao.apidoacao.repository.DoacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=";

    private static final String SYSTEM_PROMPT = """
            Você é o assistente virtual do Doe+, uma plataforma de doações para ONGs.

            Seu nome é DoaBot e você ajuda exclusivamente com os seguintes assuntos:
            - Doações realizadas pelo usuário (histórico, valores, status, campanhas apoiadas)
            - Campanhas de doação disponíveis na plataforma (detalhes, metas, ONGs)
            - Como fazer uma doação via PIX
            - Dúvidas sobre o processo de doação

            REGRAS IMPORTANTES:
            - Responda APENAS perguntas relacionadas a doações e campanhas
            - Se o usuário perguntar sobre qualquer outro assunto, recuse educadamente dizendo:
              "Posso ajudar apenas com dúvidas sobre suas doações e campanhas do Doe+. Para outros assuntos, acesse o suporte geral."
            - Seja simpático, direto e use linguagem informal mas respeitosa
            - Use emojis com moderação para tornar a conversa mais amigável
            - Responda sempre em português brasileiro
            - Mantenha as respostas curtas e objetivas (máximo 3 parágrafos)
            """;

    private final DoacaoRepository doacaoRepository;
    private final CampanhaRepository campanhaRepository;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public ChatService(DoacaoRepository doacaoRepository,
                       CampanhaRepository campanhaRepository) {
        this.doacaoRepository = doacaoRepository;
        this.campanhaRepository = campanhaRepository;
        this.restTemplate = new RestTemplate();
    }

    public String responder(Long idDoador, String mensagem) {
        String contexto = montarContexto(idDoador);
        String prompt = contexto + "\n\nPergunta do doador: " + mensagem;

        try {
            return chamarGemini(prompt);
        } catch (Exception e) {
            return "Desculpe, não consegui processar sua pergunta agora. Tente novamente em instantes.";
        }
    }

    private String montarContexto(Long idDoador) {
        StringBuilder ctx = new StringBuilder();
        ctx.append("=== CONTEXTO DO SISTEMA ===\n");

        // Doações do usuário
        List<Doacao> doacoes = doacaoRepository.findByDoadorIdDoador(idDoador);
        if (doacoes.isEmpty()) {
            ctx.append("O usuário ainda não realizou nenhuma doação.\n");
        } else {
            ctx.append("Doações realizadas pelo usuário:\n");
            doacoes.forEach(d -> ctx.append(String.format(
                    "- R$ %.2f para '%s' (status: %s, data: %s)\n",
                    d.getValorTotal() != null ? d.getValorTotal().doubleValue() : 0,
                    d.getCampanha() != null ? d.getCampanha().getTitulo() : "Avulso",
                    d.getStatusDoacao(),
                    d.getDataDoacao() != null ? d.getDataDoacao().toLocalDate() : "—"
            )));
        }

        // Campanhas ativas
        List<Campanha> campanhas = campanhaRepository.findAll().stream()
                .filter(c -> "ativa".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList());

        ctx.append("\nCampanhas ativas na plataforma:\n");
        campanhas.forEach(c -> ctx.append(String.format(
                "- '%s' (%s) — Meta: R$ %.2f, Arrecadado: R$ %.2f, ONG: %s\n",
                c.getTitulo(),
                c.getTipoCampanha(),
                c.getMetaFinanceira() != null ? c.getMetaFinanceira().doubleValue() : 0,
                c.getValorArrecadado() != null ? c.getValorArrecadado().doubleValue() : 0,
                c.getOng() != null ? c.getOng().getNomeFantasia() : "—"
        )));

        ctx.append("=== FIM DO CONTEXTO ===\n");
        return ctx.toString();
    }

    private String chamarGemini(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", SYSTEM_PROMPT))
                ),
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 512
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                GEMINI_URL + geminiApiKey, request, Map.class
        );

        // Extrai o texto da resposta
        var candidates = (List<?>) response.getBody().get("candidates");
        var candidate = (Map<?, ?>) candidates.get(0);
        var content = (Map<?, ?>) candidate.get("content");
        var parts = (List<?>) content.get("parts");
        var part = (Map<?, ?>) parts.get(0);
        return (String) part.get("text");
    }
}
