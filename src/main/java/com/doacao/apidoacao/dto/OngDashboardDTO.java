package com.doacao.apidoacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Indicadores de uma ONG específica")
public class OngDashboardDTO {

    private Long idOng;
    private String nomeOng;
    private long totalCampanhas;
    private long campanhasAtivas;
    private long totalDoacoes;
    private BigDecimal valorArrecadado;

    public OngDashboardDTO(Long idOng, String nomeOng, long totalCampanhas, long campanhasAtivas,
                            long totalDoacoes, BigDecimal valorArrecadado) {
        this.idOng = idOng;
        this.nomeOng = nomeOng;
        this.totalCampanhas = totalCampanhas;
        this.campanhasAtivas = campanhasAtivas;
        this.totalDoacoes = totalDoacoes;
        this.valorArrecadado = valorArrecadado;
    }

    public Long getIdOng() {
        return idOng;
    }

    public String getNomeOng() {
        return nomeOng;
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
