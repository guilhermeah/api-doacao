package com.doacao.apidoacao.dto;

import com.doacao.apidoacao.model.Campanha;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Detalhes completos de uma campanha")
public class CampanhaResponseDTO {

    private Long idCampanha;
    private Long idOng;
    private String nomeOng;
    private String tipoCampanha;
    private String titulo;
    private String descricao;
    private String objetivo;
    private BigDecimal metaFinanceira;
    private BigDecimal valorArrecadado;
    private String imagemUrl;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status;
    private String categoria;
    private String localizacao;
    private Integer quantidadeDoadores;
    private Long visualizacoes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CampanhaResponseDTO de(Campanha campanha) {
        CampanhaResponseDTO dto = new CampanhaResponseDTO();
        dto.idCampanha = campanha.getIdCampanha();
        dto.idOng = campanha.getOng().getIdOng();
        dto.nomeOng = campanha.getOng().getNomeFantasia() != null
                ? campanha.getOng().getNomeFantasia()
                : campanha.getOng().getRazaoSocial();
        dto.tipoCampanha = campanha.getTipoCampanha();
        dto.titulo = campanha.getTitulo();
        dto.descricao = campanha.getDescricao();
        dto.objetivo = campanha.getObjetivo();
        dto.metaFinanceira = campanha.getMetaFinanceira();
        dto.valorArrecadado = campanha.getValorArrecadado();
        dto.imagemUrl = campanha.getImagemUrl();
        dto.dataInicio = campanha.getDataInicio();
        dto.dataFim = campanha.getDataFim();
        dto.status = campanha.getStatus();
        dto.categoria = campanha.getCategoria();
        dto.localizacao = campanha.getLocalizacao();
        dto.quantidadeDoadores = campanha.getQuantidadeDoadores();
        dto.visualizacoes = campanha.getVisualizacoes();
        dto.createdAt = campanha.getCreatedAt();
        dto.updatedAt = campanha.getUpdatedAt();
        return dto;
    }

    public Long getIdCampanha() {
        return idCampanha;
    }

    public Long getIdOng() {
        return idOng;
    }

    public String getNomeOng() {
        return nomeOng;
    }

    public String getTipoCampanha() { return tipoCampanha; }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public BigDecimal getMetaFinanceira() {
        return metaFinanceira;
    }

    public BigDecimal getValorArrecadado() {
        return valorArrecadado;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public String getStatus() {
        return status;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public Integer getQuantidadeDoadores() {
        return quantidadeDoadores;
    }

    public Long getVisualizacoes() {
        return visualizacoes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
