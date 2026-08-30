package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.CampanhaItemDoadoDTO;
import com.doacao.apidoacao.dto.CampanhaRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Campanha;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.repository.CampanhaRepository;
import com.doacao.apidoacao.repository.DoacaoItemRepository;
import com.doacao.apidoacao.repository.OngRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CampanhaService {

    private static final String STATUS_ENCERRADA = "encerrada";

    private final CampanhaRepository campanhaRepository;
    private final OngRepository ongRepository;
    private final DoacaoItemRepository doacaoItemRepository;

    public CampanhaService(CampanhaRepository campanhaRepository, OngRepository ongRepository,
                           DoacaoItemRepository doacaoItemRepository) {
        this.campanhaRepository = campanhaRepository;
        this.ongRepository = ongRepository;
        this.doacaoItemRepository = doacaoItemRepository;
    }

    public List<Campanha> listarTodas() {
        return campanhaRepository.findAll();
    }

    public Campanha buscarPorId(Long id) {
        return campanhaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campanha não encontrada"));
    }

    public Campanha criar(CampanhaRequestDTO dto) {
        Ong ong = buscarOng(dto.getIdOng());

        validarDatas(dto);
        validarTipoCampanha(dto);

        Campanha campanha = new Campanha();
        campanha.setOng(ong);
        mapear(dto, campanha);

        return campanhaRepository.save(campanha);
    }

    public Campanha atualizar(Long id, CampanhaRequestDTO dto) {
        Campanha campanha = buscarPorId(id);

        Ong ong = buscarOng(dto.getIdOng());

        validarDatas(dto);
        validarTipoCampanha(dto);

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

    @Transactional
    public void atualizarArrecadacao(Long idCampanha, BigDecimal valorDoacao, boolean novoDoador) {
        Campanha campanha = buscarPorId(idCampanha);

        BigDecimal valor = valorDoacao != null ? valorDoacao : BigDecimal.ZERO;
        BigDecimal atual = campanha.getValorArrecadado() != null ? campanha.getValorArrecadado() : BigDecimal.ZERO;
        campanha.setValorArrecadado(atual.add(valor));

        int qtdDoadores = campanha.getQuantidadeDoadores() != null ? campanha.getQuantidadeDoadores() : 0;
        if (novoDoador) {
            campanha.setQuantidadeDoadores(qtdDoadores + 1);
        }

        campanhaRepository.save(campanha);
    }

    /**
     * Desfaz na campanha o efeito de uma doacao que foi excluida:
     * subtrai o valor arrecadado (nunca deixando negativo) e, quando o doador
     * nao tem mais nenhuma doacao naquela campanha, tira 1 do contador.
     */
    @Transactional
    public void estornarArrecadacao(Long idCampanha, BigDecimal valorDoacao, boolean removeDoador) {
        Campanha campanha = buscarPorId(idCampanha);

        BigDecimal valor = valorDoacao != null ? valorDoacao : BigDecimal.ZERO;
        BigDecimal atual = campanha.getValorArrecadado() != null ? campanha.getValorArrecadado() : BigDecimal.ZERO;
        BigDecimal novo = atual.subtract(valor);
        campanha.setValorArrecadado(novo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : novo);

        int qtdDoadores = campanha.getQuantidadeDoadores() != null ? campanha.getQuantidadeDoadores() : 0;
        if (removeDoador && qtdDoadores > 0) {
            campanha.setQuantidadeDoadores(qtdDoadores - 1);
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

    public List<CampanhaItemDoadoDTO> listarItensDoadosPorCampanha(Long idCampanha) {
        buscarPorId(idCampanha);
        return doacaoItemRepository.somarItensPorCampanha(idCampanha);
    }

    private Ong buscarOng(Long id) {
        return ongRepository.findById(id)
                .or(() -> ongRepository.findByUsuarioIdUsuario(id))
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));
    }

    public void validarRecebeDoacao(Campanha campanha) {
        if (STATUS_ENCERRADA.equalsIgnoreCase(campanha.getStatus())) {
            throw new BusinessException("Campanha encerrada não pode receber novas doações");
        }
    }

    public void validarTipoDoacaoCompativel(Campanha campanha, String tipoDoacao) {
        String tipo = campanha.getTipoCampanha();
        if (tipo == null || "ambas".equalsIgnoreCase(tipo)) return;
        if ("financeira".equalsIgnoreCase(tipo) && "material".equalsIgnoreCase(tipoDoacao)) {
            throw new BusinessException("Esta campanha só aceita doações financeiras");
        }
        if ("material".equalsIgnoreCase(tipo) && "financeira".equalsIgnoreCase(tipoDoacao)) {
            throw new BusinessException("Esta campanha só aceita doações de itens materiais");
        }
    }

    private void validarDatas(CampanhaRequestDTO dto) {
        if (dto.getDataInicio() != null && dto.getDataFim() != null
                && dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new BusinessException("A data final não pode ser menor que a data inicial");
        }
    }

    private void validarTipoCampanha(CampanhaRequestDTO dto) {
        String tipo = dto.getTipoCampanha() != null ? dto.getTipoCampanha().toLowerCase() : null;
        if (!List.of("financeira", "material", "ambas").contains(tipo)) {
            throw new BusinessException("Tipo de campanha inválido. Use: financeira, material ou ambas");
        }
        if (!"material".equals(tipo) && dto.getMetaFinanceira() == null) {
            throw new BusinessException("A meta financeira é obrigatória para campanhas financeiras ou ambas");
        }
    }

    private void mapear(CampanhaRequestDTO dto, Campanha campanha) {
        campanha.setTipoCampanha(dto.getTipoCampanha());
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
