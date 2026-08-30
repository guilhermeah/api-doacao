package com.doacao.apidoacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Indicadores gerais da plataforma")
public class DashboardResumoDTO {

    private long totalOngs;
    private long totalDoadores;
    private long totalCampanhas;
    private long campanhasAtivas;
    private long totalDoacoes;
    private BigDecimal valorArrecadado;

    public DashboardResumoDTO(long totalOngs, long totalDoadores, long totalCampanhas,
                               long campanhasAtivas, long totalDoacoes, BigDecimal valorArrecadado) {
        this.totalOngs = totalOngs;
        this.totalDoadores = totalDoadores;
        this.totalCampanhas = totalCampanhas;
        this.campanhasAtivas = campanhasAtivas;
        this.totalDoacoes = totalDoacoes;
        this.valorArrecadado = valorArrecadado;
    }

    public long getTotalOngs() {
        return totalOngs;
    }

    public long getTotalDoadores() {
        return totalDoadores;
    }

    public long getTotalCampanhas() {
        return totalCampanhas;
    }

    public long getCampanhasAtivas() {
        return campanhasAtivas;
    }

    public long getTotalDoacoes() {
        return totalDoacoes;
    }

    public BigDecimal getValorArrecadado() {
        return valorArrecadado;
    }
}
