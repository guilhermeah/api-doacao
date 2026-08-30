package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.CampanhaRequestDTO;
import com.doacao.apidoacao.dto.CampanhaResponseDTO;
import com.doacao.apidoacao.dto.CampanhaResumoDTO;
import com.doacao.apidoacao.dto.OngDashboardDTO;
import com.doacao.apidoacao.dto.OngRequestDTO;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.service.CampanhaService;
import com.doacao.apidoacao.service.FileStorageService;
import com.doacao.apidoacao.service.OngService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ongs")
@Tag(name = "ONGs", description = "Gestão de ONGs e suas campanhas")
public class OngController {

    private final OngService ongService;
    private final CampanhaService campanhaService;
    private final FileStorageService fileStorageService;

    public OngController(OngService ongService, CampanhaService campanhaService, FileStorageService fileStorageService) {
        this.ongService = ongService;
        this.campanhaService = campanhaService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<Ong> listarTodas() {
        return ongService.listarTodas();
    }

    @GetMapping("/{id}")
    public Ong buscarPorId(@PathVariable Long id) {
        return ongService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ong criar(@RequestBody @Valid OngRequestDTO dto) {
        return ongService.criar(dto);
    }

    @PutMapping("/{id}")
    public Ong atualizar(@PathVariable Long id, @RequestBody @Valid OngRequestDTO dto) {
        return ongService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        ongService.deletar(id);
    }

    @GetMapping("/{id}/campanhas")
    @Operation(summary = "Lista as campanhas de uma ONG")
    public List<CampanhaResumoDTO> listarCampanhas(@PathVariable Long id) {
        return campanhaService.listarPorOng(id).stream()
                .map(CampanhaResumoDTO::de)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/campanhas")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma nova campanha para a ONG informada")
    public CampanhaResponseDTO criarCampanha(@PathVariable Long id, @RequestBody @Valid CampanhaRequestDTO dto) {
        dto.setIdOng(id);
        return CampanhaResponseDTO.de(campanhaService.criar(dto));
    }

    @PostMapping("/{id}/foto")
    @Operation(summary = "Faz upload da foto de perfil/logo da ONG")
    public Ong uploadFoto(@PathVariable Long id, @RequestParam("arquivo") MultipartFile arquivo) {
        String url = fileStorageService.salvarFotoOng(id, arquivo);
        return ongService.atualizarFoto(id, url);
    }

    @GetMapping("/{id}/dashboard")
    @Operation(summary = "Retorna os indicadores de campanhas e doações da ONG")
    public OngDashboardDTO dashboard(@PathVariable Long id) {
        return ongService.dashboard(id);
    }
}