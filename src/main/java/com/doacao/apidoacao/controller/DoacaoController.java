package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.DoacaoRequestDTO;
import com.doacao.apidoacao.model.CategoriaDoacao;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.DoacaoItem;
import com.doacao.apidoacao.model.ItemDoacao;
import com.doacao.apidoacao.repository.CategoriaDoacaoRepository;
import com.doacao.apidoacao.repository.ItemDoacaoRepository;
import com.doacao.apidoacao.service.DoacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Doações", description = "Gestão de doações financeiras e materiais")
public class DoacaoController {

    private final DoacaoService doacaoService;
    private final CategoriaDoacaoRepository categoriaRepository;
    private final ItemDoacaoRepository itemDoacaoRepository;

    public DoacaoController(DoacaoService doacaoService,
                            CategoriaDoacaoRepository categoriaRepository,
                            ItemDoacaoRepository itemDoacaoRepository) {
        this.doacaoService = doacaoService;
        this.categoriaRepository = categoriaRepository;
        this.itemDoacaoRepository = itemDoacaoRepository;
    }

    // ── Doações ──────────────────────────────────────────────────────────────

    @GetMapping("/doacoes")
    @Operation(summary = "Lista todas as doações")
    public List<Doacao> listarTodas() {
        return doacaoService.listarTodas();
    }

    @GetMapping("/doacoes/{id}")
    @Operation(summary = "Busca uma doação por ID")
    public Doacao buscarPorId(@PathVariable Long id) {
        return doacaoService.buscarPorId(id);
    }

    @GetMapping("/doacoes/{id}/itens")
    @Operation(summary = "Lista os itens de uma doação material")
    public List<DoacaoItem> listarItens(@PathVariable Long id) {
        return doacaoService.listarItensDaDoacao(id);
    }

    @PostMapping("/doacoes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra uma doação financeira ou material")
    public Doacao criar(@RequestBody @Valid DoacaoRequestDTO dto) {
        return doacaoService.criar(dto);
    }

    @PutMapping("/doacoes/{id}")
    @Operation(summary = "Atualiza uma doação")
    public Doacao atualizar(@PathVariable Long id, @RequestBody @Valid DoacaoRequestDTO dto) {
        return doacaoService.atualizar(id, dto);
    }

    @DeleteMapping("/doacoes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove uma doação")
    public void deletar(@PathVariable Long id) {
        doacaoService.deletar(id);
    }

    // ── Categorias ───────────────────────────────────────────────────────────

    @GetMapping("/categorias")
    @Operation(summary = "Lista todas as categorias de itens doáveis")
    public List<CategoriaDoacao> listarCategorias() {
        return categoriaRepository.findAll();
    }

    // ── Itens ────────────────────────────────────────────────────────────────

    @GetMapping("/itens")
    @Operation(summary = "Lista todos os itens doáveis")
    public List<ItemDoacao> listarItens() {
        return itemDoacaoRepository.findAll();
    }

    @GetMapping("/categorias/{id}/itens")
    @Operation(summary = "Lista os itens de uma categoria específica")
    public List<ItemDoacao> listarItensPorCategoria(@PathVariable Long id) {
        CategoriaDoacao categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new com.doacao.apidoacao.exception.ResourceNotFoundException("Categoria não encontrada"));
        return itemDoacaoRepository.findByCategoria(categoria);
    }
}
