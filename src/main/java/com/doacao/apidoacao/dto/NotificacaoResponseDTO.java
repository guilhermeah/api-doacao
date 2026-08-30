package com.doacao.apidoacao.dto;

import com.doacao.apidoacao.model.Notificacao;

import java.time.LocalDateTime;

public class NotificacaoResponseDTO {

    private Long idNotificacao;
    private Long idDoacao;
    private String titulo;
    private String mensagem;
    private Boolean lida;
    private LocalDateTime dataCriacao;

    public NotificacaoResponseDTO(Notificacao n) {
        this.idNotificacao = n.getIdNotificacao();
        this.idDoacao = n.getDoacao() != null ? n.getDoacao().getIdDoacao() : null;
        this.titulo = n.getTitulo();
        this.mensagem = n.getMensagem();
        this.lida = n.getLida();
        this.dataCriacao = n.getDataCriacao();
    }

    public Long getIdNotificacao() { return idNotificacao; }
    public Long getIdDoacao() { return idDoacao; }
    public String getTitulo() { return titulo; }
    public String getMensagem() { return mensagem; }
    public Boolean getLida() { return lida; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}
