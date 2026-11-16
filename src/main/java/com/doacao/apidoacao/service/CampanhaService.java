package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.CampanhaRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Campanha;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.repository.CampanhaRepository;
import com.doacao.apidoacao.repository.OngRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CampanhaService {

    private static final String STATUS_ENCERRADA = "encerrada";

    private final CampanhaRepository campanhaRepository;
    private final OngRepository ongRepository;

    public CampanhaService(CampanhaRepository campanhaRepository, OngRepository ongRepository) {
        this.campanhaRepository = campanhaRepository;
        this.ongRepository = ongRepository;
    }

    public List<Campanha> listarTodas() {
        return campanhaRepository.findAll();
    }

    public Campanha buscarPorId(Long id) {
        return campanhaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campanha não encontrada"));
    }

    public Campanha criar(CampanhaRequestDTO dto) {
        Ong ong = ongRepository.findById(dto.getIdOng())
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));

        validarDatas(dto);

        Campanha campanha = new Campanha();
        campanha.setOng(ong);
        mapear(dto, campanha);

        return campanhaRepository.save(campanha);
    }

    public Campanha atualizar(Long id, CampanhaRequestDTO dto) {
        Campanha campanha = buscarPorId(id);

        Ong ong = ongRepository.findById(dto.getIdOng())
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));

        validarDatas(dto);

        campanha.setOng(ong);
        mapear(dto, campanha);

        return campanhaRepository.save(campanha);
    }

    public void excluir(Long id) {
        Campanha campanha = buscarPorId(id);
        campanhaRepository.delete(campanha);
    }

    public Campanha encerrarCampanha(Long id) {
        Campanha campanha = buscarPorId(id);
        campanha.setStatus(STATUS_ENCERRADA);
        return campanhaRepository.save(campanha);
    }

    public List<Campanha> listarPorOng(Long idOng) {
        Ong ong = ongRepository.findById(idOng)
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));
        return campanhaRepository.findByOng(ong);
    }

    public List<Campanha> listarAtivas() {
        return campanhaRepository.findByStatus("ativa");
    }

    public void atualizarArrecadacao(Long idCampanha, BigDecimal valorDoacao, boolean novoDoador) {
        Campanha campanha = buscarPorId(idCampanha);

        BigDecimal valor = valorDoacao != null ? valorDoacao : BigDecimal.ZERO;
        campanha.setValorArrecadado(campanha.getValorArrecadado().add(valor));

        if (novoDoador) {
            campanha.setQuantidadeDoadores(campanha.getQuantidadeDoadores() + 1);
        }

        campanhaRepository.save(campanha);
    }

    public Campanha atualizarImagem(Long id, String imagemUrl) {
        Campanha campanha = buscarPorId(id);
        campanha.setImagemUrl(imagemUrl);
        return campanhaRepository.save(campanha);
    }

    public Campanha registrarVisualizacao(Long id) {
        Campanha campanha = buscarPorId(id);
        campanha.setVisualizacoes(campanha.getVisualizacoes() + 1);
        return campanhaRepository.save(campanha);
    }

    public void validarRecebeDoacao(Campanha campanha) {
        if (STATUS_ENCERRADA.equalsIgnoreCase(campanha.getStatus())) {
            throw new BusinessException("Campanha encerrada não pode receber novas doações");
        }
    }

    private void validarDatas(CampanhaRequestDTO dto) {
        if (dto.getDataInicio() != null && dto.getDataFim() != null
                && dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new BusinessException("A data final não pode ser menor que a data inicial");
        }
    }

    private void mapear(CampanhaRequestDTO dto, Campanha campanha) {
        campanha.setTitulo(dto.getTitulo());
        campanha.setDescricao(dto.getDescricao());
        campanha.setObjetivo(dto.getObjetivo());
        campanha.setMetaFinanceira(dto.getMetaFinanceira());
        campanha.setImagemUrl(dto.getImagemUrl());
        campanha.setDataInicio(dto.getDataInicio());
        campanha.setDataFim(dto.getDataFim());
        campanha.setCategoria(dto.getCategoria());
        campanha.setLocalizacao(dto.getLocalizacao());
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            campanha.setStatus(dto.getStatus());
        }
    }
}
