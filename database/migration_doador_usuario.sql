-- Migração: vincula doadores à tabela usuarios
-- Execute este script no banco ANTES de reiniciar a aplicação

-- 1. Adiciona ultimo_login em usuarios
ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS ultimo_login TIMESTAMP;

-- 2. Adiciona 'doador' ao CHECK de perfil em usuarios
ALTER TABLE usuarios
    DROP CONSTRAINT IF EXISTS usuarios_perfil_check;
ALTER TABLE usuarios
    ADD CONSTRAINT usuarios_perfil_check
        CHECK (perfil IN ('admin', 'funcionario', 'voluntario', 'ong', 'doador'));

-- 3. Adiciona id_usuario em doadores (FK para usuarios)
ALTER TABLE doadores
    ADD COLUMN IF NOT EXISTS id_usuario INTEGER UNIQUE REFERENCES usuarios(id_usuario);

-- 4. Remove senha de doadores (autenticação agora é via usuarios)
ALTER TABLE doadores
    DROP COLUMN IF EXISTS senha;

-- 5. Remove ultimo_login de doadores caso tenha sido adicionado antes
ALTER TABLE doadores
    DROP COLUMN IF EXISTS ultimo_login;
