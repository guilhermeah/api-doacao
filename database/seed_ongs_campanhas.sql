-- =========================================
-- SEED: ONGs de Americana e Santa Bárbara d'Oeste
-- Senha de todos os logins de ONG: 123456
--
-- ATENÇÃO: Execute este script no banco "doacao" (PostgreSQL).
-- O script apaga as ONGs existentes antes de reinserir.
-- =========================================


-- =========================================
-- 1. DELETE — limpa dados existentes de ONGs
--    (ordem respeitando as FKs)
-- =========================================
DELETE FROM notificacoes
 WHERE id_ong IN (SELECT id_ong FROM ongs);

DELETE FROM distribuicao_itens
 WHERE id_distribuicao IN (SELECT id_distribuicao FROM distribuicoes);

DELETE FROM distribuicoes
 WHERE id_ong IN (SELECT id_ong FROM ongs);

DELETE FROM doacao_itens
 WHERE id_doacao IN (
     SELECT id_doacao FROM doacoes
      WHERE id_ong IN (SELECT id_ong FROM ongs)
 );

DELETE FROM comprovantes
 WHERE id_doacao IN (
     SELECT id_doacao FROM doacoes
      WHERE id_ong IN (SELECT id_ong FROM ongs)
 );

DELETE FROM doacoes
 WHERE id_ong IN (SELECT id_ong FROM ongs)
    OR id_doador IN (
        SELECT id_doador FROM doadores
         WHERE cpf_cnpj IN (
             '123.456.789-09','234.567.890-12','345.678.901-23',
             '12.345.678/0001-90','456.789.012-34'
         )
    );

DELETE FROM doadores
 WHERE cpf_cnpj IN (
     '123.456.789-09','234.567.890-12','345.678.901-23',
     '12.345.678/0001-90','456.789.012-34'
 );

DELETE FROM campanhas
 WHERE id_ong IN (SELECT id_ong FROM ongs);

DELETE FROM ongs;

DELETE FROM usuarios WHERE perfil = 'ong';


-- =========================================
-- 2. INSERT — usuários de login para cada ONG
--    Perfil: ong | Senha: 123456 (BCrypt)
-- =========================================
INSERT INTO usuarios (nome, email, senha, perfil, ativo) VALUES
(
  'APAE Americana',
  'apae.americana@doacao.com',
  '$2a$10$G67etl85SEi8QXM3W2icBOTdD6CG5fD/Tu3gSc2RuV8qtykPORK8O',
  'ong', TRUE
),
(
  'Lar São Francisco de Americana',
  'lar.saofrancisco@doacao.com',
  '$2a$10$0BO6XUIeodlf7tAekHszkeRNhk3/tveDyTZHen.2WLTu26WzQmWPi',
  'ong', TRUE
),
(
  'Casa do Menor de Americana',
  'casadomenor.americana@doacao.com',
  '$2a$10$1f1xAkgIKunnRYXQ1c9fWeWYR9Kw8nymD8mDaY3aL5C0Iu3ZG3NJu',
  'ong', TRUE
),
(
  'APAE Santa Bárbara d''Oeste',
  'apae.santabarbara@doacao.com',
  '$2a$10$T9OINS.pReApoQpWMrPf4.OW/fVmZKD1L5ERWZtD082KiryIrmySC',
  'ong', TRUE
),
(
  'Banco de Alimentos São Vicente — SBO',
  'bancoalimentos.sbo@doacao.com',
  '$2a$10$e5OvhOfNhcKmHocPtkDBD.ujNIVN1yts9jN/2.VLzCPIe7cxawd92',
  'ong', TRUE
),
(
  'Instituto Reciclar Santa Bárbara',
  'reciclar.sbo@doacao.com',
  '$2a$10$QK/nzD5iM9wr6IvII6h8WuIsmOufNnntFXcETFQIn9p.QSGdWlcuy',
  'ong', TRUE
);


-- =========================================
-- 3. INSERT — ONGs com logo
--    logo_url: URLs absolutas do Unsplash
--    (se o frontend só aceita caminhos relativos,
--     substitua pelos paths /uploads/ongs/<arquivo>)
-- =========================================
INSERT INTO ongs (
    id_usuario, razao_social, nome_fantasia, cnpj, email, telefone,
    responsavel, endereco, numero, bairro, cidade, estado, cep,
    area_atuacao, status_ong, logo_url, chave_pix
)
VALUES

-- 1) APAE Americana
(
  (SELECT id_usuario FROM usuarios WHERE email = 'apae.americana@doacao.com'),
  'Associação de Pais e Amigos dos Excepcionais de Americana',
  'APAE Americana',
  '46.055.551/0001-35',
  'contato@apaeamericana.org.br',
  '(19) 3406-1900',
  'Dra. Ana Paula Ferreira',
  'Rua São José', '320', 'Vila Medon', 'Americana', 'SP', '13477-390',
  'Inclusão e Deficiência Intelectual',
  'ativa',
  'https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?auto=format&fit=crop&w=600&q=80',
  'apae.americana@pix.com'
),

-- 2) Lar São Francisco de Americana
(
  (SELECT id_usuario FROM usuarios WHERE email = 'lar.saofrancisco@doacao.com'),
  'Associação Beneficente Lar São Francisco de Assis de Americana',
  'Lar São Francisco',
  '47.312.089/0001-12',
  'contato@larsaofrancisco.org.br',
  '(19) 3461-2200',
  'Ir. Maria Aparecida Souza',
  'Rua Dom Pedro II', '1050', 'Jardim Glória', 'Americana', 'SP', '13476-020',
  'Assistência ao Idoso',
  'ativa',
  'https://images.unsplash.com/photo-1576765608866-5b51046452be?auto=format&fit=crop&w=600&q=80',
  '47312089000112'
),

-- 3) Casa do Menor de Americana
(
  (SELECT id_usuario FROM usuarios WHERE email = 'casadomenor.americana@doacao.com'),
  'Associação Casa do Menor de Americana',
  'Casa do Menor',
  '51.823.764/0001-44',
  'casadomenor@casadomenoramericana.org.br',
  '(19) 3404-5577',
  'Marcos Antonio Lima',
  'Av. Brasil', '2750', 'Jardim São Paulo', 'Americana', 'SP', '13478-300',
  'Proteção à Criança e ao Adolescente',
  'ativa',
  'https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?auto=format&fit=crop&w=600&q=80',
  '51823764000144'
),

-- 4) APAE Santa Bárbara d'Oeste
(
  (SELECT id_usuario FROM usuarios WHERE email = 'apae.santabarbara@doacao.com'),
  'Associação de Pais e Amigos dos Excepcionais de Santa Bárbara d''Oeste',
  'APAE Santa Bárbara',
  '45.600.067/0001-37',
  'apae@apaesbo.org.br',
  '(19) 3458-1600',
  'Dr. Ricardo Mendes',
  'Rua São Pedro', '480', 'Centro', 'Santa Bárbara d''Oeste', 'SP', '13450-060',
  'Inclusão e Deficiência Intelectual',
  'ativa',
  'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?auto=format&fit=crop&w=600&q=80',
  'apae.sbo@pix.com'
),

-- 5) Banco de Alimentos São Vicente de Paulo — SBO
(
  (SELECT id_usuario FROM usuarios WHERE email = 'bancoalimentos.sbo@doacao.com'),
  'Sociedade São Vicente de Paulo — Banco de Alimentos de Santa Bárbara d''Oeste',
  'Banco de Alimentos SBO',
  '63.917.452/0001-88',
  'bancoalimentos@ssvpsbo.org.br',
  '(19) 3455-8800',
  'Joaquim Pereira',
  'Av. Minas Gerais', '3200', 'Residencial Rio das Pedras', 'Santa Bárbara d''Oeste', 'SP', '13455-610',
  'Segurança Alimentar e Combate à Fome',
  'ativa',
  'https://images.unsplash.com/photo-1488459716781-31db52582fe9?auto=format&fit=crop&w=600&q=80',
  '63917452000188'
),

-- 6) Instituto Reciclar
(
  (SELECT id_usuario FROM usuarios WHERE email = 'reciclar.sbo@doacao.com'),
  'Instituto Reciclar — Educação Ambiental de Santa Bárbara d''Oeste',
  'Instituto Reciclar',
  '72.481.935/0001-06',
  'contato@institutoreciclar.org.br',
  '(19) 3458-4422',
  'Fernanda Castro',
  'Rua das Palmeiras', '95', 'Jardim Europa', 'Santa Bárbara d''Oeste', 'SP', '13456-320',
  'Meio Ambiente e Reciclagem',
  'ativa',
  'https://images.unsplash.com/photo-1542601906897-ecd85e4f24b5?auto=format&fit=crop&w=600&q=80',
  '72481935000106'
);


-- =========================================
-- 4. INSERT — Campanhas por ONG
--    imagem_url: fotos temáticas do Unsplash
--    meta_financeira: valores variados
--    valor_arrecadado: progresso parcial
-- =========================================

-- ---- APAE Americana ----
INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Terapia e Inclusão Social 2025',
    'Ajude a manter o atendimento terapêutico de mais de 300 pessoas com deficiência intelectual em Americana. Cada real doado financia sessões de fisioterapia, fonoaudiologia e psicologia.',
    'Garantir 12 meses de atendimento multidisciplinar sem interrupção.',
    85000.00, 42300.00,
    'https://images.unsplash.com/photo-1576091160550-2173dba999ef?auto=format&fit=crop&w=800&q=80',
    '2025-01-15', '2025-12-15',
    'ativa', 'SAUDE', 'Americana, SP',
    87, 1240
FROM ongs o WHERE o.cnpj = '46.055.551/0001-35';

INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Oficina de Artes e Criatividade',
    'Queremos ampliar nossa oficina de artes, onde alunos com deficiência expressam sentimentos e desenvolvem habilidades motoras por meio da pintura, cerâmica e música.',
    'Comprar materiais, instrumentos musicais e ampliar o espaço físico da oficina.',
    28000.00, 9800.00,
    'https://images.unsplash.com/photo-1513364776144-60967b0f800f?auto=format&fit=crop&w=800&q=80',
    '2025-03-01', '2025-09-30',
    'ativa', 'EDUCACAO', 'Americana, SP',
    34, 520
FROM ongs o WHERE o.cnpj = '46.055.551/0001-35';

INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Esporte Adaptado — Quadra Acessível',
    'Nossa quadra esportiva precisa de reforma completa de acessibilidade para receber os atletas com deficiência que participam do nosso programa de esporte adaptado.',
    'Revitalização da quadra poliesportiva com piso antiderrapante e alargamento das entradas.',
    55000.00, 55000.00,
    'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?auto=format&fit=crop&w=800&q=80',
    '2024-06-01', '2024-12-31',
    'encerrada', 'ESPORTE', 'Americana, SP',
    112, 2100
FROM ongs o WHERE o.cnpj = '46.055.551/0001-35';


-- ---- Lar São Francisco ----
INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Reforma dos Quartos dos Idosos',
    'O Lar São Francisco abriga 68 idosos em situação de vulnerabilidade. Os dormitórios precisam de reforma urgente: pintura, troca de pisos e instalação de camas hospitalares.',
    'Reformar 8 dormitórios garantindo conforto, higiene e segurança para os moradores.',
    120000.00, 67500.00,
    'https://images.unsplash.com/photo-1601925228068-44d180dc5b47?auto=format&fit=crop&w=800&q=80',
    '2025-02-01', '2025-11-30',
    'ativa', 'SOCIAL', 'Americana, SP',
    156, 3400
FROM ongs o WHERE o.cnpj = '47.312.089/0001-12';

INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Natal dos Idosos 2025',
    'Cada idoso do Lar São Francisco merece um Natal especial. Com sua doação, proporcionamos ceia de Natal, presentes e a alegria de sentir-se lembrado e amado.',
    'Arrecadar recursos para a ceia natalina e presentes personalizados para todos os 68 moradores.',
    15000.00, 3200.00,
    'https://images.unsplash.com/photo-1512389142860-9c449e58a543?auto=format&fit=crop&w=800&q=80',
    '2025-10-01', '2025-12-24',
    'ativa', 'SOCIAL', 'Americana, SP',
    28, 480
FROM ongs o WHERE o.cnpj = '47.312.089/0001-12';


-- ---- Casa do Menor de Americana ----
INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Kit Escolar — Volta às Aulas 2026',
    'Ajude a preparar 180 crianças e adolescentes em situação de vulnerabilidade para o ano letivo de 2026. Cada kit inclui mochila, cadernos, lápis, canetas e material escolar completo.',
    'Distribuir kits escolares completos para todas as crianças atendidas pela Casa do Menor.',
    22500.00, 11800.00,
    'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=800&q=80',
    '2025-10-15', '2026-01-31',
    'ativa', 'EDUCACAO', 'Americana, SP',
    63, 890
FROM ongs o WHERE o.cnpj = '51.823.764/0001-44';

INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Colônia de Férias — Verão Feliz',
    'Durante as férias de verão, as crianças da Casa do Menor têm acesso a atividades esportivas, artísticas e culturais. Sua doação custeia transporte, alimentação e materiais para 5 semanas de programação.',
    'Proporcionar férias seguras, divertidas e educativas para 180 crianças e adolescentes.',
    18000.00, 18000.00,
    'https://images.unsplash.com/photo-1526632518851-deeaa10da28f?auto=format&fit=crop&w=800&q=80',
    '2024-12-01', '2025-02-28',
    'encerrada', 'EDUCACAO', 'Americana, SP',
    75, 1560
FROM ongs o WHERE o.cnpj = '51.823.764/0001-44';


-- ---- APAE Santa Bárbara d'Oeste ----
INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Centro de Convivência e Autonomia',
    'Estamos construindo um Centro de Convivência para que adultos com deficiência intelectual possam desenvolver autonomia por meio de atividades de culinária, jardinagem e arte.',
    'Concluir a construção e equipar o Centro de Convivência para atender 120 pessoas.',
    150000.00, 89000.00,
    'https://images.unsplash.com/photo-1559027615-cd4628902d4a?auto=format&fit=crop&w=800&q=80',
    '2024-09-01', '2025-12-31',
    'ativa', 'SOCIAL', 'Santa Bárbara d''Oeste, SP',
    204, 4200
FROM ongs o WHERE o.cnpj = '45.600.067/0001-37';

INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Fraldas e Higiene para Alunos Dependentes',
    'Vários alunos da APAE SBO dependem de fraldas geriátricas e produtos de higiene pessoal. A doação garante o suprimento mensal e preserva a dignidade desses indivíduos.',
    'Garantir o estoque de fraldas e higiene por 12 meses para todos os alunos dependentes.',
    36000.00, 12400.00,
    'https://images.unsplash.com/photo-1584515933487-779824d29309?auto=format&fit=crop&w=800&q=80',
    '2025-01-01', '2025-12-31',
    'ativa', 'SAUDE', 'Santa Bárbara d''Oeste, SP',
    41, 670
FROM ongs o WHERE o.cnpj = '45.600.067/0001-37';


-- ---- Banco de Alimentos SBO ----
INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Cestas de Natal — 1000 Famílias',
    'Neste Natal, o Banco de Alimentos pretende distribuir 1.000 cestas básicas reforçadas para famílias em situação de insegurança alimentar em Santa Bárbara d''Oeste.',
    'Arrecadar R$ 70.000 para a compra e montagem de 1.000 cestas de Natal com itens natalinos.',
    70000.00, 28900.00,
    'https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=800&q=80',
    '2025-09-01', '2025-12-20',
    'ativa', 'ALIMENTOS', 'Santa Bárbara d''Oeste, SP',
    98, 2300
FROM ongs o WHERE o.cnpj = '63.917.452/0001-88';

INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Galpão de Armazenamento Refrigerado',
    'Para receber e conservar alimentos perecíveis doados por supermercados e distribuidoras, precisamos ampliar nosso galpão com câmaras frias de capacidade industrial.',
    'Adquirir e instalar 3 câmaras frias e ampliar o galpão de triagem em 200 m².',
    95000.00, 51000.00,
    'https://images.unsplash.com/photo-1567361672830-f5e1a9c1a7ba?auto=format&fit=crop&w=800&q=80',
    '2025-04-01', '2025-10-31',
    'pausada', 'ALIMENTOS', 'Santa Bárbara d''Oeste, SP',
    130, 1800
FROM ongs o WHERE o.cnpj = '63.917.452/0001-88';


-- ---- Instituto Reciclar ----
INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Educação Ambiental nas Escolas Públicas',
    'Levamos oficinas de reciclagem, compostagem e consumo consciente para 20 escolas públicas de Santa Bárbara d''Oeste, alcançando mais de 4.000 alunos por semestre.',
    'Financiar os kits didáticos, transporte e material para dois semestres de oficinas nas escolas.',
    32000.00, 14600.00,
    'https://images.unsplash.com/photo-1532996122724-e3de71baf5c5?auto=format&fit=crop&w=800&q=80',
    '2025-02-15', '2025-11-30',
    'ativa', 'MEIO_AMBIENTE', 'Santa Bárbara d''Oeste, SP',
    58, 940
FROM ongs o WHERE o.cnpj = '72.481.935/0001-06';

INSERT INTO campanhas (
    id_ong, tipo_campanha, titulo, descricao, objetivo,
    meta_financeira, valor_arrecadado, imagem_url,
    data_inicio, data_fim, status, categoria, localizacao,
    quantidade_doadores, visualizacoes
)
SELECT
    o.id_ong,
    'financeira',
    'Mutirão de Limpeza — Córrego dos Patos',
    'O Córrego dos Patos está sendo sufocado por resíduos sólidos. Organizamos um mutirão com 200 voluntários para remover os detritos, reflorestar as margens e instalar pontos de coleta seletiva.',
    'Remover o lixo, reflorestar 500 m de margem e instalar 30 pontos de coleta nos arredores.',
    12000.00, 12000.00,
    'https://images.unsplash.com/photo-1542601906897-ecd85e4f24b5?auto=format&fit=crop&w=800&q=80',
    '2025-03-01', '2025-05-31',
    'encerrada', 'MEIO_AMBIENTE', 'Santa Bárbara d''Oeste, SP',
    47, 1120
FROM ongs o WHERE o.cnpj = '72.481.935/0001-06';


-- =========================================
-- 5. INSERT — Doadores de exemplo
-- =========================================
INSERT INTO doadores (tipo_doador, nome, cpf_cnpj, email, telefone, cidade, estado)
VALUES
  ('pessoa_fisica',  'Carlos Eduardo Mendes',      '123.456.789-09', 'carlos.mendes@email.com',   '(19) 99812-3456', 'Americana',              'SP'),
  ('pessoa_fisica',  'Beatriz Almeida Santos',     '234.567.890-12', 'beatriz.santos@email.com',  '(19) 98745-6789', 'Americana',              'SP'),
  ('pessoa_fisica',  'Rafael Oliveira Costa',      '345.678.901-23', 'rafael.costa@email.com',    '(19) 97654-3210', 'Santa Bárbara d''Oeste', 'SP'),
  ('empresa',        'Têxtil Americana Ltda',      '12.345.678/0001-90', 'contato@textilam.com.br','(19) 3461-7700', 'Americana',              'SP'),
  ('pessoa_fisica',  'Márcia Fernandes Lima',      '456.789.012-34', 'marcia.lima@email.com',     '(19) 96543-2109', 'Nova Odessa',            'SP');


-- =========================================
-- 6. INSERT — Doações confirmadas por ONG/campanha
--    status 'confirmada' entra no total do dashboard
-- =========================================

-- ---- APAE Americana — Terapia e Inclusão Social ----
INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 5000.00, 'confirmada', '2025-02-10 10:30:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '123.456.789-09' AND o.cnpj = '46.055.551/0001-35' AND c.titulo = 'Terapia e Inclusão Social 2025' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação corporativa anual', 15000.00, 'confirmada', '2025-03-05 09:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '12.345.678/0001-90' AND o.cnpj = '46.055.551/0001-35' AND c.titulo = 'Terapia e Inclusão Social 2025' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 2500.00, 'confirmada', '2025-04-18 14:20:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '234.567.890-12' AND o.cnpj = '46.055.551/0001-35' AND c.titulo = 'Terapia e Inclusão Social 2025' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 1800.00, 'confirmada', '2025-05-22 11:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '456.789.012-34' AND o.cnpj = '46.055.551/0001-35' AND c.titulo = 'Terapia e Inclusão Social 2025' AND c.id_ong = o.id_ong;

-- ---- APAE Americana — Oficina de Artes ----
INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 3200.00, 'confirmada', '2025-03-15 16:45:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '345.678.901-23' AND o.cnpj = '46.055.551/0001-35' AND c.titulo = 'Oficina de Artes e Criatividade' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 1500.00, 'confirmada', '2025-04-30 08:30:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '123.456.789-09' AND o.cnpj = '46.055.551/0001-35' AND c.titulo = 'Oficina de Artes e Criatividade' AND c.id_ong = o.id_ong;

-- ---- Lar São Francisco — Reforma dos Quartos ----
INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação corporativa — reforma', 20000.00, 'confirmada', '2025-02-20 10:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '12.345.678/0001-90' AND o.cnpj = '47.312.089/0001-12' AND c.titulo = 'Reforma dos Quartos dos Idosos' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 8000.00, 'confirmada', '2025-03-10 13:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '234.567.890-12' AND o.cnpj = '47.312.089/0001-12' AND c.titulo = 'Reforma dos Quartos dos Idosos' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 5000.00, 'confirmada', '2025-04-05 09:30:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '123.456.789-09' AND o.cnpj = '47.312.089/0001-12' AND c.titulo = 'Reforma dos Quartos dos Idosos' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 3200.00, 'confirmada', '2025-05-14 15:10:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '456.789.012-34' AND o.cnpj = '47.312.089/0001-12' AND c.titulo = 'Reforma dos Quartos dos Idosos' AND c.id_ong = o.id_ong;

-- ---- Lar São Francisco — Natal dos Idosos ----
INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 800.00, 'confirmada', '2025-10-08 17:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '345.678.901-23' AND o.cnpj = '47.312.089/0001-12' AND c.titulo = 'Natal dos Idosos 2025' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 500.00, 'confirmada', '2025-10-25 10:45:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '234.567.890-12' AND o.cnpj = '47.312.089/0001-12' AND c.titulo = 'Natal dos Idosos 2025' AND c.id_ong = o.id_ong;

-- ---- Casa do Menor — Kit Escolar ----
INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 2000.00, 'confirmada', '2025-11-02 08:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '123.456.789-09' AND o.cnpj = '51.823.764/0001-44' AND c.titulo = 'Kit Escolar — Volta às Aulas 2026' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 1200.00, 'confirmada', '2025-11-18 12:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '456.789.012-34' AND o.cnpj = '51.823.764/0001-44' AND c.titulo = 'Kit Escolar — Volta às Aulas 2026' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 800.00, 'confirmada', '2025-12-01 14:30:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '345.678.901-23' AND o.cnpj = '51.823.764/0001-44' AND c.titulo = 'Kit Escolar — Volta às Aulas 2026' AND c.id_ong = o.id_ong;

-- ---- APAE Santa Bárbara — Centro de Convivência ----
INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação corporativa — construção', 30000.00, 'confirmada', '2024-10-01 09:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '12.345.678/0001-90' AND o.cnpj = '45.600.067/0001-37' AND c.titulo = 'Centro de Convivência e Autonomia' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 8000.00, 'confirmada', '2024-11-12 11:30:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '123.456.789-09' AND o.cnpj = '45.600.067/0001-37' AND c.titulo = 'Centro de Convivência e Autonomia' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 5000.00, 'confirmada', '2025-01-20 10:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '234.567.890-12' AND o.cnpj = '45.600.067/0001-37' AND c.titulo = 'Centro de Convivência e Autonomia' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 3500.00, 'confirmada', '2025-03-08 16:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '345.678.901-23' AND o.cnpj = '45.600.067/0001-37' AND c.titulo = 'Centro de Convivência e Autonomia' AND c.id_ong = o.id_ong;

-- ---- APAE Santa Bárbara — Fraldas e Higiene ----
INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 1500.00, 'confirmada', '2025-02-15 09:45:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '234.567.890-12' AND o.cnpj = '45.600.067/0001-37' AND c.titulo = 'Fraldas e Higiene para Alunos Dependentes' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 800.00, 'confirmada', '2025-04-10 13:30:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '456.789.012-34' AND o.cnpj = '45.600.067/0001-37' AND c.titulo = 'Fraldas e Higiene para Alunos Dependentes' AND c.id_ong = o.id_ong;

-- ---- Banco de Alimentos SBO — Cestas de Natal ----
INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação corporativa — cestas', 10000.00, 'confirmada', '2025-09-15 10:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '12.345.678/0001-90' AND o.cnpj = '63.917.452/0001-88' AND c.titulo = 'Cestas de Natal — 1000 Famílias' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 3000.00, 'confirmada', '2025-10-02 14:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '123.456.789-09' AND o.cnpj = '63.917.452/0001-88' AND c.titulo = 'Cestas de Natal — 1000 Famílias' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 2500.00, 'confirmada', '2025-10-20 11:15:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '345.678.901-23' AND o.cnpj = '63.917.452/0001-88' AND c.titulo = 'Cestas de Natal — 1000 Famílias' AND c.id_ong = o.id_ong;

-- ---- Instituto Reciclar — Educação Ambiental ----
INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 3000.00, 'confirmada', '2025-03-20 08:30:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '234.567.890-12' AND o.cnpj = '72.481.935/0001-06' AND c.titulo = 'Educação Ambiental nas Escolas Públicas' AND c.id_ong = o.id_ong;

INSERT INTO doacoes (id_doador, id_ong, id_campanha, tipo_doacao, descricao_geral, valor_total, status_doacao, data_doacao)
SELECT d.id_doador, o.id_ong, c.id_campanha, 'financeira', 'Doação financeira via PIX', 1500.00, 'confirmada', '2025-05-05 15:00:00'
FROM doadores d, ongs o, campanhas c
WHERE d.cpf_cnpj = '123.456.789-09' AND o.cnpj = '72.481.935/0001-06' AND c.titulo = 'Educação Ambiental nas Escolas Públicas' AND c.id_ong = o.id_ong;


-- =========================================
-- RESUMO
-- =========================================
-- ONGs inseridas (6):
--   • APAE Americana              → apae.americana@doacao.com
--   • Lar São Francisco           → lar.saofrancisco@doacao.com
--   • Casa do Menor Americana     → casadomenor.americana@doacao.com
--   • APAE Santa Bárbara d'Oeste  → apae.santabarbara@doacao.com
--   • Banco de Alimentos SBO      → bancoalimentos.sbo@doacao.com
--   • Instituto Reciclar SBO      → reciclar.sbo@doacao.com
--
-- Senha de todas: Mudar@321
--
-- Campanhas inseridas (13):
--   • APAE Americana       — 3 campanhas (ativa, ativa, encerrada)
--   • Lar São Francisco    — 2 campanhas (ativa, ativa)
--   • Casa do Menor        — 2 campanhas (ativa, encerrada)
--   • APAE SBO             — 2 campanhas (ativa, ativa)
--   • Banco Alimentos SBO  — 2 campanhas (ativa, pausada)
--   • Instituto Reciclar   — 2 campanhas (ativa, encerrada)
--
-- Metas financeiras: de R$ 12.000 a R$ 150.000
-- Todas as campanhas possuem imagem_url com fotos temáticas do Unsplash
--
-- Doadores inseridos (5):
--   • Carlos Eduardo Mendes    — 123.456.789-09
--   • Beatriz Almeida Santos   — 234.567.890-12
--   • Rafael Oliveira Costa    — 345.678.901-23
--   • Têxtil Americana Ltda    — 12.345.678/0001-90
--   • Márcia Fernandes Lima    — 456.789.012-34
--
-- Doações inseridas: ~27 registros confirmados distribuídos por campanha
--   Status 'confirmada' → entram no total do dashboard e da tela de doações
-- =========================================
