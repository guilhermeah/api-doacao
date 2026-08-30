package com.doacao.apidoacao.dto;

import java.math.BigDecimal;

public class CampanhaItemDoadoDTO {

    private Long idItem;
    private String nomeItem;
    private String categoria;
    private String unidadeMedida;
    private BigDecimal totalDoado;

    public CampanhaItemDoadoDTO(Long idItem, String nomeItem, String categoria, String unidadeMedida, BigDecimal totalDoado) {
        this.idItem = idItem;
        this.nomeItem = nomeItem;
        this.categoria = categoria;
        this.unidadeMedida = unidadeMedida;
        this.totalDoado = totalDoado;
    }

    public Long getIdItem() { return idItem; }
    public String getNomeItem() { return nomeItem; }
    public String getCategoria() { return categoria; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public BigDecimal getTotalDoado() { return totalDoado; }
}
