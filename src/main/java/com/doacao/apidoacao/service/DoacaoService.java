package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.DoacaoItemRequestDTO;
import com.doacao.apidoacao.dto.DoacaoRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.*;
import com.doacao.apidoacao.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final DoadorRepository doadorRepository;
    private final OngRepository ongRepository;
    private final CampanhaService campanhaService;
    private final ItemDoacaoRepository itemDoacaoRepository;
    private final DoacaoItemRepository doacaoItemRepository;
    private final NotificacaoService notificacaoService;

    public DoacaoService(
            DoacaoRepository doacaoRepository,
            DoadorRepository doadorRepository,
            OngRepository ongRepository,
            CampanhaService campanhaService,
            ItemDoacaoRepository itemDoacaoRepository,
            DoacaoItemRepository doacaoItemRepository,
            NotificacaoService notificacaoService
    ) {
        this.doacaoRepository = doacaoRepository;
        this.doadorRepository = doadorRepository;
        this.ongRepository = ongRepository;
        this.campanhaService = campanhaService;
        this.itemDoacaoRepository = itemDoacaoRepository;
        this.doacaoItemRepository = doacaoItemRepository;
        this.notificacaoService = notificacaoService;
    }

    public List<Doacao> listarTodas() {
        return doacaoRepository.findAll();
    }

    public Doacao buscarPorId(Long id) {
        return doacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doação não encontrada"));
    }

    public List<DoacaoItem> listarItensDaDoacao(Long idDoacao) {
        buscarPorId(idDoacao);
        return doacaoItemRepository.findByDoacaoIdDoacao(idDoacao);
    }

    @Transactional
    public Doacao criar(DoacaoRequestDTO dto) {
        Doador doador = doadorRepository.findById(dto.getIdDoador())
                .or(() -> doadorRepository.findByUsuarioIdUsuario(dto.getIdDoador()))
                .orElseThrow(() -> new ResourceNotFoundException("Doador não encontrado"));

        Ong ong = ongRepository.findById(dto.getIdOng())
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));

        validarTipoValor(dto);

        Campanha campanha = null;
        boolean novoDoadorNaCampanha = false;
        if (dto.getIdCampanha() != null) {
            campanha = campanhaService.buscarPorId(dto.getIdCampanha());
            campanhaService.validarRecebeDoacao(campanha);
            campanhaService.validarTipoDoacaoCompativel(campanha, dto.getTipoDoacao());
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

        doacao = doacaoRepository.save(doacao);

        boolean ehFinanceira = "financeira".equalsIgnoreCase(dto.getTipoDoacao())
                || "monetaria".equalsIgnoreCase(dto.getTipoDoacao());
        if (!ehFinanceira && dto.getItens() != null && !dto.getItens().isEmpty()) {
            salvarItens(doacao, dto.getItens());
        }

        if (campanha != null) {
            campanhaService.atualizarArrecadacao(campanha.getIdCampanha(), doacao.getValorTotal(), novoDoadorNaCampanha);
        }

        try {
            notificacaoService.criarNotificacaoDoacao(doacao);
        } catch (Exception e) {
            // notificação não deve impedir o registro da doação
        }

        return doacao;
    }

    @Transactional
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

        doacao = doacaoRepository.save(doacao);

        boolean ehFinanceiraUpd = "financeira".equalsIgnoreCase(dto.getTipoDoacao())
                || "monetaria".equalsIgnoreCase(dto.getTipoDoacao());
        if (!ehFinanceiraUpd && dto.getItens() != null && !dto.getItens().isEmpty()) {
            doacaoItemRepository.deleteAll(doacaoItemRepository.findByDoacaoIdDoacao(id));
            salvarItens(doacao, dto.getItens());
        }

        return doacao;
    }

    @Transactional
    public void deletar(Long id) {
        Doacao doacao = buscarPorId(id);

        Campanha campanha = doacao.getCampanha();
        Doador doador = doacao.getDoador();
        java.math.BigDecimal valorEstornado = doacao.getValorTotal();

        // os itens precisam sair antes, senao a FK impede a exclusao da doacao
        doacaoItemRepository.deleteAll(doacaoItemRepository.findByDoacaoIdDoacao(id));
        doacaoRepository.delete(doacao);
        doacaoRepository.flush();

        if (campanha != null) {
            boolean doadorSemOutrasDoacoes = doador != null
                    && !doacaoRepository.existsByCampanhaAndDoador(campanha, doador);
            campanhaService.estornarArrecadacao(campanha.getIdCampanha(), valorEstornado, doadorSemOutrasDoacoes);
        }
    }

    private void salvarItens(Doacao doacao, List<DoacaoItemRequestDTO> itens) {
        for (DoacaoItemRequestDTO itemDto : itens) {
            ItemDoacao itemDoacao = itemDoacaoRepository.findById(itemDto.getIdItem())
                    .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado: " + itemDto.getIdItem()));

            DoacaoItem doacaoItem = new DoacaoItem();
            doacaoItem.setDoacao(doacao);
            doacaoItem.setItem(itemDoacao);
            doacaoItem.setQuantidade(itemDto.getQuantidade());
            doacaoItem.setValorUnitario(itemDto.getValorUnitario());
            doacaoItem.setValidade(itemDto.getValidade());
            doacaoItem.setEstadoItem(itemDto.getEstadoItem());
            doacaoItem.setObservacoes(itemDto.getObservacoes());
            doacaoItemRepository.save(doacaoItem);
        }
    }

    private void validarTipoValor(DoacaoRequestDTO dto) {
        boolean ehFinanceira = "financeira".equalsIgnoreCase(dto.getTipoDoacao())
                || "monetaria".equalsIgnoreCase(dto.getTipoDoacao());
        boolean ehMaterial = "material".equalsIgnoreCase(dto.getTipoDoacao());
        boolean ehItem = !ehFinanceira && !ehMaterial; // alimentos, roupas, medicamentos, outros

        if ((ehFinanceira) && (dto.getValorTotal() == null || dto.getValorTotal().signum() <= 0)) {
            throw new BusinessException("Doações financeiras precisam ter valor total maior que zero");
        }
        if (ehMaterial && (dto.getItens() == null || dto.getItens().isEmpty())) {
            throw new BusinessException("Doações materiais precisam ter pelo menos um item");
        }
        // tipos de item (alimentos, roupas, etc.) não exigem valorTotal
    }
}
