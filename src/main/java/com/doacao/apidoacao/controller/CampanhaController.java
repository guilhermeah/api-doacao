package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.CampanhaItemDoadoDTO;
import com.doacao.apidoacao.dto.CampanhaRequestDTO;
import com.doacao.apidoacao.dto.CampanhaResponseDTO;
import com.doacao.apidoacao.dto.CampanhaResumoDTO;
import com.doacao.apidoacao.model.Campanha;
import com.doacao.apidoacao.service.CampanhaService;
import com.doacao.apidoacao.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/campanhas")
@Tag(name = "Campanhas", description = "Gestão de campanhas de doação das ONGs")
public class CampanhaController {

    private final CampanhaService campanhaService;
    private final FileStorageService fileStorageService;

    public CampanhaController(CampanhaService campanhaService, FileStorageService fileStorageService) {
        this.campanhaService = campanhaService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    @Operation(summary = "Lista todas as campanhas cadastradas")
    public List<CampanhaResumoDTO> listarTodas() {
        return campanhaService.listarTodas().stream()
                .map(CampanhaResumoDTO::de)
                .collect(Collectors.toList());
    }

    @GetMapping("/ativas")
    @Operation(summary = "Lista apenas as campanhas com status ativa")
    public List<CampanhaResumoDTO> listarAtivas() {
        return campanhaService.listarAtivas().stream()
                .map(CampanhaResumoDTO::de)
                .collect(Collectors.toList());
    }

    @GetMapping("/ong/{idOng}")
    @Operation(summary = "Lista as campanhas de uma ONG específica")
    public List<CampanhaResumoDTO> listarPorOng(@PathVariable Long idOng) {
        return campanhaService.listarPorOng(idOng).stream()
                .map(CampanhaResumoDTO::de)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca os detalhes de uma campanha e contabiliza uma visualização")
    public CampanhaResponseDTO buscarPorId(@PathVariable Long id) {
        Campanha campanha = campanhaService.registrarVisualizacao(id);
        return CampanhaResponseDTO.de(campanha);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma nova campanha para uma ONG")
    public CampanhaResponseDTO criar(@RequestBody @Valid CampanhaRequestDTO dto) {
        return CampanhaResponseDTO.de(campanhaService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de uma campanha existente")
    public CampanhaResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid CampanhaRequestDTO dto) {
        return CampanhaResponseDTO.de(campanhaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove uma campanha")
    public void excluir(@PathVariable Long id) {
        campanhaService.excluir(id);
    }

    @PatchMapping("/{id}/encerrar")
    @Operation(summary = "Encerra uma campanha, impedindo o recebimento de novas doações")
    public CampanhaResponseDTO encerrar(@PathVariable Long id) {
        return CampanhaResponseDTO.de(campanhaService.encerrarCampanha(id));
    }

    @GetMapping("/{id}/itens-doados")
    @Operation(summary = "Retorna o total doado de cada item em uma campanha material")
    public List<CampanhaItemDoadoDTO> listarItensDoadosPorCampanha(@PathVariable Long id) {
        return campanhaService.listarItensDoadosPorCampanha(id);
    }

    @PostMapping("/{id}/imagem")
    @Operation(summary = "Faz upload da imagem de capa de uma campanha")
    public CampanhaResponseDTO uploadImagem(@PathVariable Long id, @RequestParam("arquivo") MultipartFile arquivo) {
        String urlImagem = fileStorageService.salvarImagemCampanha(id, arquivo);
        return CampanhaResponseDTO.de(campanhaService.atualizarImagem(id, urlImagem));
    }
}
