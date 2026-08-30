package com.doacao.apidoacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados para criação/atualização de uma campanha de doação")
public class CampanhaRequestDTO {

    @NotNull(message = "O id da ONG é obrigatório")
    @Schema(example = "1")
    private Long idOng;

    @NotBlank(message = "O tipo da campanha é obrigatório")
    @Schema(example = "financeira", description = "financeira, material ou ambas")
    private String tipoCampanha;

    @NotBlank(message = "O título é obrigatório")
    @Schema(example = "Campanha do Agasalho 2026")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    @Schema(example = "Arrecadação de roupas de inverno para famílias em situação de vulnerabilidade")
    private String descricao;

    @Schema(example = "Arrecadar 1000 peças de roupa de frio até o fim do inverno")
    private String objetivo;

    @DecimalMin(value = "0.01", message = "A meta financeira deve ser maior que zero")
    @Schema(example = "10000.00", description = "Obrigatório para campanhas financeiras e ambas")
    private BigDecimal metaFinanceira;

    @Schema(example = "https://exemplo.com/imagens/campanha.jpg")
    private String imagemUrl;

    @Schema(example = "2026-06-01")
    private LocalDate dataInicio;

    @Schema(example = "2026-08-31")
    private LocalDate dataFim;

    @Schema(example = "ativa", description = "ativa, pausada ou encerrada")
    private String status;

    @Schema(example = "Agasalho")
    private String categoria;

    @Schema(example = "Americana - SP")
    private String localizacao;

    public Long getIdOng() {
        return idOng;
    }

    public void setIdOng(Long idOng) {
        this.idOng = idOng;
    }

    public String getTipoCampanha() { return tipoCampanha; }
    public void setTipoCampanha(String tipoCampanha) { this.tipoCampanha = tipoCampanha; }

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
}
