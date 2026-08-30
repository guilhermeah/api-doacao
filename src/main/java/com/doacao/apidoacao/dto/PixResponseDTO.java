package com.doacao.apidoacao.dto;

import java.math.BigDecimal;

public class PixResponseDTO {

    private Long idDoacao;
    private String pixCopiaECola;
    private String qrCodeBase64;
    private BigDecimal valor;
    private String txId;
    private String mensagem;

    public PixResponseDTO() {}

    public PixResponseDTO(Long idDoacao, String pixCopiaECola, String qrCodeBase64,
                          BigDecimal valor, String txId) {
        this.idDoacao = idDoacao;
        this.pixCopiaECola = pixCopiaECola;
        this.qrCodeBase64 = qrCodeBase64;
        this.valor = valor;
        this.txId = txId;
        this.mensagem = "PIX gerado com sucesso. Escaneie o QR Code ou use o código Pix Copia e Cola.";
    }

    public Long getIdDoacao() { return idDoacao; }
    public void setIdDoacao(Long idDoacao) { this.idDoacao = idDoacao; }

    public String getPixCopiaECola() { return pixCopiaECola; }
    public void setPixCopiaECola(String pixCopiaECola) { this.pixCopiaECola = pixCopiaECola; }

    public String getQrCodeBase64() { return qrCodeBase64; }
    public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getTxId() { return txId; }
    public void setTxId(String txId) { this.txId = txId; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
