-- =========================================
-- LIMPA O BANCO
-- =========================================
DROP TABLE IF EXISTS comprovantes CASCADE;
DROP TABLE IF EXISTS distribuicao_itens CASCADE;
DROP TABLE IF EXISTS distribuicoes CASCADE;
DROP TABLE IF EXISTS doacao_itens CASCADE;
DROP TABLE IF EXISTS doacoes CASCADE;
DROP TABLE IF EXISTS itens_doacao CASCADE;
DROP TABLE IF EXISTS categorias_doacao CASCADE;
DROP TABLE IF EXISTS beneficiarios CASCADE;
DROP TABLE IF EXISTS ongs CASCADE;
DROP TABLE IF EXISTS doadores CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- =========================================
-- USUARIOS
-- =========================================
CREATE TABLE usuarios (
                          id_usuario INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          nome VARCHAR(100) NOT NULL,
                          email VARCHAR(120) NOT NULL UNIQUE,
                          senha VARCHAR(255) NOT NULL,
                          perfil VARCHAR(20) NOT NULL CHECK (perfil IN ('admin', 'funcionario', 'voluntario')),
                          ativo BOOLEAN DEFAULT TRUE,
                          data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- DOADORES
-- =========================================
CREATE TABLE doadores (
                          id_doador INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          tipo_doador VARCHAR(20) NOT NULL CHECK (tipo_doador IN ('pessoa_fisica', 'empresa')),
                          nome VARCHAR(120) NOT NULL,
                          cpf_cnpj VARCHAR(20) UNIQUE,
                          email VARCHAR(120),
                          telefone VARCHAR(20),
                          endereco VARCHAR(150),
                          numero VARCHAR(20),
                          complemento VARCHAR(80),
                          bairro VARCHAR(80),
                          cidade VARCHAR(80),
                          estado CHAR(2),
                          cep VARCHAR(10),
                          data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- ONGS
-- =========================================
CREATE TABLE ongs (
                      id_ong INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      razao_social VARCHAR(150) NOT NULL,
                      nome_fantasia VARCHAR(120),
                      cnpj VARCHAR(20) UNIQUE,
                      email VARCHAR(120),
                      telefone VARCHAR(20),
                      responsavel VARCHAR(120),
                      endereco VARCHAR(150),
                      numero VARCHAR(20),
                      complemento VARCHAR(80),
                      bairro VARCHAR(80),
                      cidade VARCHAR(80),
                      estado CHAR(2),
                      cep VARCHAR(10),
                      area_atuacao VARCHAR(100),
                      status_ong VARCHAR(20) DEFAULT 'ativa' CHECK (status_ong IN ('ativa', 'inativa')),
                      data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- BENEFICIARIOS
-- =========================================
CREATE TABLE beneficiarios (
                               id_beneficiario INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               tipo_beneficiario VARCHAR(20) NOT NULL CHECK (tipo_beneficiario IN ('pessoa', 'familia')),
                               nome_responsavel VARCHAR(120) NOT NULL,
                               cpf_responsavel VARCHAR(20) UNIQUE,
                               telefone VARCHAR(20),
                               email VARCHAR(120),
                               endereco VARCHAR(150),
                               numero VARCHAR(20),
                               complemento VARCHAR(80),
                               bairro VARCHAR(80),
                               cidade VARCHAR(80),
                               estado CHAR(2),
                               cep VARCHAR(10),
                               quantidade_pessoas INTEGER DEFAULT 1 CHECK (quantidade_pessoas > 0),
                               renda_familiar NUMERIC(10,2),
                               observacoes TEXT,
                               status_beneficiario VARCHAR(20) DEFAULT 'ativo' CHECK (status_beneficiario IN ('ativo', 'inativo')),
                               data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- CATEGORIAS
-- =========================================
CREATE TABLE categorias_doacao (
                                   id_categoria INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   nome VARCHAR(50) NOT NULL UNIQUE,
                                   descricao TEXT
);

-- =========================================
-- ITENS
-- =========================================
CREATE TABLE itens_doacao (
                              id_item INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              id_categoria INTEGER NOT NULL,
                              nome VARCHAR(100) NOT NULL,
                              descricao TEXT,
                              unidade_medida VARCHAR(20) NOT NULL,
                              perecivel BOOLEAN DEFAULT FALSE,
                              FOREIGN KEY (id_categoria) REFERENCES categorias_doacao(id_categoria)
);

-- =========================================
-- DOACOES
-- =========================================
CREATE TABLE doacoes (
                         id_doacao INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         id_doador INTEGER NOT NULL,
                         id_ong INTEGER NOT NULL,
                         data_doacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         tipo_doacao VARCHAR(20),
                         descricao_geral TEXT,
                         valor_total NUMERIC(12,2),
                         status_doacao VARCHAR(20),
                         observacoes TEXT,
                         FOREIGN KEY (id_doador) REFERENCES doadores(id_doador),
                         FOREIGN KEY (id_ong) REFERENCES ongs(id_ong)
);

-- =========================================
-- DOACAO_ITENS
-- =========================================
CREATE TABLE doacao_itens (
                              id_doacao_item INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              id_doacao INTEGER NOT NULL,
                              id_item INTEGER NOT NULL,
                              quantidade NUMERIC(10,2),
                              valor_unitario NUMERIC(10,2),
                              validade DATE,
                              estado_item VARCHAR(30) CHECK (estado_item IN ('novo', 'usado_bom', 'usado_regular')),
                              observacoes TEXT,
                              FOREIGN KEY (id_doacao) REFERENCES doacoes(id_doacao) ON DELETE CASCADE,
                              FOREIGN KEY (id_item) REFERENCES itens_doacao(id_item)
);

-- =========================================
-- DISTRIBUICOES
-- =========================================
CREATE TABLE distribuicoes (
                               id_distribuicao INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               id_doacao INTEGER NOT NULL,
                               id_ong INTEGER NOT NULL,
                               id_beneficiario INTEGER NOT NULL,
                               data_distribuicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               responsavel_entrega VARCHAR(120),
                               status_entrega VARCHAR(20) DEFAULT 'pendente' CHECK (status_entrega IN ('pendente', 'entregue', 'cancelada')),
                               observacoes TEXT,
                               FOREIGN KEY (id_doacao) REFERENCES doacoes(id_doacao),
                               FOREIGN KEY (id_ong) REFERENCES ongs(id_ong),
                               FOREIGN KEY (id_beneficiario) REFERENCES beneficiarios(id_beneficiario)
);

-- =========================================
-- DISTRIBUICAO_ITENS
-- =========================================
CREATE TABLE distribuicao_itens (
                                    id_distribuicao_item INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    id_distribuicao INTEGER NOT NULL,
                                    id_item INTEGER NOT NULL,
                                    quantidade NUMERIC(10,2) NOT NULL CHECK (quantidade > 0),
                                    observacoes TEXT,
                                    FOREIGN KEY (id_distribuicao) REFERENCES distribuicoes(id_distribuicao) ON DELETE CASCADE,
                                    FOREIGN KEY (id_item) REFERENCES itens_doacao(id_item)
);

-- =========================================
-- COMPROVANTES
-- =========================================
CREATE TABLE comprovantes (
                              id_comprovante INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              id_doacao INTEGER,
                              id_distribuicao INTEGER,
                              nome_arquivo VARCHAR(255) NOT NULL,
                              caminho_arquivo VARCHAR(255) NOT NULL,
                              tipo_arquivo VARCHAR(50),
                              data_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (id_doacao) REFERENCES doacoes(id_doacao) ON DELETE CASCADE,
                              FOREIGN KEY (id_distribuicao) REFERENCES distribuicoes(id_distribuicao) ON DELETE CASCADE,
                              CHECK (id_doacao IS NOT NULL OR id_distribuicao IS NOT NULL)
);
