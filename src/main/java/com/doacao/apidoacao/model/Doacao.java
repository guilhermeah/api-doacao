package com.doacao.apidoacao.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "doacoes")
public class Doacao {

    /** Prazo que o doador tem para pagar o PIX de uma doação financeira. */
    public static final int MINUTOS_PARA_PAGAR = 15;

    public static final String STATUS_PENDENTE = "pendente";
    public static final String STATUS_CONFIRMADA = "confirmada";
    public static final String STATUS_EXPIRADA = "expirada";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDoacao;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_doador", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Doador doador;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ong", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ong ong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_campanha")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Campanha campanha;

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

    // GETTERS E SETTERS

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

    public Campanha getCampanha() {
        return campanha;
    }

    public void setCampanha(Campanha campanha) {
        this.campanha = campanha;
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

    // ── Prazo de pagamento (calculado, não fica no banco) ────────────────────
    // A expiração é sempre dataDoacao + MINUTOS_PARA_PAGAR, então não precisa
    // de coluna nova: quem manda é o relógio do servidor.

    @Transient
    public boolean isAguardandoPagamento() {
        return STATUS_PENDENTE.equalsIgnoreCase(statusDoacao);
    }

    /** Momento em que o PIX desta doação expira. Null quando não está pendente. */
    @Transient
    public LocalDateTime getExpiraEm() {
        if (!isAguardandoPagamento() || dataDoacao == null) return null;
        return dataDoacao.plusMinutes(MINUTOS_PARA_PAGAR);
    }

    /** Segundos que faltam para o PIX expirar (0 quando já venceu). Null quando não está pendente. */
    @Transient
    public Long getSegundosRestantes() {
        LocalDateTime expiraEm = getExpiraEm();
        if (expiraEm == null) return null;
        long restantes = java.time.Duration.between(LocalDateTime.now(), expiraEm).getSeconds();
        return restantes > 0 ? restantes : 0L;
    }

    @Transient
    public boolean isPagamentoExpirado() {
        LocalDateTime expiraEm = getExpiraEm();
        return expiraEm != null && LocalDateTime.now().isAfter(expiraEm);
    }
}