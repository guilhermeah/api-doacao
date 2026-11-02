package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.DoacaoItemRequestDTO;
import com.doacao.apidoacao.dto.DoacaoRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.*;
import com.doacao.apidoacao.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        boolean ehFinanceira = ehFinanceira(dto.getTipoDoacao());

        Campanha campanha = null;
        if (dto.getIdCampanha() != null) {
            campanha = campanhaService.buscarPorId(dto.getIdCampanha());
            campanhaService.validarRecebeDoacao(campanha);
            campanhaService.validarTipoDoacaoCompativel(campanha, dto.getTipoDoacao());
        }

        Doacao doacao = new Doacao();
        doacao.setDoador(doador);
        doacao.setOng(ong);
        doacao.setCampanha(campanha);
        doacao.setTipoDoacao(dto.getTipoDoacao());
        doacao.setDescricaoGeral(dto.getDescricaoGeral());
        doacao.setValorTotal(dto.getValorTotal());
        // A financeira nasce pendente: só entra no total da campanha depois que o
        // pagamento do PIX for confirmado (ou expira sozinha no prazo).
        doacao.setStatusDoacao(ehFinanceira ? Doacao.STATUS_PENDENTE : dto.getStatusDoacao());
        doacao.setObservacoes(dto.getObservacoes());

        doacao = doacaoRepository.save(doacao);

        if (!ehFinanceira && dto.getItens() != null && !dto.getItens().isEmpty()) {
            salvarItens(doacao, dto.getItens());
        }

        // Recalcula sempre: a financeira nasce pendente e nao entra na soma, mas
        // deixar o recalculo aqui garante que a campanha nunca fique desalinhada
        // da tabela de doacoes, mesmo que o tipo mude no futuro.
        if (campanha != null) {
            doacaoRepository.flush();
            campanhaService.recalcularArrecadacao(campanha.getIdCampanha());
        }

        if (!ehFinanceira) {
            notificar(doacao);
        }

        return doacao;
    }

    /**
     * Confirma que o doador pagou o PIX: aí sim a doação entra no total da
     * campanha e a ONG é notificada.
     */
    @Transactional
    public Doacao confirmarPagamento(Long id) {
        Doacao doacao = buscarPorId(id);

        if (!ehFinanceira(doacao.getTipoDoacao())) {
            throw new BusinessException("Só doações financeiras precisam de confirmação de pagamento");
        }

        // idempotente: clicar duas vezes em "já paguei" não soma duas vezes
        if (Doacao.STATUS_CONFIRMADA.equalsIgnoreCase(doacao.getStatusDoacao())) {
            return doacao;
        }

        if (Doacao.STATUS_EXPIRADA.equalsIgnoreCase(doacao.getStatusDoacao())) {
            throw new BusinessException("O prazo para pagar este PIX expirou. Faça uma nova doação.");
        }

        if (!doacao.isAguardandoPagamento()) {
            throw new BusinessException("Esta doação não está aguardando pagamento");
        }

        if (doacao.isPagamentoExpirado()) {
            doacao.setStatusDoacao(Doacao.STATUS_EXPIRADA);
            doacaoRepository.save(doacao);
            throw new BusinessException("O prazo de " + Doacao.MINUTOS_PARA_PAGAR
                    + " minutos para pagar expirou. Faça uma nova doação.");
        }

        doacao.setStatusDoacao(Doacao.STATUS_CONFIRMADA);
        doacao = doacaoRepository.save(doacao);

        Campanha campanha = doacao.getCampanha();
        if (campanha != null) {
            doacaoRepository.flush();
            campanhaService.recalcularArrecadacao(campanha.getIdCampanha());
        }

        notificar(doacao);

        return doacao;
    }

    /** Marca uma doação pendente como expirada (o contador do PIX zerou). */
    @Transactional
    public Doacao expirar(Long id) {
        Doacao doacao = buscarPorId(id);
        if (doacao.isAguardandoPagamento()) {
            doacao.setStatusDoacao(Doacao.STATUS_EXPIRADA);
            doacao = doacaoRepository.save(doacao);
            doacaoRepository.flush();
            if (doacao.getCampanha() != null) {
                campanhaService.recalcularArrecadacao(doacao.getCampanha().getIdCampanha());
            }
        }
        return doacao;
    }

    /**
     * Rede de segurança: se o doador fechou o navegador, ninguém avisa o back
     * que o contador zerou. Uma vez por minuto as pendentes vencidas caem.
     */
    @Scheduled(fixedDelay = 60_000L)
    @Transactional
    public void expirarPendentesVencidas() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(Doacao.MINUTOS_PARA_PAGAR);
        List<Doacao> vencidas = doacaoRepository.buscarPendentesVencidas(limite);
        if (vencidas.isEmpty()) return;
        vencidas.forEach(d -> d.setStatusDoacao(Doacao.STATUS_EXPIRADA));
        doacaoRepository.saveAll(vencidas);
        doacaoRepository.flush();

        vencidas.stream()
                .map(Doacao::getCampanha)
                .filter(java.util.Objects::nonNull)
                .map(Campanha::getIdCampanha)
                .distinct()
                .forEach(campanhaService::recalcularArrecadacao);
    }

    @Transactional
    public Doacao atualizar(Long id, DoacaoRequestDTO dto) {
        Doacao doacao = buscarPorId(id);

        Doador doador = doadorRepository.findById(dto.getIdDoador())
                .orElseThrow(() -> new ResourceNotFoundException("Doador não encontrado"));

        Ong ong = ongRepository.findById(dto.getIdOng())
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));

        validarTipoValor(dto);

        Campanha campanhaAnterior = doacao.getCampanha();

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
        // status de doação aguardando/expirada só muda pelo fluxo de pagamento
        // (confirmarPagamento / expirar), nunca por um PUT solto
        if (contabilizada(doacao.getStatusDoacao())) {
            doacao.setStatusDoacao(dto.getStatusDoacao());
        }
        doacao.setObservacoes(dto.getObservacoes());

        doacao = doacaoRepository.save(doacao);

        boolean ehFinanceiraUpd = ehFinanceira(dto.getTipoDoacao());
        if (!ehFinanceiraUpd && dto.getItens() != null && !dto.getItens().isEmpty()) {
            doacaoItemRepository.deleteAll(doacaoItemRepository.findByDoacaoIdDoacao(id));
            salvarItens(doacao, dto.getItens());
        }

        // Um PUT pode mudar valor, status ou ate a campanha da doacao. Recalcula
        // as duas pontas: a campanha de onde ela saiu e a campanha onde ela esta.
        doacaoRepository.flush();
        if (campanhaAnterior != null) {
            campanhaService.recalcularArrecadacao(campanhaAnterior.getIdCampanha());
        }
        if (campanha != null && (campanhaAnterior == null
                || !campanha.getIdCampanha().equals(campanhaAnterior.getIdCampanha()))) {
            campanhaService.recalcularArrecadacao(campanha.getIdCampanha());
        }

        return doacao;
    }

    @Transactional
    public void deletar(Long id) {
        Doacao doacao = buscarPorId(id);

        Campanha campanha = doacao.getCampanha();

        // os itens precisam sair antes, senao a FK impede a exclusao da doacao
        doacaoItemRepository.deleteAll(doacaoItemRepository.findByDoacaoIdDoacao(id));
        doacaoRepository.delete(doacao);
        doacaoRepository.flush();

        if (campanha != null) {
            campanhaService.recalcularArrecadacao(campanha.getIdCampanha());
        }
    }

    private boolean ehFinanceira(String tipoDoacao) {
        return "financeira".equalsIgnoreCase(tipoDoacao) || "monetaria".equalsIgnoreCase(tipoDoacao);
    }

    private boolean contabilizada(String status) {
        String s = status == null ? "" : status.toLowerCase();
        return !s.equals(Doacao.STATUS_PENDENTE)
                && !s.equals(Doacao.STATUS_EXPIRADA)
                && !s.equals("cancelada");
    }

    private void notificar(Doacao doacao) {
        try {
            notificacaoService.criarNotificacaoDoacao(doacao);
        } catch (Exception e) {
            // notificação não deve impedir o registro da doação
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
