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
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String salvarImagemCampanha(Long idCampanha, MultipartFile arquivo) {
        return salvar(arquivo, "campanhas", "campanha-" + idCampanha);
    }

    public String salvarImagemDoacao(Long idDoacao, MultipartFile arquivo) {
        return salvar(arquivo, "doacoes", "doacao-" + idDoacao);
    }

    private String salvar(MultipartFile arquivo, String subdir, String prefixo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de imagem foi enviado");
        }

        String extensao = extensaoDoArquivo(arquivo.getOriginalFilename());
        String nomeArquivo = prefixo + "-" + UUID.randomUUID() + extensao;

        try {
            Path diretorio = Paths.get(uploadDir, subdir);
            Files.createDirectories(diretorio);
            Path destino = diretorio.resolve(nomeArquivo);
            arquivo.transferTo(destino);
        } catch (IOException e) {
            throw new BusinessException("Falha ao salvar o arquivo");
        }

        return "/uploads/" + subdir + "/" + nomeArquivo;
    }

    private String extensaoDoArquivo(String nomeOriginal) {
        String nome = StringUtils.cleanPath(nomeOriginal == null ? "" : nomeOriginal);
        int ponto = nome.lastIndexOf('.');
        return ponto >= 0 ? nome.substring(ponto) : "";
    }
}
