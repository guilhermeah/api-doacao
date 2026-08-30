package com.doacao.apidoacao.dto;

import com.doacao.apidoacao.model.Campanha;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Resumo de campanha para listagens")
public class CampanhaResumoDTO {

    private Long idCampanha;
    private String tipoCampanha;
    private String titulo;
    private String imagemUrl;
    private BigDecimal metaFinanceira;
    private BigDecimal valorArrecadado;
    private String status;
    private String categoria;
    private LocalDate dataFim;
    private Integer quantidadeDoadores;

    public static CampanhaResumoDTO de(Campanha campanha) {
        CampanhaResumoDTO dto = new CampanhaResumoDTO();
        dto.idCampanha = campanha.getIdCampanha();
        dto.tipoCampanha = campanha.getTipoCampanha();
        dto.titulo = campanha.getTitulo();
        dto.imagemUrl = campanha.getImagemUrl();
        dto.metaFinanceira = campanha.getMetaFinanceira();
        dto.valorArrecadado = campanha.getValorArrecadado();
        dto.status = campanha.getStatus();
        dto.categoria = campanha.getCategoria();
        dto.dataFim = campanha.getDataFim();
        dto.quantidadeDoadores = campanha.getQuantidadeDoadores();
        return dto;
    }

    public Long getIdCampanha() {
        return idCampanha;
    }

    public String getTipoCampanha() { return tipoCampanha; }

    public String getTitulo() {
        return titulo;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public BigDecimal getMetaFinanceira() {
        return metaFinanceira;
    }

    public BigDecimal getValorArrecadado() {
        return valorArrecadado;
    }

    public String getStatus() {
        return status;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public Integer getQuantidadeDoadores() {
        return quantidadeDoadores;
    }
}
