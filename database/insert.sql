-- =========================================
-- INSERTS - USUARIOS
-- =========================================
INSERT INTO usuarios (nome, email, senha, perfil)
VALUES ('Admin Sistema', 'admin@doacao.com', '123456', 'admin'),
       ('João Silva', 'joao@doacao.com', '123456', 'funcionario'),
       ('Maria Souza', 'maria@doacao.com', '123456', 'voluntario'),
       ('Pedro Lima', 'pedro@doacao.com', '123456', 'funcionario'),
       ('Ana Costa', 'ana@doacao.com', '123456', 'voluntario');

-- =========================================
-- INSERTS - DOADORES
-- =========================================
INSERT INTO doadores (tipo_doador, nome, cpf_cnpj, email, telefone, cidade, estado)
VALUES ('pessoa_fisica', 'Carlos Silva', '12345678900', 'carlos@gmail.com', '19999999999', 'Americana', 'SP'),
       ('empresa', 'Supermercado Bom Preço', '11222333000199', 'empresa@gmail.com', '1933333333', 'Campinas', 'SP'),
       ('pessoa_fisica', 'Fernanda Lima', '22233344455', 'fernanda@gmail.com', '19888888888', 'Sumaré', 'SP'),
       ('empresa', 'Padaria Pão Doce', '33445566000188', 'padaria@gmail.com', '1944444444', 'Limeira', 'SP'),
       ('pessoa_fisica', 'Roberto Alves', '55566677788', 'roberto@gmail.com', '19777777777', 'Hortolândia', 'SP');

-- =========================================
-- INSERTS - ONGS
-- =========================================
INSERT INTO ongs (razao_social, nome_fantasia, cnpj, cidade, estado, responsavel)
VALUES ('ONG Esperança LTDA', 'Esperança', '99888777000100', 'Americana', 'SP', 'Maria Oliveira'),
       ('Instituto Vida', 'Vida Melhor', '88776655000100', 'Campinas', 'SP', 'José Santos'),
       ('Casa Solidária', 'Solidária', '77665544000100', 'Sumaré', 'SP', 'Ana Paula'),
       ('Ajuda Já', 'Ajuda Já', '66554433000100', 'Limeira', 'SP', 'Carlos Mendes'),
       ('Projeto Futuro', 'Futuro', '55443322000100', 'Hortolândia', 'SP', 'Fernanda Rocha');

-- =========================================
-- INSERTS - BENEFICIARIOS
-- =========================================
INSERT INTO beneficiarios (tipo_beneficiario, nome_responsavel, cpf_responsavel, cidade, estado, quantidade_pessoas)
VALUES ('familia', 'Ana Souza', '98765432100', 'Americana', 'SP', 4),
       ('pessoa', 'Pedro Lima', '11122233344', 'Campinas', 'SP', 1),
       ('familia', 'Marcos Oliveira', '22233344411', 'Sumaré', 'SP', 5),
       ('pessoa', 'Juliana Alves', '33344455522', 'Limeira', 'SP', 1),
       ('familia', 'Rafael Costa', '44455566633', 'Hortolândia', 'SP', 3);

-- =========================================
-- INSERTS - CATEGORIAS
-- =========================================
INSERT INTO categorias_doacao (nome, descricao)
VALUES ('Alimentos', 'Produtos alimentícios'),
       ('Roupas', 'Vestimentas'),
       ('Financeiro', 'Doações em dinheiro'),
       ('Higiene', 'Produtos de higiene'),
       ('Móveis', 'Itens de casa');

-- =========================================
-- INSERTS - ITENS
-- =========================================
INSERT INTO itens_doacao (id_categoria, nome, unidade_medida, perecivel)
VALUES ((SELECT id_categoria FROM categorias_doacao WHERE nome = 'Alimentos'), 'Arroz', 'kg', TRUE),
       ((SELECT id_categoria FROM categorias_doacao WHERE nome = 'Alimentos'), 'Feijão', 'kg', TRUE),
       ((SELECT id_categoria FROM categorias_doacao WHERE nome = 'Roupas'), 'Camiseta', 'peca', FALSE),
       ((SELECT id_categoria FROM categorias_doacao WHERE nome = 'Higiene'), 'Sabonete', 'unidade', FALSE),
       ((SELECT id_categoria FROM categorias_doacao WHERE nome = 'Financeiro'), 'Dinheiro', 'dinheiro', FALSE);

-- =========================================
-- INSERTS - DOACOES
-- =========================================
INSERT INTO doacoes (id_doador, id_ong, tipo_doacao, valor_total, status_doacao, descricao_geral, observacoes)
VALUES ((SELECT id_doador FROM doadores WHERE cpf_cnpj = '12345678900'),
        (SELECT id_ong FROM ongs WHERE cnpj = '99888777000100'),
        'material',
        NULL,
        'recebida',
        'Doação de alimentos básicos',
        'Entrega realizada no ponto de coleta'),
       ((SELECT id_doador FROM doadores WHERE cpf_cnpj = '11222333000199'),
        (SELECT id_ong FROM ongs WHERE cnpj = '88776655000100'),
        'financeira',
        500.00,
        'recebida',
        'Doação em dinheiro para compra de cestas básicas',
        'Transferência bancária confirmada'),
       ((SELECT id_doador FROM doadores WHERE cpf_cnpj = '22233344455'),
        (SELECT id_ong FROM ongs WHERE cnpj = '77665544000100'),
        'material',
        NULL,
        'triagem',
        'Doação de roupas variadas',
        'Aguardando separação por tamanho'),
       ((SELECT id_doador FROM doadores WHERE cpf_cnpj = '33445566000188'),
        (SELECT id_ong FROM ongs WHERE cnpj = '66554433000100'),
        'material',
        NULL,
        'separada',
        'Doação de produtos de higiene',
        'Itens separados para distribuição'),
       ((SELECT id_doador FROM doadores WHERE cpf_cnpj = '55566677788'),
        (SELECT id_ong FROM ongs WHERE cnpj = '55443322000100'),
        'financeira',
        1000.00,
        'recebida',
        'Doação em dinheiro para apoio geral',
        'Valor disponível para uso da ONG');

-- =========================================
-- INSERTS - DOACAO_ITENS
-- =========================================
INSERT INTO doacao_itens (id_doacao, id_item, quantidade, valor_unitario, validade, estado_item, observacoes)
VALUES ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '12345678900'),
        (SELECT id_item FROM itens_doacao WHERE nome = 'Arroz'),
        10,
        NULL,
        CURRENT_DATE + INTERVAL '180 days',
        'novo',
        'Pacotes de 1kg'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '12345678900'),
        (SELECT id_item FROM itens_doacao WHERE nome = 'Feijão'),
        5,
        NULL,
        CURRENT_DATE + INTERVAL '150 days',
        'novo',
        'Pacotes de 1kg'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '22233344455'),
        (SELECT id_item FROM itens_doacao WHERE nome = 'Camiseta'),
        20,
        NULL,
        NULL,
        'usado_bom',
        'Roupas higienizadas'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '33445566000188'),
        (SELECT id_item FROM itens_doacao WHERE nome = 'Sabonete'),
        15,
        NULL,
        CURRENT_DATE + INTERVAL '365 days',
        'novo',
        'Sabonetes embalados'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '55566677788'),
        (SELECT id_item FROM itens_doacao WHERE nome = 'Dinheiro'),
        1,
        1000.00,
        NULL,
        'novo',
        'Doação financeira');

-- =========================================
-- INSERTS - DISTRIBUICOES
-- =========================================
INSERT INTO distribuicoes (id_doacao, id_ong, id_beneficiario, responsavel_entrega, status_entrega, observacoes)
VALUES ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '12345678900'),
        (SELECT id_ong FROM ongs WHERE cnpj = '99888777000100'),
        (SELECT id_beneficiario FROM beneficiarios WHERE cpf_responsavel = '98765432100'),
        'Maria Oliveira',
        'entregue',
        'Entrega de alimentos realizada'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '11222333000199'),
        (SELECT id_ong FROM ongs WHERE cnpj = '88776655000100'),
        (SELECT id_beneficiario FROM beneficiarios WHERE cpf_responsavel = '11122233344'),
        'José Santos',
        'entregue',
        'Valor destinado ao beneficiário'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '22233344455'),
        (SELECT id_ong FROM ongs WHERE cnpj = '77665544000100'),
        (SELECT id_beneficiario FROM beneficiarios WHERE cpf_responsavel = '22233344411'),
        'Ana Paula',
        'pendente',
        'Aguardando confirmação do recebimento'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '33445566000188'),
        (SELECT id_ong FROM ongs WHERE cnpj = '66554433000100'),
        (SELECT id_beneficiario FROM beneficiarios WHERE cpf_responsavel = '33344455522'),
        'Carlos Mendes',
        'entregue',
        'Kit de higiene entregue'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '55566677788'),
        (SELECT id_ong FROM ongs WHERE cnpj = '55443322000100'),
        (SELECT id_beneficiario FROM beneficiarios WHERE cpf_responsavel = '44455566633'),
        'Fernanda Rocha',
        'pendente',
        'Aguardando liberação de uso do valor');

-- =========================================
-- INSERTS - DISTRIBUICAO_ITENS
-- =========================================
INSERT INTO distribuicao_itens (id_distribuicao, id_item, quantidade, observacoes)
VALUES ((SELECT di.id_distribuicao
         FROM distribuicoes di
                  JOIN beneficiarios b ON b.id_beneficiario = di.id_beneficiario
         WHERE b.cpf_responsavel = '98765432100'),
        (SELECT id_item FROM itens_doacao WHERE nome = 'Arroz'),
        5,
        'Metade da doação distribuída'),
       ((SELECT di.id_distribuicao
         FROM distribuicoes di
                  JOIN beneficiarios b ON b.id_beneficiario = di.id_beneficiario
         WHERE b.cpf_responsavel = '11122233344'),
        (SELECT id_item FROM itens_doacao WHERE nome = 'Dinheiro'),
        1,
        'Repasse financeiro registrado'),
       ((SELECT di.id_distribuicao
         FROM distribuicoes di
                  JOIN beneficiarios b ON b.id_beneficiario = di.id_beneficiario
         WHERE b.cpf_responsavel = '22233344411'),
        (SELECT id_item FROM itens_doacao WHERE nome = 'Camiseta'),
        10,
        'Parte das roupas separadas para a família'),
       ((SELECT di.id_distribuicao
         FROM distribuicoes di
                  JOIN beneficiarios b ON b.id_beneficiario = di.id_beneficiario
         WHERE b.cpf_responsavel = '33344455522'),
        (SELECT id_item FROM itens_doacao WHERE nome = 'Sabonete'),
        5,
        'Kit básico entregue'),
       ((SELECT di.id_distribuicao
         FROM distribuicoes di
                  JOIN beneficiarios b ON b.id_beneficiario = di.id_beneficiario
         WHERE b.cpf_responsavel = '44455566633'),
        (SELECT id_item FROM itens_doacao WHERE nome = 'Feijão'),
        3,
        'Complemento alimentar reservado');

-- =========================================
-- INSERTS - COMPROVANTES
-- =========================================
INSERT INTO comprovantes (id_doacao, nome_arquivo, caminho_arquivo, tipo_arquivo)
VALUES ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '12345678900'),
        'comp1.pdf',
        '/arquivos/comp1.pdf',
        'pdf'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '11222333000199'),
        'comp2.pdf',
        '/arquivos/comp2.pdf',
        'pdf'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '22233344455'),
        'comp3.jpg',
        '/arquivos/comp3.jpg',
        'imagem'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '33445566000188'),
        'comp4.pdf',
        '/arquivos/comp4.pdf',
        'pdf'),
       ((SELECT d.id_doacao
         FROM doacoes d
                  JOIN doadores doa ON doa.id_doador = d.id_doador
         WHERE doa.cpf_cnpj = '55566677788'),
        'comp5.png',
        '/arquivos/comp5.png',
        'imagem');
