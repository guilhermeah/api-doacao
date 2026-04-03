package com.doacao.apidoacao.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "doacoes")
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDoacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doador", nullable = false)
    private Doador doador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ong", nullable = false)
    private Ong ong;

    private LocalDateTime dataDoacao;

    @Column(length = 20)
    private String tipoDoacao;

    @Column(columnDefinition = "TEXT")
    private String descricaoGeral;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Column(length = 20)
    private String statusDoacao;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @PrePersist
    public void prePersist() {
        this.dataDoacao = LocalDateTime.now();
        if (this.statusDoacao == null || this.statusDoacao.isBlank()) {
            this.statusDoacao = "recebida";
        }
    }

    public Long getIdDoacao() {
        return idDoacao;
    }

    public void setIdDoacao(Long idDoacao) {
        this.idDoacao = idDoacao;
    }

    public Doador getDoador() {
        return doador;
    }

    public void setDoador(Doador doador) {
        this.doador = doador;
    }

    public Ong getOng() {
        return ong;
    }

    public void setOng(Ong ong) {
        this.ong = ong;
    }

    public LocalDateTime getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(LocalDateTime dataDoacao) {
        this.dataDoacao = dataDoacao;
    }

    public String getTipoDoacao() {
        return tipoDoacao;
    }

    public void setTipoDoacao(String tipoDoacao) {
        this.tipoDoacao = tipoDoacao;
    }

    public String getDescricaoGeral() {
        return descricaoGeral;
    }

    public void setDescricaoGeral(String descricaoGeral) {
        this.descricaoGeral = descricaoGeral;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStatusDoacao() {
        return statusDoacao;
    }

    public void setStatusDoacao(String statusDoacao) {
        this.statusDoacao = statusDoacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}