-- Migração: chave PIX por ONG
-- Execute este script no banco ANTES de reiniciar a aplicação.
-- Sem ele, a API sobe mas quebra em qualquer consulta de ONG
-- (a entidade passa a mapear a coluna chave_pix e ddl-auto=none não a cria).

-- 1. Nova coluna
ALTER TABLE ongs
    ADD COLUMN IF NOT EXISTS chave_pix VARCHAR(100);

-- 2. Semeia as ONGs existentes com a chave que era usada para todo mundo,
--    para nenhuma campanha ficar sem PIX logo após a migração.
--    Troque o valor pela chave real de cada ONG quando tiver.
UPDATE ongs
SET chave_pix = '53055717821'
WHERE chave_pix IS NULL;

-- 3. Conferência
SELECT id_ong, nome_fantasia, chave_pix FROM ongs ORDER BY id_ong;
