package com.doacao.apidoacao.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "doacao_itens")
public class DoacaoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDoacaoItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doacao", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "itens"})
    private Doacao doacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_item", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ItemDoacao item;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantidade;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    private LocalDate validade;

    @Column(length = 30)
    private String estadoItem;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    public Long getIdDoacaoItem() { return idDoacaoItem; }
    public void setIdDoacaoItem(Long idDoacaoItem) { this.idDoacaoItem = idDoacaoItem; }

    public Doacao getDoacao() { return doacao; }
    public void setDoacao(Doacao doacao) { this.doacao = doacao; }

    public ItemDoacao getItem() { return item; }
    public void setItem(ItemDoacao item) { this.item = item; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }

    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }

    public String getEstadoItem() { return estadoItem; }
    public void setEstadoItem(String estadoItem) { this.estadoItem = estadoItem; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
