package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.PixResponseDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.repository.DoacaoRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

@Service
public class PixService {

    @Value("${pix.key}")
    private String pixKey;

    @Value("${pix.merchant.name}")
    private String merchantName;

    @Value("${pix.merchant.city}")
    private String merchantCity;

    private final DoacaoRepository doacaoRepository;

    public PixService(DoacaoRepository doacaoRepository) {
        this.doacaoRepository = doacaoRepository;
    }

    public PixResponseDTO gerarPixParaDoacao(Long idDoacao) {
        Doacao doacao = doacaoRepository.findById(idDoacao)
                .orElseThrow(() -> new ResourceNotFoundException("Doação não encontrada"));

        String tipo = doacao.getTipoDoacao();
        boolean ehFinanceira = "financeira".equalsIgnoreCase(tipo) || "monetaria".equalsIgnoreCase(tipo);
        if (!ehFinanceira) {
            throw new BusinessException("PIX só pode ser gerado para doações financeiras ou monetárias");
        }

        if (doacao.getValorTotal() == null || doacao.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("A doação precisa ter um valor válido para gerar o PIX");
        }

        Ong ong = doacao.getOng() != null
                ? doacao.getOng()
                : (doacao.getCampanha() != null ? doacao.getCampanha().getOng() : null);

        String chave = normalizarChave(ong != null ? ong.getChavePix() : null);
        if (chave == null) {
            // fallback: chave da plataforma, definida em application.properties
            chave = normalizarChave(pixKey);
        }
        if (chave == null) {
            throw new BusinessException(
                    "Esta ONG ainda nao cadastrou uma chave PIX. Peca para ela preencher o campo "
                            + "\"Chave PIX\" no perfil antes de receber doacoes financeiras.");
        }

        String nomeRecebedor = primeiroPreenchido(
                ong != null ? ong.getNomeFantasia() : null,
                ong != null ? ong.getRazaoSocial() : null,
                merchantName);
        String cidadeRecebedor = primeiroPreenchido(
                ong != null ? ong.getCidade() : null,
                merchantCity);

        String txId = "DOA" + idDoacao;
        String emv = buildEmv(chave, nomeRecebedor, cidadeRecebedor, doacao.getValorTotal(), txId);
        String qrCodeBase64 = gerarQrCode(emv);

        return new PixResponseDTO(idDoacao, emv, qrCodeBase64, doacao.getValorTotal(), txId);
    }

    public PixResponseDTO gerarPixAvulso(BigDecimal valor, String descricao) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O valor do PIX deve ser maior que zero");
        }
        String chave = normalizarChave(pixKey);
        if (chave == null) {
            throw new BusinessException("Nenhuma chave PIX configurada em pix.key");
        }
        String txId = "DOACAO" + System.currentTimeMillis();
        String emv = buildEmv(chave, merchantName, merchantCity, valor, txId);
        String qrCodeBase64 = gerarQrCode(emv);

        PixResponseDTO response = new PixResponseDTO(null, emv, qrCodeBase64, valor, txId);
        response.setMensagem(descricao != null ? descricao
                : "PIX gerado com sucesso. Escaneie o QR Code ou use o código Pix Copia e Cola.");
        return response;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Geração do EMV (formato BRCode / QRCPS-MPM do Banco Central)
    // ─────────────────────────────────────────────────────────────────────────

    private String buildEmv(String chavePix, String nomeRecebedor, String cidadeRecebedor,
                           BigDecimal valor, String txId) {
        String nome = sanitizar(nomeRecebedor, 25);
        String cidade = sanitizar(cidadeRecebedor, 15);
        String referencia = sanitizarAlphanumerico(txId, 25);

        String merchantAccount = campo("00", "br.gov.bcb.pix") + campo("01", chavePix);

        String additionalData = campo("05", referencia);

        StringBuilder emv = new StringBuilder();
        emv.append(campo("00", "01"));
        emv.append(campo("01", "11"));           // 11 = estático (chave direta, sem PSP)
        emv.append(campo("26", merchantAccount));
        emv.append(campo("52", "0000"));
        emv.append(campo("53", "986"));          // BRL
        emv.append(campo("54", String.format(Locale.US, "%.2f", valor)));
        emv.append(campo("58", "BR"));
        emv.append(campo("59", nome));
        emv.append(campo("60", cidade));
        emv.append(campo("62", additionalData));
        emv.append("6304");                      // ID do CRC sem valor ainda

        String crc = crc16(emv.toString());
        emv.append(crc);

        return emv.toString();
    }

    private String campo(String id, String valor) {
        return id + String.format("%02d", valor.length()) + valor;
    }

    // CRC16-CCITT (polinômio 0x1021, valor inicial 0xFFFF)
    private String crc16(String texto) {
        int crc = 0xFFFF;
        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                crc = ((crc & 0x8000) != 0) ? (crc << 1) ^ 0x1021 : crc << 1;
            }
        }
        return String.format("%04X", crc & 0xFFFF);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Geração do QR Code em base64
    // ─────────────────────────────────────────────────────────────────────────

    private String gerarQrCode(String conteudo) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(conteudo, BarcodeFormat.QR_CODE, 300, 300, hints);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (WriterException | IOException e) {
            throw new BusinessException("Erro ao gerar QR Code PIX: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilitários de normalização de texto
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devolve a chave PIX no formato que o BR Code exige:
     * CPF/CNPJ so com digitos, telefone com +55 na frente, e-mail em minusculas,
     * chave aleatoria (EVP) como veio. Retorna null quando nao ha chave.
     */
    private String normalizarChave(String bruta) {
        if (bruta == null) return null;
        String chave = bruta.trim();
        if (chave.isEmpty()) return null;

        if (chave.contains("@")) {
            return chave.toLowerCase(Locale.ROOT);
        }
        if (chave.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
            return chave.toLowerCase(Locale.ROOT);
        }

        String digitos = chave.replaceAll("\\D", "");
        if (digitos.isEmpty()) return chave;
        if (chave.startsWith("+")) return "+" + digitos;
        if (digitos.length() == 11 || digitos.length() == 14) return digitos;   // CPF ou CNPJ
        if (digitos.length() == 12 || digitos.length() == 13) return "+" + digitos; // telefone
        return chave;
    }

    private String primeiroPreenchido(String... valores) {
        for (String v : valores) {
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return "N/A";
    }

    private String sanitizar(String texto, int maxLen) {
        if (texto == null) return "N/A";
        String sem = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .replaceAll("\\s+", " ")
                .trim();
        return sem.substring(0, Math.min(sem.length(), maxLen));
    }

    private String sanitizarAlphanumerico(String texto, int maxLen) {
        if (texto == null) return "DOA";
        String sem = texto.replaceAll("[^a-zA-Z0-9]", "");
        return sem.substring(0, Math.min(sem.length(), maxLen));
    }
}
