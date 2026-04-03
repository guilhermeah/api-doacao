package com.doacao.apidoacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DoacaoRequestDTO {

    @NotNull(message = "O id do doador é obrigatório")
    private Long idDoador;

    @NotNull(message = "O id da ONG é obrigatório")
    private Long idOng;

    @NotBlank(message = "O tipo da doação é obrigatório")
    private String tipoDoacao;

    private String descricaoGeral;
    private BigDecimal valorTotal;
    private String statusDoacao;
    private String observacoes;

    public Long getIdDoador() {
        return idDoador;
    }

    public void setIdDoador(Long idDoador) {
        this.idDoador = idDoador;
    }

    public Long getIdOng() {
        return idOng;
    }

    public void setIdOng(Long idOng) {
        this.idOng = idOng;
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