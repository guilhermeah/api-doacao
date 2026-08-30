package com.doacao.apidoacao.service;

import com.doacao.apidoacao.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final List<String> TIPOS_PERMITIDOS = List.of("image/jpeg", "image/png", "image/webp");
    private static final long TAMANHO_MAXIMO = 10L * 1024 * 1024;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String salvarImagemCampanha(Long idCampanha, MultipartFile arquivo) {
        validar(arquivo);
        String nomeArquivo = "campanha-" + idCampanha + "-" + UUID.randomUUID() + extensao(arquivo);
        salvar(arquivo, "campanhas", nomeArquivo);
        return "/uploads/campanhas/" + nomeArquivo;
    }

    public String salvarFotoOng(Long idOng, MultipartFile arquivo) {
        validar(arquivo);
        String nomeArquivo = "ong-" + idOng + "-" + UUID.randomUUID() + extensao(arquivo);
        salvar(arquivo, "ongs", nomeArquivo);
        return "/uploads/ongs/" + nomeArquivo;
    }

    public String salvarFotoDoador(Long idDoador, MultipartFile arquivo) {
        validar(arquivo);
        String nomeArquivo = "doador-" + idDoador + "-" + UUID.randomUUID() + extensao(arquivo);
        salvar(arquivo, "doadores", nomeArquivo);
        return "/uploads/doadores/" + nomeArquivo;
    }

    private void validar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de imagem foi enviado");
        }
        String tipo = arquivo.getContentType();
        if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo)) {
            throw new BusinessException("Formato de imagem não permitido. Use JPEG, PNG ou WebP.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new BusinessException("A imagem não pode ultrapassar 10MB.");
        }
    }

    private void salvar(MultipartFile arquivo, String subdiretorio, String nomeArquivo) {
        try {
            Path diretorio = Paths.get(uploadDir, subdiretorio);
            Files.createDirectories(diretorio);
            arquivo.transferTo(diretorio.resolve(nomeArquivo));
        } catch (IOException e) {
            throw new BusinessException("Falha ao salvar a imagem");
        }
    }

    private String extensao(MultipartFile arquivo) {
        String nome = StringUtils.cleanPath(arquivo.getOriginalFilename() == null ? "" : arquivo.getOriginalFilename());
        int ponto = nome.lastIndexOf('.');
        return ponto >= 0 ? nome.substring(ponto) : "";
    }
}
