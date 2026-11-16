package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.DoacaoRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Campanha;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.Doador;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.repository.DoacaoRepository;
import com.doacao.apidoacao.repository.DoadorRepository;
import com.doacao.apidoacao.repository.OngRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final DoadorRepository doadorRepository;
    private final OngRepository ongRepository;
    private final CampanhaService campanhaService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DoacaoService(
            DoacaoRepository doacaoRepository,
            DoadorRepository doadorRepository,
            OngRepository ongRepository,
            CampanhaService campanhaService,
            FileStorageService fileStorageService
    ) {
        this.doacaoRepository = doacaoRepository;
        this.doadorRepository = doadorRepository;
        this.ongRepository = ongRepository;
        this.campanhaService = campanhaService;
        this.fileStorageService = fileStorageService;
    }

    public List<Doacao> listarTodas() {
        return doacaoRepository.findAll();
    }

    public Doacao buscarPorId(Long id) {
        return doacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doação não encontrada"));
    }

    public Doacao criar(DoacaoRequestDTO dto) {
        Doador doador = doadorRepository.findById(dto.getIdDoador())
                .orElseThrow(() -> new ResourceNotFoundException("Doador não encontrado"));

        Ong ong = ongRepository.findById(dto.getIdOng())
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));

        validarTipoValor(dto);

        Campanha campanha = null;
        boolean novoDoadorNaCampanha = false;
        if (dto.getIdCampanha() != null) {
            campanha = campanhaService.buscarPorId(dto.getIdCampanha());
            campanhaService.validarRecebeDoacao(campanha);
            novoDoadorNaCampanha = !doacaoRepository.existsByCampanhaAndDoador(campanha, doador);
        }

        Doacao doacao = new Doacao();
        doacao.setDoador(doador);
        doacao.setOng(ong);
        doacao.setCampanha(campanha);
        doacao.setTipoDoacao(dto.getTipoDoacao());
        doacao.setDescricaoGeral(dto.getDescricaoGeral());
        doacao.setValorTotal(dto.getValorTotal());
        doacao.setStatusDoacao(dto.getStatusDoacao());
        doacao.setObservacoes(dto.getObservacoes());
        doacao.setModalidadeEntrega(dto.getModalidadeEntrega());
        doacao.setEnderecoRetirada(dto.getEnderecoRetirada());
        doacao.setHorarioDisponivel(dto.getHorarioDisponivel());

        doacao = doacaoRepository.save(doacao);

        if (campanha != null) {
            campanhaService.atualizarArrecadacao(campanha.getIdCampanha(), doacao.getValorTotal(), novoDoadorNaCampanha);
        }

        return doacao;
    }

    public Doacao atualizar(Long id, DoacaoRequestDTO dto) {
        Doacao doacao = buscarPorId(id);

        Doador doador = doadorRepository.findById(dto.getIdDoador())
                .orElseThrow(() -> new ResourceNotFoundException("Doador não encontrado"));

        Ong ong = ongRepository.findById(dto.getIdOng())
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));

        validarTipoValor(dto);

        Campanha campanha = null;
        if (dto.getIdCampanha() != null) {
            campanha = campanhaService.buscarPorId(dto.getIdCampanha());
        }

        doacao.setDoador(doador);
        doacao.setOng(ong);
        doacao.setCampanha(campanha);
        doacao.setTipoDoacao(dto.getTipoDoacao());
        doacao.setDescricaoGeral(dto.getDescricaoGeral());
        doacao.setValorTotal(dto.getValorTotal());
        doacao.setStatusDoacao(dto.getStatusDoacao());
        doacao.setObservacoes(dto.getObservacoes());
        doacao.setModalidadeEntrega(dto.getModalidadeEntrega());
        doacao.setEnderecoRetirada(dto.getEnderecoRetirada());
        doacao.setHorarioDisponivel(dto.getHorarioDisponivel());

        return doacaoRepository.save(doacao);
    }

    public void deletar(Long id) {
        Doacao doacao = buscarPorId(id);
        doacaoRepository.delete(doacao);
    }

    public List<Doacao> listarPorOng(Long idOng) {
        Ong ong = ongRepository.findById(idOng)
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));
        return doacaoRepository.findByOng(ong);
    }

    public Doacao adicionarFotos(Long idDoacao, List<MultipartFile> arquivos) {
        Doacao doacao = buscarPorId(idDoacao);
        List<String> fotos = parseFotosUrls(doacao.getFotosUrls());
        for (MultipartFile arquivo : arquivos) {
            String url = fileStorageService.salvarImagemDoacao(idDoacao, arquivo);
            fotos.add(url);
        }
        doacao.setFotosUrls(toJson(fotos));
        return doacaoRepository.save(doacao);
    }

    public Doacao atualizarStatus(Long idDoacao, String novoStatus) {
        Doacao doacao = buscarPorId(idDoacao);
        doacao.setStatusDoacao(novoStatus);
        return doacaoRepository.save(doacao);
    }

    private List<String> parseFotosUrls(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    private String toJson(List<String> lista) {
        try {
            return objectMapper.writeValueAsString(lista);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private void validarTipoValor(DoacaoRequestDTO dto) {
        if ("financeira".equalsIgnoreCase(dto.getTipoDoacao())
                && (dto.getValorTotal() == null || dto.getValorTotal().signum() <= 0)) {
            throw new BusinessException("Doações financeiras precisam ter valor total maior que zero");
        }
    }
}