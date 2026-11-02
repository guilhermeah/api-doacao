package com.doacao.apidoacao.config;

import com.doacao.apidoacao.model.Campanha;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.Doador;
import com.doacao.apidoacao.repository.CampanhaRepository;
import com.doacao.apidoacao.repository.DoacaoRepository;
import com.doacao.apidoacao.repository.DoadorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Fecha a conta das campanhas de demonstracao.
 *
 * O banco veio com um total escrito na mao em campanhas.valor_arrecadado (por
 * exemplo R$ 42.300) sem as doacoes que explicassem esse valor — a tabela
 * doacoes so tinha R$ 24.300. Era por isso que o dashboard e a tela de
 * "Doacoes Recebidas" mostravam numeros diferentes: cada um lia uma fonte.
 *
 * Este seeder cria doacoes de demonstracao cobrindo exatamente a diferenca, de
 * modo que o total continue o mesmo e passe a ter lastro em registros reais.
 * Depois dele, o ArrecadacaoReconciler recalcula tudo e as duas telas batem.
 *
 * E idempotente: rodou uma vez, a diferenca vira zero e ele nao faz mais nada.
 */
@Component
@Order(3)
public class DoacaoComplementarSeeder implements CommandLineRunner {

    /** Diferencas menores que um centavo sao ruido de arredondamento. */
    private static final BigDecimal TOLERANCIA = new BigDecimal("0.01");

    /** Quantas doacoes no maximo criar por campanha, para a tabela nao virar spam. */
    private static final int MAX_DOACOES_POR_CAMPANHA = 12;

    /** Pesos do rateio: doacoes grandes primeiro, cauda de doacoes pequenas. */
    private static final double[] PESOS = {0.24, 0.18, 0.14, 0.11, 0.09, 0.07, 0.05, 0.04, 0.03, 0.02, 0.02, 0.01};

    private static final String[][] DOADORES_DEMO = {
            {"pessoa_fisica", "Ana Lúcia Prado",        "521.874.630-11", "ana.prado@email.com"},
            {"pessoa_fisica", "Bruno Tadeu Ferraz",     "632.985.741-22", "bruno.ferraz@email.com"},
            {"pessoa_fisica", "Camila Rezende Alves",   "743.096.852-33", "camila.alves@email.com"},
            {"pessoa_fisica", "Diego Martins Rocha",    "854.107.963-44", "diego.rocha@email.com"},
            {"pessoa_fisica", "Elaine Souza Barbosa",   "965.218.074-55", "elaine.barbosa@email.com"},
            {"pessoa_fisica", "Fábio Nogueira Pinto",   "176.329.185-66", "fabio.pinto@email.com"},
            {"pessoa_fisica", "Gabriela Moraes Dias",   "287.430.296-77", "gabriela.dias@email.com"},
            {"pessoa_fisica", "Henrique Castro Lopes",  "398.541.307-88", "henrique.lopes@email.com"},
            {"empresa",       "Tecelagem Nardini ME",   "23.456.789/0001-01", "contato@tecnardini.com.br"},
            {"empresa",       "Mercado Bom Preço Ltda", "34.567.890/0001-12", "contato@bompreco.com.br"},
            {"empresa",       "Metalúrgica Cedros SA",  "45.678.901/0001-23", "contato@metcedros.com.br"},
            {"empresa",       "Padaria Nove de Julho",  "56.789.012/0001-34", "contato@padaria9julho.com.br"},
    };

    private final CampanhaRepository campanhaRepository;
    private final DoacaoRepository doacaoRepository;
    private final DoadorRepository doadorRepository;

    public DoacaoComplementarSeeder(CampanhaRepository campanhaRepository, DoacaoRepository doacaoRepository,
                                    DoadorRepository doadorRepository) {
        this.campanhaRepository = campanhaRepository;
        this.doacaoRepository = doacaoRepository;
        this.doadorRepository = doadorRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Campanha> campanhas = campanhaRepository.findAll();
        if (campanhas.isEmpty()) return;

        List<Doador> pool = null;
        int criadas = 0;
        BigDecimal totalCoberto = BigDecimal.ZERO;

        for (Campanha campanha : campanhas) {
            BigDecimal alvo = campanha.getValorArrecadado() != null ? campanha.getValorArrecadado() : BigDecimal.ZERO;
            BigDecimal real = doacaoRepository.somarValorArrecadadoPorCampanha(campanha);
            if (real == null) real = BigDecimal.ZERO;

            BigDecimal diferenca = alvo.subtract(real);
            if (diferenca.compareTo(TOLERANCIA) < 0) continue;

            if (pool == null) pool = garantirDoadores();
            if (pool.isEmpty()) {
                System.out.println("[DoacaoComplementarSeeder] Sem doadores no banco; nada a fazer.");
                return;
            }

            List<BigDecimal> valores = ratear(diferenca, quantidadeDoacoes(campanha));
            for (int i = 0; i < valores.size(); i++) {
                salvarDoacao(campanha, pool.get(i % pool.size()), valores.get(i), i, valores.size());
                criadas++;
            }
            totalCoberto = totalCoberto.add(diferenca);
        }

        if (criadas > 0) {
            System.out.println("[DoacaoComplementarSeeder] " + criadas
                    + " doação(ões) de demonstração criadas, cobrindo R$ " + totalCoberto);
        } else {
            System.out.println("[DoacaoComplementarSeeder] Campanhas já batem com as doações; nada a criar.");
        }
    }

    /** Quantas doacoes criar: perto do numero de doadores que a campanha anuncia, com teto. */
    private int quantidadeDoacoes(Campanha campanha) {
        int anunciados = campanha.getQuantidadeDoadores() != null ? campanha.getQuantidadeDoadores() : 0;
        return Math.max(3, Math.min(MAX_DOACOES_POR_CAMPANHA, anunciados));
    }

    /**
     * Divide o valor em n parcelas que somam EXATAMENTE o total: as n-1 primeiras
     * saem dos pesos, a ultima recebe o que sobrou do arredondamento.
     */
    private List<BigDecimal> ratear(BigDecimal total, int n) {
        List<BigDecimal> partes = new ArrayList<>(n);

        double somaPesos = 0;
        for (int i = 0; i < n; i++) somaPesos += PESOS[i % PESOS.length];

        BigDecimal acumulado = BigDecimal.ZERO;
        for (int i = 0; i < n - 1; i++) {
            BigDecimal parte = total
                    .multiply(BigDecimal.valueOf(PESOS[i % PESOS.length] / somaPesos))
                    .setScale(2, RoundingMode.HALF_UP);
            if (parte.signum() <= 0) parte = new BigDecimal("0.01");
            partes.add(parte);
            acumulado = acumulado.add(parte);
        }

        BigDecimal resto = total.subtract(acumulado).setScale(2, RoundingMode.HALF_UP);
        partes.add(resto.signum() > 0 ? resto : new BigDecimal("0.01"));
        return partes;
    }

    private void salvarDoacao(Campanha campanha, Doador doador, BigDecimal valor, int indice, int total) {
        Doacao doacao = new Doacao();
        doacao.setDoador(doador);
        doacao.setOng(campanha.getOng());
        doacao.setCampanha(campanha);
        doacao.setTipoDoacao("financeira");
        doacao.setDescricaoGeral("Doação financeira via PIX");
        doacao.setValorTotal(valor);
        doacao.setStatusDoacao(Doacao.STATUS_CONFIRMADA);

        doacao = doacaoRepository.save(doacao);

        // Doacao.prePersist() carimba dataDoacao = agora, entao a data de
        // demonstracao so pode ser gravada depois, num segundo save.
        doacao.setDataDoacao(dataDentroDaCampanha(campanha, indice, total));
        doacaoRepository.save(doacao);
    }

    /** Espalha as doacoes ao longo do periodo da campanha, sem cair no futuro. */
    private LocalDateTime dataDentroDaCampanha(Campanha campanha, int indice, int total) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = campanha.getDataInicio() != null ? campanha.getDataInicio() : hoje.minusMonths(6);
        LocalDate fim = campanha.getDataFim() != null && campanha.getDataFim().isBefore(hoje)
                ? campanha.getDataFim()
                : hoje;

        if (fim.isBefore(inicio)) fim = inicio;

        long dias = java.time.temporal.ChronoUnit.DAYS.between(inicio, fim);
        long passo = total > 1 ? (dias * indice) / (total - 1) : dias / 2;

        return inicio.plusDays(passo).atTime(LocalTime.of(9 + (indice % 9), (indice * 7) % 60));
    }

    /** Reaproveita os doadores que ja existem e completa o pool com os de demonstracao. */
    private List<Doador> garantirDoadores() {
        List<Doador> pool = new ArrayList<>(doadorRepository.findAll());

        for (String[] dados : DOADORES_DEMO) {
            if (pool.size() >= DOADORES_DEMO.length) break;
            if (doadorRepository.findByCpfCnpj(dados[2]).isPresent()) continue;

            Doador doador = new Doador();
            doador.setTipoDoador(dados[0]);
            doador.setNome(dados[1]);
            doador.setCpfCnpj(dados[2]);
            doador.setEmail(dados[3]);
            doador.setCidade("Americana");
            doador.setEstado("SP");
            pool.add(doadorRepository.save(doador));
        }

        return pool;
    }
}
