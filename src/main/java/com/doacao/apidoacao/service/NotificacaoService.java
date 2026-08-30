package com.doacao.apidoacao.service;

import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.Notificacao;
import com.doacao.apidoacao.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void criarNotificacaoDoacao(Doacao doacao) {
        String nomeDoador = doacao.getDoador().getNome();
        String tipo = doacao.getTipoDoacao();

        String titulo = "Nova doação recebida!";
        String mensagem = buildMensagem(nomeDoador, tipo, doacao);

        Notificacao notificacao = new Notificacao();
        notificacao.setOng(doacao.getOng());
        notificacao.setDoacao(doacao);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);

        notificacaoRepository.save(notificacao);
    }

    public List<Notificacao> listarPorOng(Long idOng) {
        return notificacaoRepository.findByOngIdOngOrderByDataCriacaoDesc(idOng);
    }

    public long contarNaoLidas(Long idOng) {
        return notificacaoRepository.countByOngIdOngAndLidaFalse(idOng);
    }

    @Transactional
    public void marcarComoLida(Long idNotificacao) {
        Notificacao notificacao = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void marcarTodasComoLidas(Long idOng) {
        List<Notificacao> pendentes = notificacaoRepository.findByOngIdOngOrderByDataCriacaoDesc(idOng)
                .stream()
                .filter(n -> !n.getLida())
                .toList();
        pendentes.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(pendentes);
    }

    private String buildMensagem(String nomeDoador, String tipo, Doacao doacao) {
        if ("financeira".equalsIgnoreCase(tipo)) {
            return String.format("%s realizou uma doação financeira de R$ %.2f para sua ONG.",
                    nomeDoador, doacao.getValorTotal());
        }
        if ("material".equalsIgnoreCase(tipo)) {
            return String.format("%s realizou uma doação de itens materiais para sua ONG.", nomeDoador);
        }
        return String.format("%s realizou uma doação para sua ONG.", nomeDoador);
    }
}
