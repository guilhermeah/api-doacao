package com.doacao.apidoacao.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "campanhas")
public class Campanha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCampanha;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ong", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ong ong;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "TEXT")
    private String objetivo;

    @Column(name = "meta_financeira", precision = 12, scale = 2)
    private BigDecimal metaFinanceira;

    @Column(name = "valor_arrecadado", precision = 12, scale = 2)
    private BigDecimal valorArrecadado;

    @Column(name = "imagem_url", length = 255)
    private String imagemUrl;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "tipo_campanha", length = 20)
    private String tipoCampanha;

    @Column(length = 20)
    private String status;

    @Column(length = 60)
    private String categoria;

    @Column(length = 150)
    private String localizacao;

    @Column(name = "quantidade_doadores")
    private Integer quantidadeDoadores;

    private Long visualizacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.status == null || this.status.isBlank()) {
            this.status = "ativa";
        }
        if (this.tipoCampanha == null || this.tipoCampanha.isBlank()) {
            this.tipoCampanha = "financeira";
        }
        if (this.valorArrecadado == null) {
            this.valorArrecadado = BigDecimal.ZERO;
        }
        if (this.quantidadeDoadores == null) {
            this.quantidadeDoadores = 0;
        }
        if (this.visualizacoes == null) {
            this.visualizacoes = 0L;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // GETTERS E SETTERS

    public Long getIdCampanha() {
        return idCampanha;
    }

    public void setIdCampanha(Long idCampanha) {
        this.idCampanha = idCampanha;
    }

    public Ong getOng() {
        return ong;
    }

    public void setOng(Ong ong) {
        this.ong = ong;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public BigDecimal getMetaFinanceira() {
        return metaFinanceira;
    }

    public void setMetaFinanceira(BigDecimal metaFinanceira) {
        this.metaFinanceira = metaFinanceira;
    }

    public BigDecimal getValorArrecadado() {
        return valorArrecadado;
    }

    public void setValorArrecadado(BigDecimal valorArrecadado) {
        this.valorArrecadado = valorArrecadado;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getTipoCampanha() { return tipoCampanha; }
    public void setTipoCampanha(String tipoCampanha) { this.tipoCampanha = tipoCampanha; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Integer getQuantidadeDoadores() {
        return quantidadeDoadores;
    }

    public void setQuantidadeDoadores(Integer quantidadeDoadores) {
        this.quantidadeDoadores = quantidadeDoadores;
    }

    public Long getVisualizacoes() {
        return visualizacoes;
    }

    public void setVisualizacoes(Long visualizacoes) {
        this.visualizacoes = visualizacoes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
