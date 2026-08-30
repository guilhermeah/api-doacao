package com.doacao.apidoacao.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ong", nullable = false)
    private Ong ong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doacao")
    private Doacao doacao;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(nullable = false)
    private Boolean lida = false;

    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        if (this.lida == null) this.lida = false;
    }

    public Long getIdNotificacao() { return idNotificacao; }
    public void setIdNotificacao(Long idNotificacao) { this.idNotificacao = idNotificacao; }

    public Ong getOng() { return ong; }
    public void setOng(Ong ong) { this.ong = ong; }

    public Doacao getDoacao() { return doacao; }
    public void setDoacao(Doacao doacao) { this.doacao = doacao; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public Boolean getLida() { return lida; }
    public void setLida(Boolean lida) { this.lida = lida; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
