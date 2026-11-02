package com.doacao.apidoacao.config;

import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.model.Usuario;
import com.doacao.apidoacao.repository.OngRepository;
import com.doacao.apidoacao.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Popula as ONGs de demonstracao com dados de instituicoes REAIS de Americana
 * e Santa Barbara d'Oeste (SP), para que o botao "Ver no mapa" da listagem
 * caia no endereco correto em vez de um ponto aleatorio.
 *
 * Como funciona:
 * - cada entrada tem "apelidos": os nomes fantasia antigos (fictícios) que ja
 *   estao no banco. A ONG e localizada pelo CNPJ ou por um desses apelidos,
 *   entao os registros existentes sao ATUALIZADOS no lugar (as campanhas e
 *   doacoes ja vinculadas a eles continuam valendo) em vez de duplicados.
 * - o endereco so e sobrescrito enquanto a ONG ainda estiver com o CEP antigo
 *   (ou sem endereco). Depois que os dados reais entram — ou que a propria ONG
 *   edita o endereco pelo perfil — o seeder nao mexe mais.
 * - cada ONG ganha um usuario de login (perfil "ong") para conseguir editar o
 *   proprio endereco na tela de perfil.
 *
 * Enderecos, telefones e CNPJs vieram de fontes publicas (sites das proprias
 * instituicoes, Mapa das OSC/IPEA e listagens oficiais das prefeituras).
 * Os e-mails de contato e de login sao de demonstracao (@doemais.org.br) —
 * de proposito, para nao atribuir a essas instituicoes um contato que nao e delas.
 */
@Component
@Order(2)
public class OngSeeder implements CommandLineRunner {

    private static final String SENHA_PADRAO = "Mudar@321";

    private final OngRepository ongRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public OngSeeder(OngRepository ongRepository, UsuarioRepository usuarioRepository,
                     BCryptPasswordEncoder passwordEncoder) {
        this.ongRepository = ongRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Dados de uma ONG de demonstracao. */
    private record SeedOng(
            List<String> apelidos,      // nomes fantasia aceitos na hora de localizar o registro
            List<String> cepsAntigos,   // CEPs que este seed ja veio substituir (ficticios ou de versoes anteriores)
            String razaoSocial,
            String nomeFantasia,
            String cnpj,
            String emailContato,
            String telefone,
            String responsavel,
            String endereco,
            String numero,
            String bairro,
            String cidade,
            String estado,
            String cep,
            String areaAtuacao,
            String logoUrl,
            String emailLogin
    ) {}

    private static final List<SeedOng> ONGS = List.of(
            new SeedOng(
                    List.of("APAE Americana"),
                    List.of("13477-390"),
                    "Associação de Pais e Amigos dos Excepcionais de Americana",
                    "APAE Americana",
                    "43.262.708/0001-23",
                    "contato.apae-americana@doemais.org.br",
                    "(19) 2108-9393",
                    "Coordenação Geral",
                    "Rua Abrahim Abraham",
                    "569",
                    "Parque Residencial Nardini",
                    "Americana",
                    "SP",
                    "13468-395",
                    "Inclusão e Deficiência Intelectual",
                    "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?auto=format&fit=crop&w=600&q=80",
                    "apae.americana@doemais.org.br"
            ),
            new SeedOng(
                    List.of("APAE Santa Bárbara", "APAE Santa Bárbara d'Oeste"),
                    List.of("13450-060"),
                    "Associação de Pais e Amigos dos Excepcionais de Santa Bárbara d'Oeste",
                    "APAE Santa Bárbara d'Oeste",
                    "56.729.502/0001-02",
                    "contato.apae-sbo@doemais.org.br",
                    "(19) 3499-1811",
                    "Coordenação Geral",
                    "Avenida Tiradentes",
                    "1580",
                    "Jardim Primavera",
                    "Santa Bárbara d'Oeste",
                    "SP",
                    "13451-001",
                    "Inclusão e Deficiência Intelectual",
                    "https://images.unsplash.com/photo-1559757148-5c350d0d3c56?auto=format&fit=crop&w=600&q=80",
                    "apae.santabarbara@doemais.org.br"
            ),
            new SeedOng(
                    List.of("Lar São Francisco", "Lar dos Velhinhos São Vicente de Paulo"),
                    List.of("13476-020"),
                    "Vila de São Vicente de Paulo de Americana — Obra Unida à Sociedade de São Vicente de Paulo",
                    "Lar dos Velhinhos São Vicente de Paulo",
                    "43.263.821/0003-95",
                    "contato.lardosvelhinhos@doemais.org.br",
                    "(19) 3461-1449",
                    "Coordenação Geral",
                    "Avenida Nove de Julho",
                    "733",
                    "Jardim São Domingos",
                    "Americana",
                    "SP",
                    "13471-140",
                    "Assistência ao Idoso",
                    "https://images.unsplash.com/photo-1576765608866-5b51046452be?auto=format&fit=crop&w=600&q=80",
                    "lar.velhinhos@doemais.org.br"
            ),
            new SeedOng(
                    List.of("Casa do Menor", "SASA — Casa da Criança"),
                    List.of("13478-300", "13469-570"),
                    "Sociedade de Assistência Social de Americana",
                    "SASA — Casa da Criança",
                    "43.251.495/0001-34",
                    "contato.sasa@doemais.org.br",
                    "(19) 3461-1708",
                    "Coordenação Geral",
                    "Rua Guanabara",
                    "1",
                    "Chácara Machadinho I",
                    "Americana",
                    "SP",
                    "13478-100",
                    "Proteção à Criança e ao Adolescente",
                    "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?auto=format&fit=crop&w=600&q=80",
                    "sasa.americana@doemais.org.br"
            ),
            new SeedOng(
                    List.of("Banco de Alimentos SBO", "Fundo Social de Americana"),
                    List.of("13455-610"),
                    "Fundo Social de Solidariedade de Americana",
                    "Fundo Social de Americana",
                    null,
                    "contato.fundosocial@doemais.org.br",
                    "(19) 3405-4772",
                    "Coordenação Geral",
                    "Rua das Poncianas",
                    "930-B",
                    "Jardim São Paulo",
                    "Americana",
                    "SP",
                    "13468-180",
                    "Segurança Alimentar e Combate à Fome",
                    "https://images.unsplash.com/photo-1488459716781-31db52582fe9?auto=format&fit=crop&w=600&q=80",
                    "fundo.social@doemais.org.br"
            ),
            new SeedOng(
                    List.of("Instituto Reciclar", "Cooperativa Juntos Somos Fortes"),
                    List.of("13456-320"),
                    "Cooperativa de Trabalho dos Coletores de Recicláveis Juntos Somos Fortes",
                    "Cooperativa Juntos Somos Fortes",
                    "28.641.268/0001-21",
                    "contato.juntossomosfortes@doemais.org.br",
                    "(19) 2218-2253",
                    "Coordenação Geral",
                    "Rua James Muller Carr",
                    "485",
                    "Parque Industrial Bandeirantes",
                    "Santa Bárbara d'Oeste",
                    "SP",
                    "13457-178",
                    "Meio Ambiente e Reciclagem",
                    "https://images.unsplash.com/photo-1542601906897-ecd85e4f24b5?auto=format&fit=crop&w=600&q=80",
                    "juntos.somos.fortes@doemais.org.br"
            )
    );

    @Override
    @Transactional
    public void run(String... args) {
        List<Ong> existentes = ongRepository.findAll();
        int criadas = 0;
        int atualizadas = 0;

        for (SeedOng seed : ONGS) {
            Ong ong = localizar(existentes, seed).orElse(null);
            boolean nova = ong == null;

            if (nova) {
                ong = new Ong();
            }

            aplicarIdentificacao(ong, seed);

            if (deveAtualizarEndereco(ong, seed)) {
                aplicarEndereco(ong, seed);
                if (!nova) atualizadas++;
            }

            garantirLogin(ong, seed);
            ongRepository.save(ong);

            if (nova) criadas++;
        }

        System.out.println("[OngSeeder] ONGs criadas: " + criadas + " | endereços atualizados: " + atualizadas);
        System.out.println("[OngSeeder] Login das ONGs: e-mails @doemais.org.br, senha " + SENHA_PADRAO);
    }

    /** Acha a ONG pelo CNPJ e, se nao achar, por um dos nomes fantasia aceitos. */
    private Optional<Ong> localizar(List<Ong> existentes, SeedOng seed) {
        if (seed.cnpj() != null) {
            Optional<Ong> porCnpj = existentes.stream()
                    .filter(o -> seed.cnpj().equalsIgnoreCase(o.getCnpj()))
                    .findFirst();
            if (porCnpj.isPresent()) return porCnpj;
        }

        return existentes.stream()
                .filter(o -> seed.apelidos().stream().anyMatch(apelido -> igual(apelido, o.getNomeFantasia())))
                .findFirst();
    }

    private void aplicarIdentificacao(Ong ong, SeedOng seed) {
        ong.setRazaoSocial(seed.razaoSocial());
        ong.setNomeFantasia(seed.nomeFantasia());
        ong.setAreaAtuacao(seed.areaAtuacao());
        ong.setResponsavel(seed.responsavel());
        ong.setTelefone(seed.telefone());

        if (seed.cnpj() != null) ong.setCnpj(seed.cnpj());
        if (ong.getEmail() == null || ong.getEmail().isBlank()) ong.setEmail(seed.emailContato());
        if (ong.getLogoUrl() == null || ong.getLogoUrl().isBlank()) ong.setLogoUrl(seed.logoUrl());
        if (ong.getStatusOng() == null || ong.getStatusOng().isBlank()) ong.setStatusOng("ativa");
    }

    /**
     * So mexe no endereco enquanto ele estiver vazio ou ainda com o CEP ficticio.
     * Assim, um endereco que a propria ONG cadastrou pelo perfil nunca e sobrescrito
     * num restart da API.
     */
    private boolean deveAtualizarEndereco(Ong ong, SeedOng seed) {
        String cepAtual = somenteDigitos(ong.getCep());

        if (cepAtual.isEmpty()) return true;
        if (cepAtual.equals(somenteDigitos(seed.cep()))) return true;
        return seed.cepsAntigos().stream()
                .map(OngSeeder::somenteDigitos)
                .anyMatch(cepAtual::equals);
    }

    private void aplicarEndereco(Ong ong, SeedOng seed) {
        ong.setEndereco(seed.endereco());
        ong.setNumero(seed.numero());
        ong.setComplemento(null);
        ong.setBairro(seed.bairro());
        ong.setCidade(seed.cidade());
        ong.setEstado(seed.estado());
        ong.setCep(somenteDigitos(seed.cep()));
    }

    /** Garante que a ONG tenha um usuario de login para editar o proprio perfil. */
    private void garantirLogin(Ong ong, SeedOng seed) {
        if (ong.getUsuario() != null) return;

        Usuario usuario = usuarioRepository.findByEmail(seed.emailLogin()).orElseGet(() -> {
            Usuario novo = new Usuario();
            novo.setNome(seed.nomeFantasia());
            novo.setEmail(seed.emailLogin());
            novo.setSenha(passwordEncoder.encode(SENHA_PADRAO));
            novo.setPerfil("ong");
            novo.setAtivo(true);
            return usuarioRepository.save(novo);
        });

        ong.setUsuario(usuario);
    }

    private static boolean igual(String a, String b) {
        if (a == null || b == null) return false;
        return a.trim().toLowerCase(Locale.ROOT).equals(b.trim().toLowerCase(Locale.ROOT));
    }

    private static String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }
}
